#!/usr/bin/env bash

set -e

./gradlew clean installDist

yes | docker-compose -f docker/docker-compose.yaml kill
yes | docker-compose -f docker/docker-compose.yaml rm
docker-compose -f docker/docker-compose.yaml build
docker-compose -f docker/docker-compose.yaml up
