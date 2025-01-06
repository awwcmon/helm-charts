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
