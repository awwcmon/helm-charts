pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: sheer-tools
    image: sheer/tools:latest
    volumeMounts:
    - name: dockerconfig
      mountPath: /home/jenkins/.docker
      readOnly: true
    - name: kubeconfig
      mountPath: /home/jenkins/.kube
      readOnly: true
    securityContext:
      privileged: true
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