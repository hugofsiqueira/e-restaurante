# =============================================================
# Estágio único: copia o JAR pré-compilado pelo host
# Compilar: ./gradlew bootJar -x test
# Subir:    docker compose up --build
# =============================================================
FROM eclipse-temurin:25-jre-alpine AS runtime

# Usuário não-root por boas práticas de segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copia o JAR gerado pelo Gradle no host
COPY build/libs/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# JAVA_OPTS permite configurar heap, GC, etc. via variável de ambiente
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
