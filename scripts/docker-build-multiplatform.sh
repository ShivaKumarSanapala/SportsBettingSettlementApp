#!/bin/bash

# Multi-platform Docker build script for Sports Betting Settlement Service
# Supports: linux/amd64 (Intel/AMD), linux/arm64 (Apple M1/M2, ARM servers)

set -e

echo "🏗️  Multi-Platform Docker Build"
echo "==============================="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
IMAGE_NAME="sports-betting-settlement"
TAG="latest"
PLATFORMS="linux/amd64,linux/arm64"

# Check if Docker buildx is available
if ! docker buildx version >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker buildx is not available. Please update Docker to a newer version.${NC}"
    exit 1
fi

# Create a new builder instance if it doesn't exist
BUILDER_NAME="sports-betting-builder"
if ! docker buildx ls | grep -q $BUILDER_NAME; then
    echo -e "${BLUE}🔧 Creating new buildx builder: $BUILDER_NAME${NC}"
    docker buildx create --name $BUILDER_NAME --use
else
    echo -e "${BLUE}🔧 Using existing buildx builder: $BUILDER_NAME${NC}"
    docker buildx use $BUILDER_NAME
fi

# Inspect the builder
echo -e "${BLUE}🔍 Builder information:${NC}"
docker buildx inspect --bootstrap

echo -e "${YELLOW}📦 Building multi-platform image...${NC}"
echo -e "   Image: ${IMAGE_NAME}:${TAG}"
echo -e "   Platforms: ${PLATFORMS}"

# Build for multiple platforms
docker buildx build \
    --platform $PLATFORMS \
    --tag $IMAGE_NAME:$TAG \
    --load \
    .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Multi-platform build completed successfully!${NC}"
    echo
    echo -e "${BLUE}📋 Image Details:${NC}"
    docker image inspect $IMAGE_NAME:$TAG --format 'Architecture: {{.Architecture}}'
    docker image inspect $IMAGE_NAME:$TAG --format 'OS: {{.Os}}'
    echo
    echo -e "${BLUE}🚀 To push to registry:${NC}"
    echo "   docker buildx build --platform $PLATFORMS --tag $IMAGE_NAME:$TAG --push ."
else
    echo -e "${RED}❌ Build failed!${NC}"
    exit 1
fi

echo
echo -e "${GREEN}🎉 Ready for deployment on multiple platforms:${NC}"
echo -e "   • Linux x86_64 (Intel/AMD servers, older Macs)"
echo -e "   • Linux ARM64 (Apple M1/M2, ARM servers, Raspberry Pi)"
