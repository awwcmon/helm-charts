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
    - ls &&
    - cat /home/jenkins/.docker/config.json
    - && pwd
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
                echo "haha"
                """
            }
        }
    }
}