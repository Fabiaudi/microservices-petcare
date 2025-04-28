# Usa uma imagem oficial do Java 21
FROM eclipse-temurin:21-jdk

# Cria a pasta da aplicação dentro do container
WORKDIR /app

# Copia o jar gerado para dentro do container
COPY target/eurekaServer-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta 8761 (Eureka Server)
EXPOSE 8761

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
