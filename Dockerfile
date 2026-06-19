# ---- Conteneur BACKEND ----
# Stage 1 : build du WAR avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

# Redirige la connexion JDBC vers le service "db" du compose
RUN sed -i 's#jdbc:mysql://localhost:3306/#jdbc:mysql://db:3306/#' \
    src/main/resources/META-INF/persistence.xml

RUN mvn -B -q clean package -DskipTests

# ---- Stage 2 : runtime Tomcat 10 (Jakarta EE 10) ----
FROM tomcat:10.1-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/gestion-cotisations.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
