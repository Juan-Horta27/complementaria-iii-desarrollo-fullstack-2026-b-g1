# Receta para construir la imagen de la API de ScoreSound.

# Imagen base: Java 17 (la misma version que usa el proyecto), solo para ejecutar
FROM eclipse-temurin:17-jre

# Copia el jar que genera Maven (.\mvnw.cmd package -DskipTests) dentro de la imagen
COPY target/scoresound-api-0.0.1-SNAPSHOT.jar /app.jar

# La API escucha en el puerto 8080
EXPOSE 8080

# Comando que se ejecuta al arrancar el contenedor
ENTRYPOINT ["java", "-jar", "/app.jar"]
