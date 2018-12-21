#!/usr/bin/env bash

set -eo pipefail

GITHUB_TAG_URL_BASE="https://github.com/${CIRCLE_PROJECT_USERNAME}/${CIRCLE_PROJECT_REPONAME}/releases/tag"


# Analyze target version to be release

WANTED_VERSION="$(cat ./artifacts/current-version.txt)"
WANTED_VERSION="$(echo $WANTED_VERSION)" # Trim whitespaces

VERSION_GIT_TAG="v${WANTED_VERSION}"


# Ensure version name follows semantic versioning

VERSION_REGEX='^[0-9](\.[0-9]){2}$'
if [[ ! $WANTED_VERSION =~ $VERSION_REGEX ]]; then
  echo "[ERROR] Version number $WANTED_VERSION is not valid." >&2
  echo "Please follow 3-number semantic versioning system." >&2
  exit 1
fi


# Check published releases

HTTP_STATUS=$(curl -LI "${GITHUB_TAG_URL_BASE}/${VERSION_GIT_TAG}" -o /dev/null -w '%{http_code}\n' -s)

if [[ $HTTP_STATUS = '400' ]]; then
  echo "[ERROR] Version number $WANTED_VERSION cannot be used as a tag." >&2
  exit 1
elif [[ $HTTP_STATUS = '200' ]]; then
  echo "EXISTING"
  exit 0
fi

echo $WANTED_VERSION
