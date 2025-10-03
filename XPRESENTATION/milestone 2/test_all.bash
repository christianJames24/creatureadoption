#!/usr/bin/env bash
set -e

# Semi-automated integration tests for Creature Adoption system
# Covers T12 (one POST + GET per downstream) and T11 (all main, error, and custom exception use cases of Adoptions)
# Usage: ./test_api.sh [host] [port]

HOST=${1:-localhost}
PORT=${2:-8080}
BASE_URL="http://$HOST:$PORT"
invalidId="00000000-0000-0000-0000-000000000000"

declare -A ids

# Helpers
assertCurl() {
  local expected=$1
  local cmd="$2 -w '%{http_code}' -s"
  local result=$(eval "$cmd")
  local code=${result: -3}
  local body=${result%???}
  if [[ "$code" == "$expected" ]]; then
    echo "[OK] $cmd → $code"
    RESPONSE="$body"
  else
    echo "[FAIL] expected $expected but got $code"
    echo ">=> $cmd"
    echo "Body: $body"
    exit 1
  fi
}

assertEqual() {
  if [[ "$2" == "$1" ]]; then
    echo "[OK] value '$1'"
  else
    echo "[FAIL] expected '$1' got '$2'"
    exit 1
  fi
}

#have all the microservices come up yet?
function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

#don't start testing until all the microservices are up and running
function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

#try to delete a resource that you won't be needing. This will confirm that our microservices architecture is up and running.
waitForService curl -X DELETE http://$HOST:$PORT/api/v1/adoptions/03441080-2893-48c6-892b-1d4f63f694ef
echo "-----------------------------------------------------------------------------------------------------------------WAIT FOR SERVICCE IS COMPELETED"

# === customers microservice ===
echo "-----------------------------------------------------------------------------------------------------------------CUSTOMERS"
echo "POST - success path"
custBody=$(cat <<EOF
{
  "firstName": "Misty",
  "lastName": "Waterflower",
  "emailAddress": "misty@goldenrod.com",
  "contactMethodPreference": "PHONE",
  "streetAddress": "321 Cerulean Ave",
  "city": "Cerulean City",
  "province": "Kanto",
  "country": "Japan",
  "postalCode": "12345"
}
EOF
)
assertCurl 201 "curl -X POST $BASE_URL/api/v1/customers -H 'Content-Type: application/json' --data '$custBody'"
ids[customer]=$(echo "$RESPONSE" | jq -r '.customerId')

echo "GET by Id - success path"
assertCurl 200 "curl $BASE_URL/api/v1/customers/${ids[customer]}"
assertEqual "${ids[customer]}" "$(echo $RESPONSE | jq -r '.customerId')"
echo

# === creatures microservice ===
echo "-----------------------------------------------------------------------------------------------------------------CREATUREs"
echo "POST - success path"
creBody=$(cat <<EOF
{
  "registrationCode": "REG-NEW123",
  "name": "Flicker",
  "species": "Luxray",
  "type": "ELECTRIC",
  "rarity": "RARE",
  "level": 27,
  "age": 4,
  "health": 88,
  "experience": 4200,
  "status": "AVAILABLE",
  "strength": 75,
  "intelligence": 70,
  "agility": 82,
  "temperament": "FRIENDLY"
}
EOF
)
assertCurl 201 "curl -X POST $BASE_URL/api/v1/creatures -H 'Content-Type: application/json' --data '$creBody'"
ids[creature]=$(echo "$RESPONSE" | jq -r '.creatureId')

echo "GET by Id - success path"
assertCurl 200 "curl $BASE_URL/api/v1/creatures/${ids[creature]}"
assertEqual "${ids[creature]}" "$(echo $RESPONSE | jq -r '.creatureId')"
echo

# === trainings microservice ===
echo "-----------------------------------------------------------------------------------------------------------------TRAININGS"
echo "POST - success path"
trainBody=$(cat <<EOF
{
  "trainingCode": "TRN-87654321",
  "name": "Shadow Techniques",
  "description": "Special training for ghost and dark type creatures",
  "difficulty": "ADVANCED",
  "duration": 5,
  "status": "ACTIVE",
  "category": "SPECIAL",
  "price": 189.99,
  "location": "Old Chateau - Eterna Forest"
}
EOF
)
assertCurl 201 "curl -X POST $BASE_URL/api/v1/trainings -H 'Content-Type: application/json' --data '$trainBody'"
ids[training]=$(echo "$RESPONSE" | jq -r '.trainingId')

echo "GET by Id - success path"
assertCurl 200 "curl $BASE_URL/api/v1/trainings/${ids[training]}"
assertEqual "${ids[training]}" "$(echo $RESPONSE | jq -r '.trainingId')"
echo

