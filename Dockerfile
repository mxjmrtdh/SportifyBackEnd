#Imagen modelo
#FROM eclipse-temurin:22.0.1_8-jdk
FROM eclipse-temurin:21.0.6_7-jdk
#FROM eclipse-temurin:17.0.14_7-jdk

EXPOSE 8080

#definir directorio raiz de nuestro contenedor
WORKDIR /root

#copiar y pegar archivos dentro del contenedor
COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root


#descargar las dependencias
RUN ./mvnw dependency:go-offline

#copiar el codigo fuente en el contenedor
COPY ./src /root/src

#Credenciales del drive
#COPY ./src/main/resources/credentials.json /root/credentials.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/root/src/main/resources/credentials.json

#construir nuestra aplicación
RUN ./mvnw clean package -DskipTests
#clean install

# LEVANTAR NUESTRA APLICACION CUANDO EL CONTENEDOR INICIE
ENTRYPOINT ["java","-jar","/root/target/court_rental-0.0.1-SNAPSHOT.jar"]