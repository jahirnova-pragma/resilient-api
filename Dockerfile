# Etapa de construcción
FROM amazoncorretto:17-alpine AS build

WORKDIR /app

# Copiar Gradle wrapper y archivos de proyecto
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Dar permisos y construir
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# Etapa de ejecución
FROM amazoncorretto:17-alpine

WORKDIR /app

# Copiar JAR generado
COPY --from=build /app/build/libs/resilient-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
