#!/usr/bin/env bash

set -eo pipefail

echo 'Checking version...'

VERSION=$(./.github/check-version.sh)

if [[ $VERSION == "" ]]; then
  exit 0 # exit as status 0 to finish GitHub Actions as success
fi


echo 'Installing tcnksm/ghr to manage GitHub Releases...'

go get -u github.com/tcnksm/ghr
go version


echo "Starting to publish a new release as the version: $VERSION..."

# remove nightly-build jar
rm ./workspace/artifacts/*-nightly-build.jar

# generate change log
CHANGELOG="## Change Log

$(cat ./workspace/CHANGELOG.md)"

ARRAY=( `echo $GITHUB_REPOSITORY | tr -s '/' ' '`)
USER_NAME=${ARRAY[0]}
REPO_NAME=${ARRAY[1]}
GOPATH=$(go env GOPATH)

$GOPATH/bin/ghr \
  --token $GITHUB_TOKEN \
  --username $USER_NAME \
  --repository $REPO_NAME \
  --commitish $COMMIT_SHA \
  --name "Version $VERSION" \
  --body "$CHANGELOG" \
    v$VERSION ./workspace/artifacts/
