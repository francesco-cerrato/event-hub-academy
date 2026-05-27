# Stage 1: Compilazione del codice sorgente tramite Maven ed JDK 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copia i file di configurazione e il codice sorgente nel container
COPY pom.xml .
COPY src ./src
# Compila il progetto saltando l'esecuzione dei test per velocizzare l'avvio del container
RUN mvn clean package -DskipTests

# Stage 2: Creazione dell'immagine finale leggera per l'esecuzione usando Eclipse Temurin
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia il file .jar generato dallo stage precedente
COPY --from=build /app/target/*.jar app.jar
# Espone la porta interna utilizzata dall'applicazione
EXPOSE 8081
# Comando per avviare l'applicazione Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

