#!/usr/bin/env bash

set -eo pipefail

echo 'Checking version...'

VERSION_GIT_TAG="v$(./.circleci/check-version.sh)"

if [[ $? -ne 0 ]]; then
  exit 0 # exit as success for circleci
fi


echo 'Installing lalitkapoor/github-changes to generate the release note...'

sudo npm install -g github-changes@1.1.2


echo 'Generating a change log...'

github-changes \
  --token $GITHUB_TOKEN \
  --owner $CIRCLE_PROJECT_USERNAME \
  --repository $CIRCLE_PROJECT_REPONAME \
  --only-pulls --use-commit-body --for-tag nightly-build

sed -i -e '1,3d' CHANGELOG.md
