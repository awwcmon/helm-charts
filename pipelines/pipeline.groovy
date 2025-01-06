pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: "v1"
kind: "Pod"
metadata:
  labels:
    jenkins/jenkins-jenkins-agent: "true"
    jenkins/label: "jenkins-jenkins-agent"
  name: "new-pipeline"
  namespace: "default"
spec:
  containers:
  - args:
    - "/usr/local/bin/jenkins-agent"
    image: "sheer/tools:latest"
    imagePullPolicy: "IfNotPresent"
    name: "jnlp"
    resources:
      limits:
        memory: "1024Mi"
        cpu: "1024m"
      requests:
        memory: "512Mi"
        cpu: "512m"
    securityContext:
      privileged: true
    tty: true
    volumeMounts:
    - mountPath: "/home/jenkins/.kube"
      name: "kubeconfig"
      readOnly: true
    - mountPath: "/home/jenkins/.docker"
      name: "dockerconfig"
      readOnly: true
    - mountPath: "/home/jenkins/agent"
      name: "workspace-volume"
      readOnly: false
    workingDir: "/home/jenkins/agent"
  hostNetwork: false
  nodeSelector:
    kubernetes.io/os: "linux"
  restartPolicy: "Never"
  serviceAccountName: "default"
  volumes:
  - emptyDir:
      medium: ""
    name: "workspace-volume"
  - name: kubeconfig
    secret:
      secretName: kubeconfig
  - name: dockerconfig
    secret:
      secretName: dockerconfig
"""
        }
    }
    parameters {
            booleanParam(name: 'SKIP_PULL', defaultValue: false, description: 'Skip the pull stage')
            booleanParam(name: 'SKIP_BUILD', defaultValue: false, description: 'Skip the build stage')
            booleanParam(name: 'SKIP_PUSH', defaultValue: false, description: 'Skip the push stage')
            booleanParam(name: 'SKIP_DEPLOY', defaultValue: false, description: 'Skip the deploy stage')
            string(name: 'BUILD_SCRIPT', defaultValue: 'scripts/image-build2.sh', description: 'image build script')
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
            DOCKER_USERNAME='sheer'
        }
        stages {
            stage('pull') {
                when {
                    expression { !params.SKIP_PULL }
                }
                steps {
                    echo ".......pull code from ${params.GIT_URL}......."
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
                        sh ${params.BUILD_SCRIPT} ${DOCKER_REGISTRY}
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
                        echo ".......docker push......."
                        sh """
                        set -x
                        #export DOCKER_CONFIG=/home/jenkins/.docker
                        docker login
                        docker push ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${params.IMAGE_NAME}:${params.IMAGE_TAG}
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
                        #export KUBECONFIG=/home/jenkins/.kube/kubeconfig.yaml
                        helm repo add ${CHART_REPO_NAME} ${CHART_URL}
                        helm repo update
                        helm upgrade \
                        --install ${params.APP_NAME}${params.RELEASE_NAME} ${CHART_REPO_NAME}/${params.APP_NAME} \
                        --namespace ${params.NAMESPACE}

                        timeout 13 kubectl get pods school -w
                        """
                    }
                }
        }
    }
}
