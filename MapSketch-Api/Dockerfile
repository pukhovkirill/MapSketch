FROM eclipse-temurin:21 AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw package -DskipTests=true

FROM eclipse-temurin:21

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
