pipeline {
    agent any
    parameters {
        booleanParam(name: 'SKIP_PREPARE', defaultValue: true, description: 'Skip the prepare stage')
        booleanParam(name: 'SKIP_PULL', defaultValue: true, description: 'Skip the pull stage')
        booleanParam(name: 'SKIP_BUILD', defaultValue: true, description: 'Skip the build stage')
        booleanParam(name: 'SKIP_PUSH', defaultValue: true, description: 'Skip the push stage')
        booleanParam(name: 'SKIP_DEPLOY', defaultValue: false, description: 'Skip the deploy stage')
    }
    environment {
        K8S_TOKEN = sh(script: 'cat /var/run/secrets/kubernetes.io/serviceaccount/token', returnStdout: true).trim()
        K8S_CA_CERT = '/var/run/secrets/kubernetes.io/serviceaccount/ca.crt'
        GIT_URL = 'https://github.com/awwcmon/school.git'
        GIT_BRANCH = 'main'
        CHART_URL = 'https://awwcmon.github.io/helm-charts'
        CHART_REPO_NAME ='qing'
        APPNAME = 'school'
        RELEASE_NAME = ''
        NAMESPACE = 'default'
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_NAME = 'school'
        IMAGE_TAG = 'latest'
        KUBEPATH = '~/.kube'
        DOCKERPATH = '~/.docker'
        KUBECONFIG_PATH = "$KUBEPATH/kubeconfig.yaml"
        DOCKER_CONFIG_PATH = "$DOCKERPATH/config.json"
        kubeconfig='kubeconfig'
        dockeruserconfig='dockeruserconfig'
    }
    stages {
        stage('prepare'){
            when {
                expression { !params.SKIP_PREPARE }
            }
            steps{
                script{
                    sh '''
                    set -x
                    mkdir -p $DOCKERPATH $KUBEPATH
                    '''
                    withCredentials([file(credentialsId: "${kubeconfig}", variable: 'KUBECONFIG_PATH')]) {
                    withCredentials([file(credentialsId: "${dockeruserconfig}", variable: 'DOCKER_CONFIG_PATH')]) {
                    sh '''
                    ls -l ~/.kube ~/.docker
                    chmod 600 $DOCKER_CONFIG_PATH $KUBECONFIG_PATH
                    ls -l ~/.kube ~/.docker
                    export KUBECONFIG=${KUBECONFIG_PATH}
                    docker login
                    kubectl get nodes
                    helm repo add $CHART_REPO_NAME $CHART_URL
                    helm repo update
                    '''
                }
            }
        }
    }
        }
        stage('pull') {
            when {
                expression { !params.SKIP_PULL }
            }
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${GIT_BRANCH}"]],
                    userRemoteConfigs: [[url: "${GIT_URL}"]]
                ])
            }
        }
        stage('build') {
            when {
                expression { !params.SKIP_BUILD }
            }
           steps {
                script {
                    echo "Building the project from branch: ${GIT_BRANCH}"
                    sh '''
                    sh scripts/image-build2.sh ${DOCKER_REGISTRY}
                    '''
                }
            }
        }
        stage('push') {
            when {
                expression { !params.SKIP_PUSH }
            }
           steps {
                script {
                    echo "Building the project from branch: ${GIT_BRANCH}"
                    sh '''
                    docker push ${DOCKER_REGISTRY}/$DOCKER_USERNAME/${IMAGE_NAME}:${IMAGE_TAG}
                    '''
                }
            }
        }
        stage('deploy') {
            when {
                expression { !params.SKIP_DEPLOY }
            }
           steps {
                script {
                    echo "deploy"
                    sh '''
                    helm upgrade --install $APPNAME$RELEASE_NAME $CHART_REPO_NAME/$APPNAME --namespace $NAMESPACE
                    '''
                }
            }
        }
    }
}
