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
            string(name: 'IMAGE_NAME', defaultValue: 'school', description: 'IMAGE_NAME')
            string(name: 'TAG', defaultValue: 'latest', description: 'TAG')
            string(name: 'RELEASE_NAME', defaultValue: '', description: 'RELEASE_NAME')
        }
        environment {
            GIT_BRANCH = 'main'
            CHART_URL = 'https://awwcmon.github.io/helm-charts'
            CHART_REPO_NAME ='qing'
            REPO_HOST = 'docker.io'
            DOCKER_USERNAME='sheer'
            NAMESPACE = 'default'
        }
        stages {
            stage('pull') {
                when {
                    expression { !params.SKIP_PULL }
                }
                steps {
                    echo ".......pull code from https://github.com/awwcmon/${params.IMAGE_NAME}.git......."
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "${GIT_BRANCH}"]],
                        userRemoteConfigs: [[url: "https://github.com/awwcmon/${params.IMAGE_NAME}.git"]]
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
                        make image-build REPO_HOST=${env.REPO_HOST} TAG=${params.TAG}
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
                        make image-push REPO_HOST=${env.REPO_HOST} TAG=${params.TAG}
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
                        make helm CHART_REPO_NAME=${env.CHART_REPO_NAME} CHART_URL=${env.CHART_URL} \
                        IMAGE_NAME=${params.IMAGE_NAME} NAMESPACE=${env.NAMESPACE} \
                        RELEASE_NAME=${params.RELEASE_NAME}
                        """
                    }
                }
        }
    }
}
