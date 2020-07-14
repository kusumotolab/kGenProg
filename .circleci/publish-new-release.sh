#!/usr/bin/env bash

set -eo pipefail

echo 'Checking version...'

VERSION_GIT_TAG="v$(./.circleci/check-version.sh)"

if [[ $? -ne 0 ]]; then
  exit 1
fi


echo 'Installing tcnksm/ghr to manage GitHub Releases...'

go get -u github.com/tcnksm/ghr


echo "Starting to publish a new release as the version: ${WANTED_VERSION} ..."

CHANGELOG="## Change Log

$(cat ./workspace/CHANGELOG.md)"

ghr \
  --token $GITHUB_TOKEN \
  --username $CIRCLE_PROJECT_USERNAME \
  --repository $CIRCLE_PROJECT_REPONAME \
  --commitish $CIRCLE_SHA1 \
  --name "Version $WANTED_VERSION" \
  --body "$CHANGELOG" \
    $VERSION_GIT_TAG ./workspace/artifacts/
