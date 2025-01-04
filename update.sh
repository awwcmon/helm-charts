#!/bin/zsh
set -eux -o pipefail
packages=$(find . -maxdepth 1 -type d ! -name ".*"|xargs)
helm package $packages --destination ./charts
helm repo index ./charts
git add .
git commit -m "update charts"
git push
helm repo update