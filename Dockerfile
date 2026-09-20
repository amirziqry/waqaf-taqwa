# Build
FROM eclipse-temurin:25-jdk AS backend-build

WORKDIR /app

COPY waqaf-taqwa-backend/mvnw .
COPY waqaf-taqwa-backend/.mvn .mvn
COPY waqaf-taqwa-backend/pom.xml .

RUN ./mvnw dependency:go-offline

COPY waqaf-taqwa-backend/src src

RUN ./mvnw clean package -DskipTests


# Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=backend-build /app/target/*.jar waqaf-taqwa-backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "waqaf-taqwa-backend.jar"]
