#!/bin/bash

# This is a script to push with multiple platform images to docker hub

# login to docker hub
BM_USER="ghkdqhrbals"
BM_PLATFORM="linux/amd64,linux/arm64"
BM_VERSION="latest"

docker buildx create --use

docker buildx inspect --bootstrap

echo "Build the docker image with multi platform support"
docker buildx build --platform ${BM_PLATFORM} -t ${BM_USER}/bm-controller:${BM_VERSION} -f /bm-controller/Dockerfile --push .
docker buildx build --platform ${BM_PLATFORM} -t ${BM_USER}/bm-agent:${BM_VERSION} -f /bm-agent/Dockerfile --push .

# Build the docker image
images=$(docker images --format "{{.Repository}}" | grep "^${PREFIX}")

# Push the docker image to docker hub
echo "Image deploy to docker hub"
for image in $images; do
  echo "${image}"
  docker push "${image}"
done
