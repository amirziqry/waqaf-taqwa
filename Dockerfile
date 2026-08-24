# Build
FROM eclipse-temurin:25-jdk AS backend-build

WORKDIR /app

COPY gowaqaf-backend/mvnw .
COPY gowaqaf-backend/.mvn .mvn
COPY gowaqaf-backend/pom.xml .

RUN ./mvnw dependency:go-offline

COPY gowaqaf-backend/src src

RUN ./mvnw clean package -DskipTests


# Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=backend-build /app/target/*.jar gowaqaf-backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "gowaqaf-backend.jar"]
