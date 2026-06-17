#Constroi a aplicação usando o Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia o arquivo de dependências e baixa o que for necessário
COPY pom.xml .
COPY src ./src

# Roda o build ignorando os testes para o deploy ser mais rápido
RUN mvn clean package -DskipTests

#Cria a imagem final só com o Java
FROM eclipse-temurin:17-jre
WORKDIR /app

# Pega o arquivo .jar que foi gerado e copia pra cá
COPY --from=build /app/target/*.jar app.jar

# Libera a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]