pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: sheer-tools
    image: sheer/tools
    volumeMounts:
    - name: dockerconfig
      mountPath: /home/jenkins/.docker
      readOnly: true
    - name: kubeconfig
      mountPath: /home/jenkins/.kube
      readOnly: true
    command:
    - sh
    - -c
    - |
      /usr/local/bin/jenkins-agent && export DOCKER_CONFIG=/home/jenkins/.docker/config.json && \
      export KUBECONFIG=/home/jenkins/.kube/kubeconfig.json && docker login && cat
    stdin: true
    tty: true
  volumes:
  - name: dockerconfig
    secret:
      secretName: dockerconfig
  - name: kubeconfig
    secret:
      secretName: kubeconfig
"""
        }
    }
    stages {
        stage('Run in Kubernetes Pod') {
            steps {
                sh """
                docker images
                docker ps
                kubectl get nodes
                """
            }
        }
    }
}