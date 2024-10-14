# Utilizza l'immagine base con Maven e OpenJDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Setta la directory di lavoro
WORKDIR /app

# Copia il file pom.xml e scarica le dipendenze separatamente (ottimizzazione del caching)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copia tutto il progetto nel container
COPY . .

# Scarica le dipendenze e compila il progetto
RUN mvn clean package -DskipTests

# Seconda fase: utilizza solo l'immagine OpenJDK per eseguire l'applicazione
FROM eclipse-temurin:21-jdk

# Setta la directory di lavoro
WORKDIR /app

# Copia qualsiasi file .jar dalla cartella target
COPY --from=build /app/target/*.jar /app/app.jar

# Espone la porta 8080
EXPOSE 8080

# Comando di esecuzione dell'applicazione
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

