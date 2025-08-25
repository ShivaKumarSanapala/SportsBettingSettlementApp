#!/bin/bash

# Sports Betting Settlement Service - Test Script

echo "=== Sports Betting Settlement Service Test Script ==="
echo

BASE_URL="http://localhost:8080"

# Check if application is running
echo "1. Checking if application is running..."
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/bets" > /tmp/status_code
STATUS_CODE=$(cat /tmp/status_code)

if [ "$STATUS_CODE" != "200" ]; then
    echo "❌ Application is not running on $BASE_URL"
    echo "Please start the application first with: mvn spring-boot:run"
    exit 1
fi
echo "✅ Application is running"
echo

# Test 1: Get all existing bets
echo "2. Getting all existing bets..."
echo "GET $BASE_URL/api/bets"
curl -X GET "$BASE_URL/api/bets" -H "Content-Type: application/json" | jq '.'
echo
echo

# Test 2: Create a new bet
echo "3. Creating a new bet..."
echo "POST $BASE_URL/api/bets"
NEW_BET='{
    "userId": "testUser001",
    "eventId": "event003",
    "eventMarketId": "market001",
    "eventWinnerId": "teamX",
    "betAmount": 250.00
}'
echo "Request body: $NEW_BET"
curl -X POST "$BASE_URL/api/bets" \
    -H "Content-Type: application/json" \
    -d "$NEW_BET" | jq '.'
echo
echo

# Test 3: Publish event outcome that matches existing bets
echo "4. Publishing event outcome for event001 (should trigger settlements)..."
echo "POST $BASE_URL/api/events/outcomes"
EVENT_OUTCOME='{
    "eventId": "event001",
    "eventName": "Team A vs Team B - Championship Final",
    "eventWinnerId": "team1"
}'
echo "Request body: $EVENT_OUTCOME"
curl -X POST "$BASE_URL/api/events/outcomes" \
    -H "Content-Type: application/json" \
    -d "$EVENT_OUTCOME"
echo
echo

# Wait a bit for async processing
echo "5. Waiting 3 seconds for settlement processing..."
sleep 3
echo

# Test 4: Check bets again to see updated statuses
echo "6. Getting all bets again to see settlement results..."
echo "GET $BASE_URL/api/bets"
curl -X GET "$BASE_URL/api/bets" -H "Content-Type: application/json" | jq '.'
echo
echo

# Test 5: Publish another event outcome
echo "7. Publishing event outcome for the new bet (event003)..."
echo "POST $BASE_URL/api/events/outcomes"
EVENT_OUTCOME_2='{
    "eventId": "event003",
    "eventName": "Team X vs Team Y - League Match",
    "eventWinnerId": "teamX"
}'
echo "Request body: $EVENT_OUTCOME_2"
curl -X POST "$BASE_URL/api/events/outcomes" \
    -H "Content-Type: application/json" \
    -d "$EVENT_OUTCOME_2"
echo
echo

# Wait a bit for async processing
echo "8. Waiting 2 seconds for settlement processing..."
sleep 2
echo

# Test 6: Final check of all bets
echo "9. Final check - Getting all bets to see final settlement results..."
echo "GET $BASE_URL/api/bets"
curl -X GET "$BASE_URL/api/bets" -H "Content-Type: application/json" | jq '.'
echo
echo

echo "=== Test Script Completed ==="
echo "Check the application logs to see the settlement processing details."
echo "You can also access the H2 console at: $BASE_URL/h2-console"
echo "JDBC URL: jdbc:h2:mem:testdb, Username: sa, Password: password"
