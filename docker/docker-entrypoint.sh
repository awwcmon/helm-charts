#!/bin/bash
set -e

# 启动 Docker Daemon，指定存储驱动
sudo nohup dockerd --storage-driver=vfs &> /dev/null &

exec "$@"