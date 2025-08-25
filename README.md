# Sports Betting Settlement Service

A backend application that simulates sports betting event outcome handling and bet settlement via Kafka and RocketMQ.

## Overview

This application demonstrates:
- Publishing sports event outcomes to Kafka
- Consuming event outcomes and matching them to bets
- Settling bets via RocketMQ (with mock implementation)
- In-memory database for bet storage
- Fully containerized environment with Docker

## Features

- **REST API** for publishing event outcomes and managing bets
- **Kafka Producer/Consumer** for event outcome handling
- **RocketMQ Producer/Consumer** for bet settlement (configurable: mock mode by default, real implementation available)
- **In-memory H2 Database** for bet storage
- **Automatic bet matching** based on event outcomes
- **Docker containerization** with health checks
- **Lombok integration** for clean, boilerplate-free code
- **Comprehensive logging** and monitoring
- **Clean project structure** with organized scripts and Makefile automation

## Getting Started

### Quick Start (Recommended)
```bash
# Option 1: Docker with full setup
./start-docker.sh

# Option 2: Using Makefile (most convenient)
make start
```

### Alternative Methods
```bash
# Docker Compose directly
docker-compose up --build -d

# Local development (no Docker)
make run-local
```

### 4. Access the Application

- **API Base URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

## API Usage

### Create Sample Bets

```bash
# Create a winning bet
curl -X POST http://localhost:8080/api/bets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "eventId": "event001",
    "eventMarketId": "market001",
    "eventWinnerId": "team1",
    "betAmount": 100.0
  }'

# Create a losing bet
curl -X POST http://localhost:8080/api/bets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user456",
    "eventId": "event001",
    "eventMarketId": "market001",
    "eventWinnerId": "team2",
    "betAmount": 50.0
  }'
```

### Publish Event Outcome (Triggers Settlement)

```bash
curl -X POST http://localhost:8080/api/events/outcomes \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "event001",
    "eventName": "Team A vs Team B",
    "eventWinnerId": "team1"
  }'
```

### Get All Bets

```bash
curl -X GET http://localhost:8080/api/bets
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/events/outcomes` | Publish event outcome to Kafka |
| POST | `/api/bets` | Create a new bet |
| GET | `/api/bets` | Get all bets |
| GET | `/api/bets/{id}` | Get bet by ID |
| GET | `/api/bets/user/{userId}` | Get bets for a specific user |
| GET | `/actuator/health` | Health check endpoint |

## Project Structure

```
sports-betting-settlement/
├── src/main/java/com/sportygroup/
│   ├── SportsBettingSettlementApplication.java    # Main Spring Boot application
│   ├── config/                                    # Configuration classes
│   │   ├── AppConfig.java                         # General app configuration
│   │   ├── DataLoader.java                        # Sample data initialization
│   │   └── KafkaConfig.java                       # Kafka producer/consumer setup
│   ├── controller/                                # REST API endpoints
│   │   ├── BetController.java                     # Bet management API
│   │   └── EventOutcomeController.java            # Event outcome API
│   ├── service/                                   # Business logic layer
│   │   ├── BetService.java                        # Bet operations
│   │   ├── BetMatchingService.java                # Bet matching logic
│   │   ├── BetSettlementService.java              # Settlement processing
│   │   └── EventOutcomeService.java               # Event outcome handling
│   ├── model/                                     # Data entities (JPA + Lombok)
│   │   ├── Bet.java                               # Bet entity
│   │   ├── BetStatus.java                         # Bet status enum
│   │   ├── BetSettlement.java                     # Settlement message
│   │   └── EventOutcome.java                      # Event outcome entity
│   ├── repository/                                # Data access layer
│   │   └── BetRepository.java                     # JPA repository
│   ├── consumer/                                  # Kafka consumers
│   │   └── EventOutcomeConsumer.java              # Event outcome processor
│   └── dto/                                       # Data transfer objects
│       ├── CreateBetRequest.java                  # Bet creation DTO
│       └── EventOutcomeRequest.java               # Event outcome DTO
├── src/main/resources/
│   ├── application.yml                            # Default configuration
│   └── application-docker.yml                     # Docker environment config
├── docker-compose.yml                             # Full environment setup
├── Dockerfile                                     # Multi-platform app container
├── Makefile                                       # Development commands
├── start-docker.sh                               # One-command startup
├── scripts/                                       # Utility scripts
│   ├── run.sh                                     # Local development
│   ├── test-api.sh                                # API testing
│   ├── docker-build-multiplatform.sh             # Multi-platform builds
│   ├── test-platform-compatibility.sh            # Platform testing
│   └── validate-setup.sh                          # Setup validation
└── README.md                                      # This file
```

## Architecture

```
API Endpoint → Kafka Producer → event-outcomes topic
                                      ↓
Kafka Consumer → Bet Matching Logic → RocketMQ Producer → bet-settlements topic
                                                                ↓
                                                         RocketMQ Consumer → Bet Settlement
```

