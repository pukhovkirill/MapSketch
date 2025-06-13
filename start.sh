#!/usr/bin/env bash
set -e

# 1. Prompt for CORS domains and generate application.yml
read -p "Enter CORS domains (comma-separated): " CORS_DOMAINS

CONFIG_PATH="./MapSketch-Api/src/main/resources/application-prod.yaml"
mkdir -p "$(dirname "$CONFIG_PATH")"

cat > "$CONFIG_PATH" << EOF
spring:
  config:
    import: "optional:file:.env[.properties]"
  datasource:
    driver-class-name: org.postgresql.Driver
    username: \${DATABASE_USER}
    password: \${DATABASE_PASSWORD}
    url: \${DATABASE_DATASOURCE_URL}

cors:
  allowed-origins: $CORS_DOMAINS

mybatis:
  configuration:
    cache-enabled: false
    map-underscore-to-camel-case: true
  mapper-locations: classpath*:mappers/*-mapper.xml

logging:
  level:
    root: info
    com.pukhovkirill.mapsketch: warn
EOF

echo "application-prod.yaml created at: $CONFIG_PATH"

# 2. Run genenvv.sh
if [ -f "./MapSketch-Api/genenvv.sh" ]; then
  chmod +x "./MapSketch-Api/genenvv.sh"
  echo "Running genenvv.sh..."
  (cd "./MapSketch-Api" && ./genenvv.sh)
else
  echo "Warning: genenvv.sh not found in ./MapSketch-Api, skipping."
fi

# 3. Prompt for server domain and generate config.js
read -p "Enter server domain (e.g. http://localhost:8080): " SERVER_DOMAIN

CLIENT_CONFIG="./MapSketch-Web/config.js"
cat > "$CLIENT_CONFIG" << EOF
window.SERVER_HOST = '$SERVER_DOMAIN';
EOF

echo "config.js created at: $CLIENT_CONFIG"

# 4. Run Docker Compose
echo "Starting backend..."
if [ -f "./MapSketch-Api/compose.yaml" ]; then
  (cd "./MapSketch-Api" && docker-compose -f compose.yaml up -d)
else
  echo "Error: compose.yaml not found in ./MapSketch-Api, skipping."
fi

echo "Starting frontend..."
if [ -f "./MapSketch-Web/compose.yaml" ]; then
  (cd "./MapSketch-Web" && docker-compose -f compose.yaml up -d)
else
  echo "Error: compose.yaml not found in ./MapSketch-Web, skipping."
fi

echo "Setup complete!"

