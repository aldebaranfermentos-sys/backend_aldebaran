# Usa una imagen base con Java 21. Eclipse Temurin es una opción común y segura.
FROM eclipse-temurin:21-jdk-jammy

# Establece el directorio de trabajo dentro del contenedor.
# Esto hace que las rutas de los comandos COPY y CMD sean relativas a este directorio.
WORKDIR /app

# Copia el archivo JAR de tu máquina local al contenedor.
# Asegúrate de que el archivo Inventario-0.0.1-SNAPSHOT.jar se encuentra en el
# mismo directorio que este Dockerfile cuando construyas la imagen.
COPY Inventario-0.0.1-SNAPSHOT.jar app.jar

# El comando a ejecutar al iniciar el contenedor.
# Ejecuta el archivo JAR. Usa 'java -jar' para ejecutar la aplicación.
CMD ["java", "-jar", "app.jar"]