### Key Components
- **Controllers**: REST API layer with validation
- **Services**: Business logic for betting and settlement
- **Repositories**: JPA data access with H2 database
- **Kafka Integration**: Event streaming for outcomes
- **RocketMQ Integration**: Settlement message processing
- **Lombok**: Reduces boilerplate code significantly

## Configuration

### Environment Variables (Docker)

- `SPRING_PROFILES_ACTIVE`: Set to `docker` for containerized environment
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka connection string
- `ROCKETMQ_NAME_SERVER`: RocketMQ name server address
- `APP_MOCK_ROCKETMQ`: Set to `true` for mock implementation (default), `false` to use real RocketMQ

### Key Configuration Files

- `application.yml`: Default configuration
- `application-docker.yml`: Docker-specific configuration
- `docker-compose.yml`: Full environment orchestration

## Data Models

### Event Outcome
```json
{
  "eventId": "event001",
  "eventName": "Team A vs Team B",
  "eventWinnerId": "team1"
}
```

### Bet
```json
{
  "betId": 1,
  "userId": "user123",
  "eventId": "event001",
  "eventMarketId": "market001",
  "eventWinnerId": "team1",
  "betAmount": 100.0,
  "status": "PENDING",
  "createdAt": "2025-08-23T10:00:00"
}
```

### Bet Settlement
```json
{
  "betId": 1,
  "userId": "user123",
  "settlementStatus": "WON",
  "payoutAmount": 200.0,
  "settlementTime": "2025-08-23T10:05:00",
  "eventId": "event001"
}
```

## Monitoring and Debugging

### View Service Logs
```bash
# Application logs
docker-compose logs -f sports-betting-app

# Kafka logs
docker-compose logs -f kafka

# All services
docker-compose logs -f
```

### Service Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Check all container status
docker-compose ps
```

### Database Access

Access H2 Console at http://localhost:8080/h2-console:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Development Commands (Makefile)

The project includes a comprehensive Makefile for easy development workflow:

### Core Commands
| Command | Description |
|---------|-------------|
| `make help` | Show all available commands |
| `make start` | Start all services with Docker Compose |
| `make stop` | Stop all services |
| `make restart` | Restart all services with rebuild |
| `make status` | Show service status |
| `make health` | Check service health |

### Development & Testing
| Command | Description |
|---------|-------------|
| `make logs` | Show application logs |
| `make logs-all` | Show all service logs |
| `make quick-test` | Quick API endpoint test |
| `make full-test` | Run comprehensive API tests |
| `make test-platform` | Test platform compatibility |
| `make clean` | Clean up containers and images |

### Build & Deployment
| Command | Description |
|---------|-------------|
| `make build` | Build the Docker image |
| `make build-multiplatform` | Build multi-platform Docker image |
| `make run-local` | Run application locally with Maven |
| `make dev-build` | Build without cache |
| `make dev-shell` | Access application container shell |

### Database & Utilities
| Command | Description |
|---------|-------------|
| `make db-console` | Show H2 database console info |
| `make validate` | Validate Docker setup |

## Testing

### Quick Testing
```bash
# Quick API test (basic functionality)
make quick-test

# Check application health
make health
```

### Comprehensive Testing
```bash
# Full API test suite
make full-test

# Platform compatibility testing
make test-platform
```

### Manual Testing
```bash
# Test individual endpoints
curl -f http://localhost:8080/actuator/health
```

### Sample Test Flow
1. Create multiple bets for the same event
2. Publish event outcome
3. Check bet statuses (should be updated)
4. Verify settlement logs in application output

## Development Notes

- **RocketMQ Implementation**: Mock mode enabled by default for simplicity. Real RocketMQ implementation available by setting `APP_MOCK_ROCKETMQ=false`
- **RocketMQ Configuration Note**: RocketMQ 4.9.4+ may require specific broker configuration for Docker environments. The application gracefully handles broker unavailability by falling back to mock mode while maintaining full functionality
- **In-Memory Database**: All data is reset on restart
- **Auto-created Topics**: Kafka topics are created automatically
- **Health Checks**: Built-in health monitoring for all services
- **Hot Reload**: Application restarts automatically on code changes in development

## Production Considerations

For production deployment, consider:
- External database (PostgreSQL, MySQL)
- Real RocketMQ cluster setup
- Persistent volumes for Kafka
- Load balancing
- Security configuration
- Resource limits and scaling

## Platform Compatibility

This application is built with multi-platform support and runs on:
- ✅ **Mac M1/M2** (Apple Silicon) - Native ARM64
- ✅ **Mac Intel** (x86_64) - Native AMD64
- ✅ **Linux ARM64** - ARM servers, cloud instances
- ✅ **Linux x86_64** - Traditional servers
- ✅ **Docker Desktop** - All platforms

The Dockerfile uses multi-platform build arguments for maximum compatibility across different architectures.
