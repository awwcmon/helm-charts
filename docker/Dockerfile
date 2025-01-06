# 第一阶段：构建工具和依赖
FROM ubuntu:20.04 as builder
# 设置非交互模式
ENV DEBIAN_FRONTEND=noninteractive
# 安装构建工具
RUN apt-get update && apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    wget \
    software-properties-common && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=arm64 signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && apt-get install -y \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    docker-buildx-plugin \
    docker-compose-plugin

## 安装 Go
#ENV GO_VERSION=1.23.4
#RUN wget https://go.dev/dl/go${GO_VERSION}.linux-arm64.tar.gz && \
#    tar -C /usr/local -xzf go${GO_VERSION}.linux-arm64.tar.gz && \
#    rm go${GO_VERSION}.linux-arm64.tar.gz
#ENV PATH=$PATH:/usr/local/go/bin

# 安装 kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/arm64/kubectl" && \
    chmod +x kubectl && mv kubectl /usr/local/bin/

# 安装 Helm
RUN curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
#RUN #which docker dockerd && ls /usr/bin/*|grep docker && which containerd
RUN which runc
#RUN locate libip4tc.so.2
# 第二阶段：基于 jenkins/inbound-agent 创建最终镜像
# 第二阶段：基于 jenkins/inbound-agent 创建最终镜像

FROM jenkins/inbound-agent:latest
ENV DEBIAN_FRONTEND=noninteractive

USER root
RUN apt-get update -y && apt-get install -y iptables sudo && \
apt-get autoremove -y && \
apt-get clean && \
rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# 复制构建阶段所需的工具
#COPY --from=builder /usr/local/go /usr/local/go
COPY --from=builder /usr/local/bin/kubectl /usr/local/bin/kubectl
COPY --from=builder /usr/local/bin/helm /usr/local/bin/helm
COPY --from=builder /usr/bin/docker* /usr/bin/
COPY --from=builder /usr/bin/contain* /usr/bin/
COPY --from=builder /usr/sbin/iptables /usr/sbin/iptables
COPY --from=builder /usr/bin/runc /usr/bin/runc
# 复制 Docker Daemon 配置文件
COPY daemon.json /etc/docker/daemon.json
# 配置 Docker Daemon 启动脚本
#COPY docker-entrypoint.sh /usr/local/bin/
RUN #chmod +x /usr/local/bin/docker-entrypoint.sh
RUN usermod -aG root,sudo jenkins
RUN echo 'jenkins ALL=(ALL) NOPASSWD:ALL' | tee -a /etc/sudoers
RUN sed -i '1a sudo nohup dockerd --storage-driver=vfs > /dev/null 2>&1 &' /usr/local/bin/jenkins-agent
ENV KUBECONFIG=/home/jenkins/.kube/kubeconfig.yaml
ENV DOCKER_CONFIG=/home/jenkins/.kube/config.json
USER jenkins
# 设置默认启动命令
ENTRYPOINT ["/usr/local/bin/jenkins-agent"]
#CMD ["bash"]