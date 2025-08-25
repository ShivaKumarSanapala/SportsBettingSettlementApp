# Sports Betting Settlement Service - Makefile

.PHONY: help build start stop restart logs test clean validate

# Default target
help: ## Show this help message
	@echo "Sports Betting Settlement Service - Available Commands:"
	@echo "======================================================"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

validate: ## Validate Docker setup
	@./scripts/validate-setup.sh

build: ## Build the Docker image
	@echo "🔨 Building Docker image..."
	@docker-compose build

build-multiplatform: ## Build multi-platform Docker image
	@echo "🏗️  Building multi-platform image..."
	@./scripts/docker-build-multiplatform.sh

start: ## Start all services with Docker Compose
	@echo "🚀 Starting services..."
	@./start-docker.sh

stop: ## Stop all services
	@echo "🛑 Stopping services..."
	@docker-compose down

restart: ## Restart all services
	@echo "🔄 Restarting services..."
	@docker-compose down
	@docker-compose up --build -d

logs: ## Show application logs
	@echo "📋 Showing application logs..."
	@docker-compose logs -f sports-betting-app

logs-all: ## Show all service logs
	@echo "📋 Showing all service logs..."
	@docker-compose logs -f

test: ## Run API tests
	@echo "🧪 Running API tests..."
	@./scripts/test-api.sh

test-platform: ## Test platform compatibility
	@echo "🧪 Testing platform compatibility..."
	@./scripts/test-platform-compatibility.sh

status: ## Show service status
	@echo "📊 Service status:"
	@docker-compose ps

clean: ## Clean up containers and images
	@echo "🧹 Cleaning up..."
	@docker-compose down --volumes --remove-orphans
	@docker system prune -f

health: ## Check service health
	@echo "🩺 Checking service health..."
	@curl -f http://localhost:8080/actuator/health || echo "❌ Service not healthy"

# Development commands
dev-build: ## Build without cache
	@docker-compose build --no-cache

dev-shell: ## Access application container shell
	@docker-compose exec sports-betting-app /bin/bash

dev-maven: ## Run Maven commands in container
	@docker-compose exec sports-betting-app mvn $(CMD)

# Database commands
db-console: ## Open H2 database console URL
	@echo "🗄️  Opening H2 Console: http://localhost:8080/h2-console"
	@echo "   JDBC URL: jdbc:h2:mem:testdb"
	@echo "   Username: sa"
	@echo "   Password: password"

# Quick test commands
quick-test: ## Quick API test (basic endpoints)
	@echo "🔥 Quick API test..."
	@echo "Creating a test bet..."
	@curl -s -X POST http://localhost:8080/api/bets -H "Content-Type: application/json" -d '{"userId":"user123","eventId":"event001","eventMarketId":"market001","eventWinnerId":"team1","betAmount":100.0}' | head -c 100 && echo "..."
	@echo "Publishing event outcome..."
	@curl -s -X POST http://localhost:8080/api/events/outcomes -H "Content-Type: application/json" -d '{"eventId":"event001","eventName":"Test Event","eventWinnerId":"team1"}' && echo ""
	@echo "Getting all bets..."
	@curl -s -X GET http://localhost:8080/api/bets | head -c 200 && echo "..."
	@echo "✅ Quick test completed"

full-test: ## Run comprehensive API tests
	@echo "🧪 Running comprehensive API tests..."
	@./scripts/test-api.sh

# Local development (without Docker)
run-local: ## Run application locally with Maven
	@echo "🏃 Running application locally..."
	@./scripts/run.sh

# RocketMQ mode switching
start-mock: ## Start with mock RocketMQ
	@echo "🚀 Starting with mock RocketMQ..."
	@APP_MOCK_ROCKETMQ=true docker-compose up --build -d

start-real: ## Start with real RocketMQ
	@echo "🚀 Starting with real RocketMQ..."
	@APP_MOCK_ROCKETMQ=false docker-compose up --build -d
