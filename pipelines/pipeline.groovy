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
    jenkins/label-digest: "500b4f18aee87616849e4f4c2435020898e34aa0"
    jenkins/label: "jenkins-jenkins-agent"
    kubernetes.jenkins.io/controller: "http___jenkins_default_svc_cluster_local_8080x"
  name: "default-4rzsg"
  namespace: "default"
spec:
  containers:
  - args:
    - "/usr/local/bin/jenkins-agent"
    env:
    - name: "JENKINS_SECRET"
      value: "********"
    - name: "JENKINS_TUNNEL"
      value: "jenkins-agent.default.svc.cluster.local:50000"
    - name: "JENKINS_AGENT_NAME"
      value: "default-4rzsg"
    - name: "REMOTING_OPTS"
      value: "-noReconnectAfter 1d"
    - name: "JENKINS_NAME"
      value: "default-4rzsg"
    - name: "JENKINS_AGENT_WORKDIR"
      value: "/home/jenkins/agent"
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
