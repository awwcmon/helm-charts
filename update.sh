#!/bin/zsh
set -eux -o pipefail
charts=$(find charts -maxdepth 1 -type d ! -name ".*" ! -name "charts" ! -name "packages" |xargs)
helm package $charts --destination ./packages
helm repo index .
git add .
git commit -m "update charts"
git push
helm repo update