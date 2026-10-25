# Etapa 1: Construir o projeto (Build)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copia o código fonte para dentro do container
COPY . .
# Compila o projeto ignorando os testes para ser mais rápido
RUN mvn clean package -DskipTests

# Etapa 2: Executar o projeto (Run)
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copia apenas o ficheiro .jar compilado da etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Expõe a porta web
EXPOSE 8080
# Comando para iniciar o servidor Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]