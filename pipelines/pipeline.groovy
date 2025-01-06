pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: "v1"
kind: "Pod"
metadata:
annotations:
    kubernetes.jenkins.io/last-refresh: "1736173871371"
labels:
    jenkins/jenkins-jenkins-agent: "true"
name: "default-4rzsg"
namespace: "default"
spec:
containers:
- args:
    - /usr/local/bin/jenkins-agent
    env:
    image: "sheer/tools:latest"
    imagePullPolicy: "IfNotPresent"
    name: "jnlp"
    resources:
    limits:
        memory: "2048Mi"
        cpu: "2048m"
    requests:
        memory: "1024Mi"
        cpu: "1024m"
    securityContext:
    privileged: true
    tty: true
    volumeMounts:
    - mountPath: "/home/jenkins/agent"
    name: "workspace-volume"
    readOnly: false
    workingDir: "/home/jenkins/agent"
serviceAccountName: "default"
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