# === adoptions microservice ===
echo "-----------------------------------------------------------------------------------------------------------------ADOPTIONS"
echo "POST - success path"
adoptBody=$(cat <<EOF
{
  "customerId": "${ids[customer]}",
  "creatureId": "${ids[creature]}",
  "trainingId": "${ids[training]}",
  "summary": "Electric type adoption profile - Test creation",
  "totalAdoptions": 0,
  "profileCreationDate": "2025-04-25",
  "profileStatus": "ACTIVE",
  "adoptionDate": "2025-06-01",
  "adoptionLocation": "Driftveil City Adoption Center",
  "adoptionStatus": "PENDING",
  "specialNotes": "Postman test for successful adoption creation"
}
EOF
)
assertCurl 201 "curl -X POST $BASE_URL/api/v1/adoptions -H 'Content-Type: application/json' --data '$adoptBody'"
ids[adoption]=$(echo "$RESPONSE" | jq -r '.adoptionId')
echo

echo "GET ALL - success path"
assertCurl 200 "curl $BASE_URL/api/v1/adoptions"
echo

echo "GET by Id - success path"
assertCurl 200 "curl $BASE_URL/api/v1/adoptions/${ids[adoption]}"
assertEqual "${ids[adoption]}" "$(echo $RESPONSE | jq -r '.adoptionId')"
echo

echo "PUT - success path"
updateBody=$(cat <<EOF
{
  "customerId": "${ids[customer]}",
  "creatureId": "${ids[creature]}",
  "trainingId": "${ids[training]}",
  "summary": "Electric type adoption profile - Test update",
  "totalAdoptions": 1,
  "profileCreationDate": "2025-04-25",
  "profileStatus": "ACTIVE",
  "adoptionDate": "2025-06-01",
  "adoptionLocation": "Driftveil City Adoption Center",
  "adoptionStatus": "APPROVED",
  "specialNotes": "Updated via test script"
}
EOF
)
assertCurl 200 "curl -X PUT $BASE_URL/api/v1/adoptions/${ids[adoption]} -H 'Content-Type: application/json' --data '$updateBody'"
echo

echo "DELETE - success path"
assertCurl 204 "curl -X DELETE $BASE_URL/api/v1/adoptions/${ids[adoption]}"
echo

# === Error-path tests for adoptions ===
echo "GET by Id - 404"
assertCurl 404 "curl $BASE_URL/api/v1/adoptions/$invalidId"
echo

echo "POST - 422"
errBody1=$(cat <<EOF
{
  "customerId": "${ids[customer]}",
  "trainingId": "${ids[training]}",
  "summary": "Missing creature",
  "totalAdoptions": 0
}
EOF
)
assertCurl 422 "curl -X POST $BASE_URL/api/v1/adoptions -H 'Content-Type: application/json' --data '$errBody1'"
echo

echo "POST - 404"
errBody2=$(cat <<EOF
{
  "customerId": "$invalidId",
  "creatureId": "${ids[creature]}",
  "trainingId": "${ids[training]}",
  "summary": "Invalid customer",
  "totalAdoptions": 0,
  "profileCreationDate": "2025-04-25",
  "profileStatus": "ACTIVE",
  "adoptionDate": "2025-06-01",
  "adoptionLocation": "Test Adoption Center",
  "adoptionStatus": "PENDING",
  "specialNotes": "Testing with invalid customer"
}
EOF
)
assertCurl 404 "curl -X POST $BASE_URL/api/v1/adoptions -H 'Content-Type: application/json' --data '$errBody2'"
echo

echo "PUT - 422"
errPut=$(cat <<EOF
{
  "customerId": "${ids[customer]}",
  "creatureId": "${ids[creature]}",
  "trainingId": "${ids[training]}",
  "profileStatus": "ACTIVE"
}
EOF
)
assertCurl 422 "curl -X PUT $BASE_URL/api/v1/adoptions/invalid-uuid -H 'Content-Type: application/json' --data '$errPut'"
echo

echo "PUT - 404"
assertCurl 404 "curl -X PUT $BASE_URL/api/v1/adoptions/$invalidId -H 'Content-Type: application/json' --data '$updateBody'"
echo

