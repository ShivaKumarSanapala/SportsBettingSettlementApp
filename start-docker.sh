#!/bin/bash

# Sports Betting Settlement Service - Docker Startup Script

echo "🏈 Sports Betting Settlement Service - Docker Setup"
echo "=================================================="

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo "❌ Docker is not running. Please start Docker Desktop and try again."
        exit 1
    fi
    echo "✅ Docker is running"
}

# Function to check if Docker Compose is available
check_docker_compose() {
    if ! command -v docker-compose > /dev/null 2>&1; then
        echo "❌ Docker Compose is not installed. Please install Docker Compose and try again."
        exit 1
    fi
    echo "✅ Docker Compose is available"
}

# Function to clean up existing containers
cleanup() {
    echo "🧹 Cleaning up existing containers..."
    docker-compose down --volumes --remove-orphans
}

# Function to build and start services
start_services() {
    echo "🚀 Building and starting services..."
    docker-compose up --build -d
}

# Function to show service status
show_status() {
    echo ""
    echo "📊 Service Status:"
    echo "=================="
    docker-compose ps
    
    echo ""
    echo "🔗 Service URLs:"
    echo "================"
    echo "• Sports Betting API: http://localhost:8080"
    echo "• H2 Database Console: http://localhost:8080/h2-console"
    echo "• Health Check: http://localhost:8080/actuator/health"
    echo "• Kafka: localhost:9092"
    echo "• RocketMQ Name Server: localhost:9876"
}

# Function to wait for services to be ready
wait_for_services() {
    echo "⏳ Waiting for services to be ready..."
    
    # Wait for application to be healthy
    max_attempts=30
    attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "✅ Sports Betting Service is ready!"
            break
        fi
        
        echo "⏳ Attempt $attempt/$max_attempts - Waiting for application to start..."
        sleep 5
        attempt=$((attempt + 1))
    done
    
    if [ $attempt -gt $max_attempts ]; then
        echo "❌ Service failed to start within expected time"
        echo "📋 Checking logs..."
        docker-compose logs sports-betting-app
        exit 1
    fi
}

# Function to show logs
show_logs() {
    echo ""
    echo "📋 Recent Application Logs:"
    echo "=========================="
    docker-compose logs --tail=20 sports-betting-app
}

# Function to run tests
run_tests() {
    echo ""
    echo "🧪 Running API Tests:"
    echo "===================="
    
    # Test 1: Create a bet
    echo "1️⃣ Creating a test bet..."
    curl -X POST http://localhost:8080/api/bets \
      -H "Content-Type: application/json" \
      -d '{
        "userId": "user123",
        "eventId": "event001",
        "eventMarketId": "market001",
        "eventWinnerId": "team1",
        "betAmount": 100.0
      }' \
      -w "\nStatus: %{http_code}\n\n"
    
    # Test 2: Get all bets
    echo "2️⃣ Getting all bets..."
    curl -X GET http://localhost:8080/api/bets \
      -H "Content-Type: application/json" \
      -w "\nStatus: %{http_code}\n\n"
    
    # Test 3: Publish event outcome
    echo "3️⃣ Publishing event outcome..."
    curl -X POST http://localhost:8080/api/events/outcomes \
      -H "Content-Type: application/json" \
      -d '{
        "eventId": "event001",
        "eventName": "Team A vs Team B",
        "eventWinnerId": "team1"
      }' \
      -w "\nStatus: %{http_code}\n\n"
}

# Main execution
main() {
    check_docker
    check_docker_compose
    cleanup
    start_services
    wait_for_services
    show_status
    show_logs
    
    echo ""
    echo "🎉 Setup complete! The Sports Betting Settlement Service is running."
    echo ""
    echo "📖 Quick Start:"
    echo "• View API documentation and test endpoints at: http://localhost:8080"
    echo "• Check application logs: docker-compose logs -f sports-betting-app"
    echo "• Stop services: docker-compose down"
    echo ""
    
    # Ask if user wants to run tests
    read -p "Would you like to run API tests? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        run_tests
    fi
    
    echo "✨ Happy betting! 🎰"
}

# Handle script arguments
case "${1:-}" in
    "test")
        run_tests
        ;;
    "logs")
        docker-compose logs -f sports-betting-app
        ;;
    "stop")
        docker-compose down
        ;;
    "restart")
        cleanup
        start_services
        ;;
    *)
        main
        ;;
esac
