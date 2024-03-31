#!/bin/bash

DEFAULT_USER="ghkdqhrbals"
DEFAULT_PLATFORM="linux/amd64,linux/arm64"
DEFAULT_VERSION="latest"

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
if [ -z "$BM_USER" ] || [ -z "$BM_PLATFORM" ] || [ -z "$BM_VERSION" ] ; then
    echo "Usage: $0 [-u|--username <username>] [-p|--platform <platform>] [-v|--version <version>] "
    exit 1
fi

# This is a script to push with multiple platform images to docker hub
echo "Logging in to Docker Hub (LOCAL)"
docker login

echo "PWD"
pwd


# Use Docker Buildx
echo "Using Docker Buildx"
docker buildx create --use
docker buildx inspect --bootstrap

# Check if ./gradlew build succeeded
if ! ./gradlew clean build; then
    echo "Gradle build failed. Exiting."
    exit 1
fi

# Build and push Docker images for multiple platforms
echo "Build the docker image with multi platform support"

# if you don't have eureka image, run below script
#cd eureka
#pwd
#docker buildx build --platform ${BM_PLATFORM} -t ${BM_USER}/bm-eureka:${BM_VERSION} . --push

cd bm-controller
pwd
docker buildx build --platform ${BM_PLATFORM} -t ${BM_USER}/bm-controller:${BM_VERSION} . --push
pwd
cd ../bm-agent
docker buildx build --platform ${BM_PLATFORM} -t ${BM_USER}/bm-agent:${BM_VERSION} . --push
cd ..
pwd

# Push the docker image to docker hub
images=$(docker images --format "{{.Repository}}" | grep "^${BM_USER}")
echo "Image deploy to docker hub"
for image in $images; do
  echo "${image}"
  docker push "${image}"
done
