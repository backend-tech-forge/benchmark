#!/bin/bash

# Build the docker image
echo "Build the docker image"
PREFIX="ghkdqhrbals"
docker compose -f ../docker-compose.yaml build

images=$(docker images --format "{{.Repository}}" | grep "^${PREFIX}")

# Push the docker image to docker hub
echo "Image deploy to docker hub"
for image in $images; do
  echo "${image}"
  docker tag "${image}" "${image}:amd64"
  docker push "${image}:amd64"
done
