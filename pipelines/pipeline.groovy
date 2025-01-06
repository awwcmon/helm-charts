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
    command:
    - /bin/sh
    - -c
    - "cat /etc/passwd && ls /"
    tty: true
"""
        }
    }
    stages {
        stage('Run in Kubernetes Pod') {
            steps {
                    sh 'echo "Running in Kubernetes Pod!"'
            }
        }
    }
}