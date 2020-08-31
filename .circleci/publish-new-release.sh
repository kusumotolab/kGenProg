#!/usr/bin/env bash

set -eo pipefail

echo 'Checking version...'

VERSION=$(./.circleci/check-version.sh)

if [[ $VERSION == "" ]]; then
  exit 0 # exit as status 0 to finish circleci as success
fi


echo 'Installing tcnksm/ghr to manage GitHub Releases...'

go get -u github.com/tcnksm/ghr


echo "Starting to publish a new release as the version: ${WANTED_VERSION} ..."

# # trim suffix "-all" of fat jar
# jar=$(ls ./workspace/artifacts/*.jar)
# mv $jar ${jar/-all/}

# generate change log
CHANGELOG="## Change Log

$(cat ./workspace/CHANGELOG.md)"

ghr \
  --token $GITHUB_TOKEN \
  --username $CIRCLE_PROJECT_USERNAME \
  --repository $CIRCLE_PROJECT_REPONAME \
  --commitish $CIRCLE_SHA1 \
  --name "Version $WANTED_VERSION" \
  --body "$CHANGELOG" \
    $VERSION ./workspace/artifacts/
