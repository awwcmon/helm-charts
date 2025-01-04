#!/bin/zsh
set -eux -o pipefail
packages=$(find packages -maxdepth 1 -type d ! -name ".*" ! -name "charts" ! -name "packages" |xargs)
helm package $packages --destination ./charts
helm repo index .
git add .
git commit -m "update charts"
git push
helm repo update