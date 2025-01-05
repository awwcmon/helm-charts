#!/bin/bash
set -e

# 启动 Docker Daemon，指定存储驱动
sudo nohup dockerd --storage-driver=vfs &> /dev/null &
# 等待 dockerd 启动完成
sleep 3
/usr/local/bin/jenkins-agent
# 进入交互式 shell