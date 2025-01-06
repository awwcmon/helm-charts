pipeline {
    agent any
    parameters {
        booleanParam(name: 'SKIP_PREPARE', defaultValue: false, description: 'Skip the prepare stage')
        booleanParam(name: 'SKIP_PULL', defaultValue: false, description: 'Skip the pull stage')
        booleanParam(name: 'SKIP_BUILD', defaultValue: false, description: 'Skip the build stage')
        booleanParam(name: 'SKIP_PUSH', defaultValue: false, description: 'Skip the push stage')
        booleanParam(name: 'SKIP_DEPLOY', defaultValue: false, description: 'Skip the deploy stage')
        string(name: 'GIT_URL', defaultValue: 'https://github.com/awwcmon/school.git', description: 'GIT_URL')
        string(name: 'IMAGE_NAME', defaultValue: 'school', description: 'IMAGE_NAME')
        string(name: 'RELEASE_NAME', defaultValue: '', description: 'RELEASE_NAME')
        string(name: 'APP_NAME', defaultValue: 'school', description: 'APP_NAME')
        string(name: 'NAMESPACE', defaultValue: 'default', description: 'NAMESPACE')
        string(name: 'IMAGE_TAG', defaultValue: 'latest', description: 'IMAGE_TAG')
    }
    environment {
        GIT_BRANCH = 'main'
        CHART_URL = 'https://awwcmon.github.io/helm-charts'
        CHART_REPO_NAME ='qing'
        DOCKER_REGISTRY = 'docker.io'
        KUBECONFIG_PATH = "/home/jenkins/.kube/kubeconfig.yaml"
        DOCKER_CONFIG_PATH = "/home/jenkins/.docker/config.json"
        DOCKERUSERCONFIG = 'dockeruserconfig'
        KUBECONFIG = 'kubeconfig'
    }
    stages {
        stage('prepare'){
            when {
                expression { !params.SKIP_PREPARE }
            }
            steps {
                script{
                    echo ".......prepare dockerconfig and kubeconfig......."
                    sh """
                    mkdir -p $(dirname $KUBECONFIG_PATH) $(dirname $DOCKER_CONFIG_PATH)
                    """
                    withCredentials([
                        file(credentialsId: env.DOCKERUSERCONFIG, variable: 'DOCKER_CONFIG_PATH'),
                        file(credentialsId: env.KUBECONFIG, variable: 'KUBECONFIG_PATH')]) {
                            sh """
                            set -x
                            chmod 600 $KUBECONFIG_PATH $DOCKER_CONFIG_PATH
                            export KUBECONFIG=${KUBECONFIG_PATH}
                            export DOCKER_CONFIG=$(dirname $DOCKER_CONFIG_PATH)
                            helm repo add $CHART_REPO_NAME $CHART_URL
                            helm repo update
                            docker login
                            """
                        }
                    }
            }
        }
        stage('pull') {
            when {
                expression { !params.SKIP_PULL }
            }
            steps {
                echo ".......pull code from $GIT_URL......."
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${GIT_BRANCH}"]],
                    userRemoteConfigs: [[url: "${params.GIT_URL}"]]
                ])
            }
        }
        stage('build') {
            when {
                expression { !params.SKIP_BUILD }
            }
           steps {
                script {
                    echo ".......Building the project from branch: ${GIT_BRANCH}......."
                    sh """
                    set -x
                    sh scripts/image-build2.sh ${DOCKER_REGISTRY}
                    """
                }
            }
        }
        stage('push') {
            when {
                expression { !params.SKIP_PUSH }
            }
           steps {
                script {
                    echo ".......docker push ${DOCKER_REGISTRY}/$DOCKER_USERNAME/${params.IMAGE_NAME}......."
                    sh """
                    set -x
                    docker push ${DOCKER_REGISTRY}/$DOCKER_USERNAME/${params.IMAGE_NAME}:${params.IMAGE_TAG}
                    """
                }
            }
        }
        stage('deploy') {
            when {
                expression { !params.SKIP_DEPLOY }
            }
           steps {
                script {
                    echo ".......deploy......."
                    sh """
                    set -x
                    helm upgrade --kubeconfig=${DOCKER_CONFIG_PATH} \
                    --install ${params.APP_NAME}${params.RELEASE_NAME} ${CHART_REPO_NAME}/${params.APP_NAME} \
                    --namespace ${params.NAMESPACE}
                    """
                }
            }
        }
    }
}
