#!/bin/zsh
name=$1
if [ $# -eq 0 ];then
  echo "usages:\n $0 packagename"
fi
helm package $name
helm repo index .
git add .
git commit -m "add or update $name"
git push