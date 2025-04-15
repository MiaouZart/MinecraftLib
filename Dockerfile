# Étape 1 : Build Maven avec Java 24
FROM eclipse-temurin:24-jdk AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Installer Maven si l’image ne l’inclut pas
RUN apt update && apt install -y maven

RUN mvn clean package -DskipTests

# Étape 2 : Runtime
FROM miaou-app
WORKDIR /app

COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
