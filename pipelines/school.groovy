pipeline {
    agent any
        parameters {
        booleanParam(name: 'SKIP_PREPARE', defaultValue: true, description: 'Skip the prepare stage')
        booleanParam(name: 'SKIP_PULL', defaultValue: true, description: 'Skip the pull stage')
        booleanParam(name: 'SKIP_BUILD', defaultValue: true, description: 'Skip the build stage')
        booleanParam(name: 'SKIP_PUSH', defaultValue: true, description: 'Skip the push stage')
        booleanParam(name: 'SKIP_DEPLOY', defaultValue: false, description: 'Skip the deploy stage')
    }
    environment {
        K8S_TOKEN = sh(script: 'cat /var/run/secrets/kubernetes.io/serviceaccount/token', returnStdout: true).trim()
        K8S_CA_CERT = '/var/run/secrets/kubernetes.io/serviceaccount/ca.crt'
        GIT_URL = 'https://github.com/awwcmon/school.git'
        GIT_BRANCH = 'main'
        CHART_URL = 'https://awwcmon.github.io/helm-charts'
        CHART_REPO_NAME ='qing'
        APPNAME = 'school'
        RELEASE_NAME = ''
        NAMESPACE = 'default'
        KUBECONFIGSTR = 'YXBpVmVyc2lvbjogdjEKY2x1c3RlcnM6Ci0gY2x1c3RlcjoKICAgIGNlcnRpZmljYXRlLWF1dGhvcml0eS1kYXRhOiBMUzB0TFMxQ1JVZEpUaUJEUlZKVVNVWkpRMEZVUlMwdExTMHRDazFKU1VSQ1ZFTkRRV1V5WjBGM1NVSkJaMGxKVWxOT2RVMHJSalpsTmtWM1JGRlpTa3R2V2tsb2RtTk9RVkZGVEVKUlFYZEdWRVZVVFVKRlIwRXhWVVVLUVhoTlMyRXpWbWxhV0VwMVdsaFNiR042UVdWR2R6QjVUa1JGZUUxcVFYaE9WRVV6VFZSS1lVWjNNSHBPUkVWNFRWUm5lRTVVU1hsTlZFcGhUVUpWZUFwRmVrRlNRbWRPVmtKQlRWUkRiWFF4V1cxV2VXSnRWakJhV0UxM1oyZEZhVTFCTUVkRFUzRkhVMGxpTTBSUlJVSkJVVlZCUVRSSlFrUjNRWGRuWjBWTENrRnZTVUpCVVVSWGRHSk5VVEJYU3pSYVYwUklUWFIwY2sxNFEyMU9kRzVuYldObVpWQkRRbE5xZW5wSlJuTTRUV3B5ZVcxdmVtVmhkbU15U25waFYxVUtZVkJSWVc4elYwOTFRMDlZUjFoalJGQjNaaXR3SzB0UmNEZDFWbEpvWmtwUGEyNHhSekZpVTFoMU1HZG1ja05QWW1JMGJXZHFkRFpFWXpCRmFHdDRZd3BYUjBKSWVraDZlVlJaY0d4VGJEWXZielJhU25weVVqWlRNMlk0ZVRSMU1pOWhjVVpUVldwVVFqSnJSbEUyUkZKWmVYVlpRMk5OVTAxaGVYQjRTVEJQQ25KNE5FdHFhbWM0TDNaSk1HNXVXRVVyTkV3MGFHdzRibUZvVW1WdmRIZHhaRk5aY2t0dVlVVmtTMHMyYXpCclRqa3ZOVGx0TkdGeFIwRnFlRTEwYzJvS0wzVmliMHQyTTA1bGR6VmFWQ3ROYjJSMVIwdHRSRkV3Y1dOb1dUTmFORmRLYVRST1RYSndUbkY2YkhJd2JtSnpXalZTTldNclZtVkVhbUZyT1VScFdncDNTRmRSWVRaMWRHdFNWWGN2TVZSU1ZIWlNWeXRKVmxCR1F6bDRRV2ROUWtGQlIycFhWRUpZVFVFMFIwRXhWV1JFZDBWQ0wzZFJSVUYzU1VOd1JFRlFDa0puVGxaSVVrMUNRV1k0UlVKVVFVUkJVVWd2VFVJd1IwRXhWV1JFWjFGWFFrSlJlVlJOYkhJME4yWXpTWFUxZUZwT1VsWTRUM0UzYkU5eGN6RjZRVllLUW1kT1ZraFNSVVZFYWtGTloyZHdjbVJYU214amJUVnNaRWRXZWsxQk1FZERVM0ZIVTBsaU0wUlJSVUpEZDFWQlFUUkpRa0ZSUVhaTE5uSnhTMUpuYVFwaGIxQTFWMEZtZVZwalRscFRURXBOU0VzMmFURkhUVzVHVlRkQlFuazRTbTVYU25kT1UwbFpiemN3VkdSWWFraHpjRnAzSzJrM1Z6Qktla1pwZEZVdkNsUnNhbFZvUVd0eFVTOUNOMEZYVTBKTFdtcFlWSFJKZW1Gc05tVk9PRTExWTFCMk9GbGxha3M1VFhacE9DdDRMMWhoUjFablRuZG5hbGRPVFVwb1RuVUtWR3AyWVVKTkwwSlhaRTFzTkdGelJsVm5XWHBJY1VkM2JFUmhaMEVyWVVGdWRYaHhWamhVTlhBd2FYUjRNelowVkU4d1lrTXhPR2RsUzJSR1F6TkpNUXBpUWxCd1NHeHZRWFJXWTNKSGNIaGFWREZZYW5aU1ZXdFFWbVJqUjNKeFZYSndhRkpwZVVsbFpXVTBWblpqVm1sM1dtWnFMMk5OWjNoa2VFSjRTMmhrQ2todmEycDNTRU55VGtoTFZtVjFaRXh5VVUxd1QwNWhXbFZxVUZWbGFFODBSMnd5YVZwWldrWnlZMDR5ZG1JdmF6WkhSR0prUW01YWJXbEZaWFZKWW1jS2JVbFFUMVIxVURaeVUyUXJDaTB0TFMwdFJVNUVJRU5GVWxSSlJrbERRVlJGTFMwdExTMEsKICAgIHNlcnZlcjogaHR0cHM6Ly8xOTIuMTY4LjUuMTU6NjQ0MwogIG5hbWU6IGt1YmVybmV0ZXMKY29udGV4dHM6Ci0gY29udGV4dDoKICAgIGNsdXN0ZXI6IGt1YmVybmV0ZXMKICAgIHVzZXI6IGt1YmVybmV0ZXMtYWRtaW4KICBuYW1lOiBrdWJlcm5ldGVzLWFkbWluQGt1YmVybmV0ZXMKY3VycmVudC1jb250ZXh0OiBrdWJlcm5ldGVzLWFkbWluQGt1YmVybmV0ZXMKa2luZDogQ29uZmlnCnByZWZlcmVuY2VzOiB7fQp1c2VyczoKLSBuYW1lOiBrdWJlcm5ldGVzLWFkbWluCiAgdXNlcjoKICAgIGNsaWVudC1jZXJ0aWZpY2F0ZS1kYXRhOiBMUzB0TFMxQ1JVZEpUaUJEUlZKVVNVWkpRMEZVUlMwdExTMHRDazFKU1VSTFZFTkRRV2hIWjBGM1NVSkJaMGxKVlcxbmQyUmFkWHBrUmtsM1JGRlpTa3R2V2tsb2RtTk9RVkZGVEVKUlFYZEdWRVZVVFVKRlIwRXhWVVVLUVhoTlMyRXpWbWxhV0VwMVdsaFNiR042UVdWR2R6QjVUa1JGZUUxcVFYaE9WRVV6VFZSS1lVWjNNSGxPVkVWNFRXcEJlRTVVU1hsTlZFcGhUVVIzZUFwSWVrRmtRbWRPVmtKQmIxUkdiWFF4V1cxV2FGcEhNRFpaTW5neFl6TlNiR05wTVdoYVJ6RndZbTVOZUVkVVFWaENaMDVXUWtGTlZFVkhkREZaYlZaNUNtSnRWakJhV0UxMFdWZFNkR0ZYTkhkblowVnBUVUV3UjBOVGNVZFRTV0l6UkZGRlFrRlJWVUZCTkVsQ1JIZEJkMmRuUlV0QmIwbENRVkZEZHpOUk9UWUtkbXRWTVdvelZWTXZaR2cxV0dzMWVrdGhVbTVFVDBRMmNUUXlUekpMVEdWa2NtRlZVVEJIUTJwa1ZWTnlZV3BZUzIxQ0t6Z3dPVXhJTm5wMVduQjFVZ3BhWkZaVlFqZEpTSGh3UTNWalJIcFpZMWczVGtoQldHdHNaakZqTnpaVEwwMVdUVmN6UWtjcmIxVkxMMUpKV2psUk1IbGxlRzh2Y25GRlFYbG1XRTk0Q2poWFdFdGtVRVZRTjFWamFVdERZbk4yTm5aUmVFVTFkbU5JVms4NGNHNXJNbmx2THpkek1sbzBWVVJUUVhSMWFrSjNWMUpMZFdWd1pWSlNTR2xQSzB3S1VrUkVSblJ4ZFU1SWVtZE5LM2RPT0hkeWIzbDBXbWhCTmpseFNsWndSMjExVW5VNFltRk1aRWMzWmxRMWEyVk9jemRzUmtWd09XTlVaVFEwU2psM01RcFdjRlZRYld0clMwWnNPRWg2VDBSdGREVkRhbTVPUjFweVMwSnJkVlkxVFUxQ1drVkZTMHRvT0RKd1ZscDBWREpoWTBRcmFuTnpZV0pJT1dWNk9FVlVDbkJUVEZCemJEbGFkRUpHYm1FMWMyaEJaMDFDUVVGSGFsWnFRbFZOUVRSSFFURlZaRVIzUlVJdmQxRkZRWGRKUm05RVFWUkNaMDVXU0ZOVlJVUkVRVXNLUW1kbmNrSm5SVVpDVVdORVFXcEJUVUpuVGxaSVVrMUNRV1k0UlVGcVFVRk5RamhIUVRGVlpFbDNVVmxOUW1GQlJrUktUWGxYZG1wMEwyTnBOMjVHYXdveFJsaDNObkoxVlRaeGVsaE5RVEJIUTFOeFIxTkpZak5FVVVWQ1EzZFZRVUUwU1VKQlVVSjBPRGhzVWl0Q1MzZFBhRTB5UzJ4bU9XRm9PVEZrU0ZCWkNsaE9iakEzTTI5blFVNWtWMlpuSzJSQmF6QTNOV1pqUjBOck16QlZLMkY1ZG5aNmIzZFdiMUpqV0VKMk0zUlNkbkZEVkdOM2RXUllTVkJCWlc1RU1VOEtkWE5rTjBNeFVVMDRSa3B6UkVzMGEyVlBhV0V5WkdaQkwyYzBNRzV0VVhWSU0xa3paM1pXTHpoT1pXZHJPVVo1ZFdkNU9VMUVMMHR5ZVdVMGJUUTBTQXAyT0hCb2FVWnRaVkJHUlVKa1YwTmFUblp3UmxCME9Ga3lSbVF6TURoVGFUaFFOSE16YjJkRFJqSlRWMmd3VFZjcmR6SlpTMWx4YUZCdlVrbExNRzkwQ25remVFSjJjVlpMT0dRM1VFeGhTRk4yU0VKM055OTVSMkZSVFM5eE5UTkxMM28xVTA1NFoxQkZSMGx0V2pKS2IzUk1ZM0ZNYm5WdldrMHpibFpRTkdjS01FWk5ibEZYUVZwMFduQk9OekJ3WkdsWEt6bDZOek15VlN0eVREZzRiMkYzWm5Nek9GVldOM1JvZFRVclEyTlFObVprVTNGd0swSjVlamQxQ2kwdExTMHRSVTVFSUVORlVsUkpSa2xEUVZSRkxTMHRMUzBLCiAgICBjbGllbnQta2V5LWRhdGE6IExTMHRMUzFDUlVkSlRpQlNVMEVnVUZKSlZrRlVSU0JMUlZrdExTMHRMUXBOU1VsRmIzZEpRa0ZCUzBOQlVVVkJjMDR3VUdWeU5VWk9XVGt4UlhZeldXVldOVTlqZVcxclduZDZaeXR4ZFU1cWRHbHBNMjVoTW14RlRrSm5iek5XQ2tWeE1tOHhlWEJuWm5aT1VGTjRLM00zYldGaWExZFlWbFpCWlhsQ09HRlJjbTVCT0RKSVJpdDZVbmRHTlVwWU9WaFBLMnQyZWtaVVJuUjNVblp4UmtNS2RqQlRSMlpWVGsxdWMyRlFOalpvUVUxdU1YcHpaa1pzZVc1VWVFUXJNVWhKYVdkdE4wd3JjakJOVWs5aU0wSXhWSFpMV2pWT2MzRlFLemRPYldWR1FRb3daMHhpYjNkalJtdFRjbTV4V0d0VlVqUnFkbWt3VVhkNFltRnlhbEk0TkVSUWMwUm1UVXMyVFhKWFdWRlBkbUZwVm1GU2NISnJZblpITW1relVuVXpDakFyV2tocVlrODFVbEpMWmxoRk0zVlBRMlpqVGxaaFZrUTFjRXBEYUZwbVFqaDZaelZ5WlZGdk5YcFNiV0Y1WjFwTWJHVlVSRUZYVWtKRGFXOW1UbkVLVmxkaVZUbHRia0V2YnpkTVIyMTRMMWh6TDBKRk5sVnBlamRLWmxkaVVWSmFNblZpU1ZGSlJFRlJRVUpCYjBsQ1FVRnVNVk5IZGxkNmNuSklVM2RaTkFwMVRHMU5jamh2V1V4RFQwcFFRbTFvUlhsV1dqSlZTRUpYVjBwaWNuaExWWFpqZVVKaWNVa3JlSEJSWmxOMlYydzBUM3BFV21weU0ybDBNV2xYV1c1TENtbHNZbTlDZFZWQk4zVjZTbWxUVG01MGJ6VklRaTlYYWpVeFVFRk1SbVZhWVU1TGQwaDNjSEZuWVZCdE9Xa3JSV3cyYWtGSE5GZElWeTh6WTA4M05Yb0tNRFk1SzNBd1NuQlJSbXhQTVVWUEswSjZlbU4xUVVkRmRsbHZMelZsVnpBd1pWbDBkRmwyWkdwMldYWlVMMUpuU2pJdlNVWnlNM1ZZUjJKSFIxTkdPQXBUV0hSUU1XbG5ObnBYYUhOUVQwSmFVMnBVYVhSRmJYQXlkRGxrZUVFd2FVWXlXRVpoYUZObVkzSkJaRlZsZUdaVVlWTmlWbU42TXl0TVUwUnRXRFp6Q2tsbGJXRnBhVUZwVTA5T2VVMWhjMlV4U1RaV04wVlZVbWs0ZFRWT1VVMTBWVVYxWVdOeGJqUktURlp0WVZSVVMyeDNWWEE0VkRSMGQyUkNhSFZYUWpZS1FpOVZlRzB3YTBObldVVkJNMGRKV0hJeFpVdFFUWFpXTkZCS2NqQndTMjVyYUU5S1pqSlVVRkU0ZVZBMVIxRXZiRkZqV2psRlpEUmhVV3hFUmpWdVVBcExPRmhZVFVNMFRXWjJOMnBFVjNWa1EzQTNkVWRRTlVOc2JtTXJkblJpUlRodlpWaHZhMGhWTlc5eWFIYzFNemMxZERNd2RFWkRSSFI1Y1VoalZVTlRDbU5yUTNWaFdUSk5kVVZvVlRndmNVaEdNMnQ2TDNOTFUwUlhVMEpoV0d4UlVUaDRaWFpEVEVGdlFXd3pNM0I1VDIwMFpFaFhhazFEWjFsRlFYcFlTblVLUzBSMmNuRnZXaTgxWTJoaFpXWjNTVVJVTUUxMVRuTktaMlZ3YjNOVlVEUnBOVzQxTjNRdlFXRTVjek5DY1ZoaFEzVkpPREZwVFhoUFUzUnFlR1pJVlFvNFkwczBSbTh5Y25aT2JrTm9OekpzU21sbFRIZG5kMVUyTDFCWVZYVlhkRmhzSzIxUVpVcGFhRkF5YjBkaFIybDJSMlJQUkdGaVNrUkZOM0ZhUWpOc0NsZHRNbEJuVjFWMWVYRnhaMEZIYzIxUVIzWjNjRTl3VGxKVGFDOWxkVmN3UlZwNFUxTldjME5uV1VGU1ZHWnhRMGxEVDFGd2IycERkM2RhU1ZsSksySUtNM05FWWt0bVZrVlpOVzFuTWpKYVMzTXlXVTFWYkdoWlJuZDNXRzVHU1hsSk4xUkVObUp0T1dSNFNYQkRXbmR3U3pGWFUzcE9VVFZ6UmpsVE5rNWFTd3BJVHl0Q1NrSTBkR2hDV1RaSUwzVmFiM3BCUm5kS1V6TkhlbGhNV2sxRFlrUkJNR3cyUzBZeVJIVm1jM2xITUROSlMwMHJhVGxUWjBSS1drVjVLM1pNQ21oM05taFJla1pSTXpZeGVqUXlUVzVRZWxsU1JsRkxRbWRGYm1KUlZUWnNaU3R6Y0haM1JGVk1aa280WTBWcFUwVmxha05zV1VKdGVrbDNkR0V3VTBVS1ltZ3dOWGQxYzBSUE5YcG5UWEZ4Unl0dFptYzNkV3R0WlVFdlNrTkZlVUpQTjNsbU5GbFdZVzh4ZHpWRFVUTlhWMW92UVhsRFUzRXpSbGR2ZVRsTFNncG5SRWgyUmxVclEwRk9VakpFZUVzM09FbDBiakJuTjJseU9VTmxSSEl5WW1SWFQwaENTRTVJVFRRemVUbDVVSGhwWldzdlkyUm1Vek13UmtoSmNIcFRDa2hoVEZKQmIwZENRVXBWWTNaWk5rRkljVlF4T1ZSaEwzRTBZVFZWVmtKTU4wVjVhMUV2UTB0VFZrdEtiMXA1TjFWSFkxVnFUbTlaYVVWS2NuVlRZbTBLV1RZeVNVWnROa3AyVGtscWF6TnJlR0ZLWkdKTWRHTjJZa0poYkZSVWIwNWtjMVZIVlZaMk5qQTBlVmRMYlVoU2RERmxhRGxHTjA1ek0yMTRaMVowVWdwSVRFcGpTM2h5YzNKNVRGWTBWVEl6U25CV04yaDFRVXh0ZGtjeGRVWlpWVEJ6Vm5FdlduSlRWVTFRWmtFeFZFeExTMjlVQ2kwdExTMHRSVTVFSUZKVFFTQlFVa2xXUVZSRklFdEZXUzB0TFMwdENnPT0K'
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_NAME = 'school'
        IMAGE_TAG = 'latest'
        DOCKER_USERNAME='sheer'
        DOCKER_PASSWORD='awww8023.'
        DOCKER_USERINFO='ewoJImF1dGhzIjogewoJCSJodHRwczovL2luZGV4LmRvY2tlci5pby92MS8iOiB7CgkJCSJhdXRoIjogImMyaGxaWEk2WVhkM2R6Z3dNak11IgoJCX0sCgkJInJlZ2lzdHJ5LTEuZG9ja2VyLmlvOjQ0MyI6IHsKCQkJImF1dGgiOiAiYzJobFpYSTZZWGQzZHpnd01qTXUiCgkJfQoJfQp9'
    }
    stages {

        stage('prepare'){
            when {
                expression { !params.SKIP_PREPARE }
            }
            steps{
                script{
                    sh '''
                    mkdir -p ~/.docker
                    echo $DOCKER_USERINFO | base64 -d > ~/.docker/config.json
                    chmod 600 ~/.docker/config.json
                    docker login

                    mkdir -p ~/.kube/
                    echo $KUBECONFIGSTR | base64 -d > ~/.kube/kuberconfig.yaml
                    chmod 600 ~/.kube/kuberconfig.yaml
                    export KUBECONFIG = ~/.kube/kuberconfig.yaml
                    helm repo add $CHART_REPO_NAME $CHART_URL
                    helm repo update
                    '''
                }
            }
        }
        stage('pull') {
            when {
                expression { !params.SKIP_PULL }
            }
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${GIT_BRANCH}"]],
                    userRemoteConfigs: [[url: "${GIT_URL}"]]
                ])
            }
        }
        stage('build') {
            when {
                expression { !params.SKIP_BUILD }
            }
           steps {
                script {
                    echo "Building the project from branch: ${GIT_BRANCH}"
                    sh '''
                    sh scripts/image-build2.sh docker.io
                    ls
                    '''
                }
            }
        }
        stage('push') {
            when {
                expression { !params.SKIP_PUSH }
            }
           steps {
                script {
                    echo "Building the project from branch: ${GIT_BRANCH}"
                    sh '''
                    docker push ${DOCKER_REGISTRY}/$DOCKER_USERNAME/${IMAGE_NAME}:${IMAGE_TAG}
                    ls
                    '''
                }
            }
        }
        stage('deploy') {
            when {
                expression { !params.SKIP_DEPLOY }
            }
           steps {
                script {
                    echo "deploy"
                    sh '''
                    helm upgrade --install $APPNAME$RELEASE_NAME $CHART_REPO_NAME/$APPNAME --namespace $NAMESPACE
                    '''
                }
            }
        }
    }
}