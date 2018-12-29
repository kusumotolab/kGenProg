#!/usr/bin/env bash

set -eo pipefail

echo 'Checking version...'

WANTED_VERSION="$(./.circleci/check-version.sh)"
VERSION_GIT_TAG="v${WANTED_VERSION}"

if [[ $WANTED_VERSION = "EXISTING" ]]; then
  echo "This version has been already released. Abort."
  exit 0
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
