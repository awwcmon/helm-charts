pipeline {
    agent {
        kubernetes {
            inheritFrom 'sheer-tools' // 引用预定义模板的 Label
        }
    }
    stages {
        stage('Run in Predefined Pod Template') {
            steps {
            sh """
            cat /root/.kube/kuberconfig.yaml
            cat /root/.docker/config.json
            """
            }
        }
    }
}