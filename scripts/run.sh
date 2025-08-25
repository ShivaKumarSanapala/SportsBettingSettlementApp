#!/bin/bash

# Sports Betting Settlement Service - Build and Run Script

echo "=== Sports Betting Settlement Service ==="
echo "Building and running the application..."
echo

# Check if Java and Maven are installed
echo "Checking prerequisites..."
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"
echo "✅ Maven version: $(mvn -version 2>&1 | head -n 1)"
echo

# Start infrastructure (optional)
echo "Starting infrastructure (Kafka and RocketMQ)..."
echo "Note: The application will work with mock RocketMQ even if Docker is not available"
if command -v docker-compose &> /dev/null; then
    echo "Starting Kafka and RocketMQ with Docker Compose..."
    docker-compose up -d
    echo "Waiting 10 seconds for services to start..."
    sleep 10
else
    echo "⚠️  Docker Compose not found. Using embedded Kafka for testing."
fi
echo

# Build the application
echo "Building the application..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi
echo "✅ Build successful"
echo

# Run the application
echo "Starting the application..."
echo "The application will be available at: http://localhost:8080"
echo "H2 Console will be available at: http://localhost:8080/h2-console"
echo
echo "To test the API, you can:"
echo "1. Run the test script: ./test-api.sh"
echo "2. Import the Postman collection: Sports_Betting_API.postman_collection.json"
echo "3. Use curl commands as shown in the README"
echo
echo "Press Ctrl+C to stop the application"
echo
mvn spring-boot:run
