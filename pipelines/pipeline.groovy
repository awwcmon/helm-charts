pipeline {
    agent none
    stages {
        stage('Download Pod Template from GitHub') {
            steps {
                script {
                    // 从 GitHub 上下载 YAML 文件
                    sh 'curl -o sheer-tools.yaml https://raw.githubusercontent.com/awwcmon/helm-charts/refs/heads/main/pipelines/sheer-tools.yaml'
                }
            }
        }
        stage('Run in Kubernetes Pod') {
            agent {
                kubernetes {
                    yamlFile 'sheer-tools.yaml'
                }
            }
            steps {
                // 在 Kubernetes Pod 中执行任务
                sh """
                docker images
                """
            }
        }
    }
}