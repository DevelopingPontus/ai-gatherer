#!/bin/sh

# Load .env file if it exists
if [ -f .env ]; then
  export $(cat .env | grep -v '#' | xargs)
fi

# Run the Java app
exec java -jar app.jar