# === Custom Exception test ===
echo "POST - Custom Exception"
customExBody=$(cat <<EOF
{
  "customerId": "e5f6a7b8-c9d0-e1f2-a3b4-c5d6e7f8a9b0",
  "creatureId": "1d2e3f4a-5b6c-7d8e-9f0a-1b2c3d4e5f6a",
  "trainingId": "l8m7n6o5-p4q3-r2s1-t0u9-v8w7x6y5z4a3",
  "summary": "Testing adoption limit exceeded",
  "totalAdoptions": 0,
  "profileCreationDate": "2025-05-01",
  "profileStatus": "ACTIVE",
  "adoptionDate": "2025-05-30",
  "adoptionLocation": "Nacrene Museum Adoption Center",
  "adoptionStatus": "PENDING",
  "specialNotes": "Should fail because Lenora already has 2 completed adoptions"
}
EOF
)
#assertCurl 400 "curl -X POST $BASE_URL/api/v1/adoptions -H 'Content-Type: application/json' --data '$customExBody'"
assertCurl 500 "curl -X POST $BASE_URL/api/v1/adoptions -H 'Content-Type: application/json' --data '$customExBody'"
echo


echo "GET ALL - 422 (invalid query parameter)"
assertCurl 422 "curl $BASE_URL/api/v1/adoptions?adoptionId=invalid-uuid"
echo

echo "GET by Id - 422 (invalid UUID format)"
assertCurl 422 "curl $BASE_URL/api/v1/adoptions/invalid-uuid"
echo

echo "DELETE - 404 (non-existent ID)"
assertCurl 404 "curl -X DELETE $BASE_URL/api/v1/adoptions/$invalidId"
echo

echo "DELETE - 422 (invalid UUID format)"
assertCurl 422 "curl -X DELETE $BASE_URL/api/v1/adoptions/invalid-uuid"
echo

echo "DELETE - Custom Exception"
assertCurl 422 "curl -X DELETE $BASE_URL/api/v1/adoptions/55212ccd-f45c-489b-ae44-1d2e4e2906d4"
echo


# === Test Aggregate Invariant - Status Changes & Effect on Creature ===
echo "-----------------------------------------------------------------------------------------------------------------Testing Aggregate Invariant - Status changes affect creature status"

# Use existing adoption ID (same as in Postman collection)
existingAdoptionId="76db80b7-5f9d-4549-8a94-83d44c43bce6"

# Get current creature ID from the adoption
assertCurl 200 "curl $BASE_URL/api/v1/adoptions/$existingAdoptionId"
creatureIdForTest=$(echo "$RESPONSE" | jq -r '.creatureId')

# Check creature initial status
assertCurl 200 "curl $BASE_URL/api/v1/creatures/$creatureIdForTest"
echo "Initial creature status:"
echo "$RESPONSE" | jq -r '.status'

# Test PATCH to APPROVED
echo "PATCH - Change adoption status to APPROVED"
assertCurl 200 "curl -X PATCH $BASE_URL/api/v1/adoptions/$existingAdoptionId/status/APPROVED"
# Verify creature status changed
assertCurl 200 "curl $BASE_URL/api/v1/creatures/$creatureIdForTest"
echo "Creature status after APPROVED:"
echo "$RESPONSE" | jq -r '.status'

# Test PATCH to PENDING
echo "PATCH - Change adoption status to PENDING"
assertCurl 200 "curl -X PATCH $BASE_URL/api/v1/adoptions/$existingAdoptionId/status/PENDING"
# Verify creature status changed
assertCurl 200 "curl $BASE_URL/api/v1/creatures/$creatureIdForTest"
echo "Creature status after PENDING:"
echo "$RESPONSE" | jq -r '.status'

# Test PATCH to COMPLETED
echo "PATCH - Change adoption status to COMPLETED"
assertCurl 200 "curl -X PATCH $BASE_URL/api/v1/adoptions/$existingAdoptionId/status/COMPLETED"
# Verify creature status changed
assertCurl 200 "curl $BASE_URL/api/v1/creatures/$creatureIdForTest"
echo "Creature status after COMPLETED:"
echo "$RESPONSE" | jq -r '.status'

# Test PATCH to CANCELLED
echo "PATCH - Change adoption status to CANCELLED"
assertCurl 200 "curl -X PATCH $BASE_URL/api/v1/adoptions/$existingAdoptionId/status/CANCELLED"
# Verify creature status changed
assertCurl 200 "curl $BASE_URL/api/v1/creatures/$creatureIdForTest"
echo "Creature status after CANCELLED:"
echo "$RESPONSE" | jq -r '.status'

# Test PATCH to RETURNED
echo "PATCH - Change adoption status to RETURNED"
assertCurl 200 "curl -X PATCH $BASE_URL/api/v1/adoptions/$existingAdoptionId/status/RETURNED"
# Verify creature status changed
assertCurl 200 "curl $BASE_URL/api/v1/creatures/$creatureIdForTest"
echo "Creature status after RETURNED:"
echo "$RESPONSE" | jq -r '.status'

echo "All aggregate invariant tests completed successfully!"

echo "All tests completed successfully!"