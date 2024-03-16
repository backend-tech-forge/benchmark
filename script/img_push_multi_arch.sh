#!/bin/bash

DEFAULT_USER="ghkdqhrbals"
DEFAULT_PLATFORM="linux/amd64,linux/arm64"
DEFAULT_VERSION="latest"

# Parse command-line arguments
# Parse command-line arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        -u|--username)
            BM_USER="$2"
            shift
            ;;
        -p|--platform)
            BM_PLATFORM="$2"
            shift
            ;;
        -v|--version)
            BM_VERSION="$2"
            shift
            ;;
        -t|--token)
            DOCKER_HUB_TOKEN="$2"
            shift
            ;;
        *)
            echo "Invalid argument: $1"
            exit 1
            ;;
    esac
    shift
done

BM_USER=${BM_USER:-$DEFAULT_USER}
BM_PLATFORM=${BM_PLATFORM:-$DEFAULT_PLATFORM}
BM_VERSION=${BM_VERSION:-$DEFAULT_VERSION}

# Check if required arguments are provided
if [ -z "$BM_USER" ] || [ -z "$BM_PLATFORM" ] || [ -z "$BM_VERSION" ] || [ -z "$DOCKER_HUB_TOKEN" ]; then
    echo "Usage: $0 [-u|--username <username>] [-p|--platform <platform>] [-v|--version <version>] [-t|--token <docker_hub_token>]"
    exit 1
fi

# This is a script to push with multiple platform images to docker hub
echo "Logging in to Docker Hub"
docker login -u "$BM_USER" -p $DOCKER_HUB_TOKEN

# Use Docker Buildx
echo "Using Docker Buildx"
docker buildx create --use
docker buildx inspect --bootstrap

# Build and push Docker images for multiple platforms
echo "Build the docker image with multi platform support"
docker buildx build --platform ${BM_PLATFORM} -t ${BM_USER}/bm-controller:${BM_VERSION} -f /bm-controller/Dockerfile --push .
docker buildx build --platform ${BM_PLATFORM} -t ${BM_USER}/bm-agent:${BM_VERSION} -f /bm-agent/Dockerfile --push .

# Push the docker image to docker hub
images=$(docker images --format "{{.Repository}}" | grep "^${PREFIX}")
echo "Image deploy to docker hub"
for image in $images; do
  echo "${image}"
  docker push "${image}"
done
