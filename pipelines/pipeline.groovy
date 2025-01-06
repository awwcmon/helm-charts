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
    env:
    - name: "JENKINS_URL"
      value: "http://jenkins.default.svc.cluster.local:8080/"
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
  hostNetwork: false
  nodeSelector:
    kubernetes.io/os: "linux"
  restartPolicy: "Never"
  serviceAccountName: "default"
  volumes:
  - emptyDir:
      medium: ""
    name: "workspace-volume"
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
