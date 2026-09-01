# =============================================================
# Estágio 1: Build — compila o projeto e gera o .jar
# =============================================================
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /workspace

# Copiar arquivos de configuração do Gradle primeiro (camada cacheável)
# Só é invalidada se o build.gradle.kts ou settings mudar
COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./

# Baixar dependências antecipadamente (aproveita cache do Docker)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet

# Copiar código-fonte e compilar
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# =============================================================
# Estágio 2: Runtime — imagem mínima, só com JRE
# =============================================================
FROM eclipse-temurin:25-jre-alpine AS runtime

# Usuário não-root por boas práticas de segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiar apenas o .jar gerado no estágio anterior
COPY --from=builder /workspace/build/libs/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# JAVA_OPTS permite configurar heap, GC, etc. via variável de ambiente
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
