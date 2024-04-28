# Use a base image that supports Java 21 (adjust this as needed based on availability)
FROM amazoncorretto:21 AS build

WORKDIR /app

# Copy necessary files to the container
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Build the application
RUN ./gradlew clean build -x test

# Use a base image for the runtime
FROM amazoncorretto:21

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
