#!/bin/bash

echo "🔍 Validating Docker setup for Sports Betting Settlement Service"
echo "=============================================================="

# Check if Dockerfile exists
if [ -f "Dockerfile" ]; then
    echo "✅ Dockerfile found"
else
    echo "❌ Dockerfile not found"
    exit 1
fi

# Check if docker-compose.yml exists
if [ -f "docker-compose.yml" ]; then
    echo "✅ docker-compose.yml found"
else
    echo "❌ docker-compose.yml not found"
    exit 1
fi

# Validate Docker Compose file
echo "🔍 Validating docker-compose.yml..."
if docker-compose config > /dev/null 2>&1; then
    echo "✅ docker-compose.yml is valid"
else
    echo "❌ docker-compose.yml validation failed"
    docker-compose config
    exit 1
fi

# Check if pom.xml exists and is valid
if [ -f "pom.xml" ]; then
    echo "✅ pom.xml found"
else
    echo "❌ pom.xml not found"
    exit 1
fi

# Check if source files exist
if [ -d "src/main/java" ]; then
    echo "✅ Java source directory found"
else
    echo "❌ Java source directory not found"
    exit 1
fi

# Check key application files
key_files=(
    "src/main/java/com/sportygroup/SportsBettingSettlementApplication.java"
    "src/main/resources/application.yml"
    "src/main/resources/application-docker.yml"
)

for file in "${key_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file found"
    else
        echo "❌ $file not found"
        exit 1
    fi
done

echo ""
echo "🎉 All validation checks passed!"
echo "🚀 Ready to build and run with Docker"
echo ""
echo "Next steps:"
echo "1. Run: ./start-docker.sh"
echo "2. Or manually: docker-compose up --build"
