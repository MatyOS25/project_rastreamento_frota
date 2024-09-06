# Use uma imagem base oficial do OpenJDK 22
FROM openjdk:22-slim

# Defina o diretório de trabalho no container
WORKDIR /app

# Copie o arquivo JAR pré-compilado para o container
COPY target/*.jar app.jar

# Copie o arquivo logback-spring.xml para o container
COPY src/main/resources/logback-spring.xml /app/logback-spring.xml

# Exponha a porta que a aplicação usa
EXPOSE 8080
EXPOSE 9876/udp

# Defina variáveis de ambiente (opcional, pode ser sobrescrito no Kubernetes)
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]