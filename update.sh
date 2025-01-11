#!/bin/zsh
set -eux -o pipefail
message="update charts"
if [ $# -eq 1 ];then
   message=$1
fi
charts=$(find charts -maxdepth 1 -type d ! -name ".*" ! -name "charts" ! -name "packages" |xargs)
helm package $charts --destination ./packages
helm repo index .
git add .
git commit -m "$message"
git push
helm repo update