FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
#
#RUN addgroup --system spring && adduser --system spring --ingroup spring
#USER spring:spring

COPY target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT [ "java", "-XX:MaxRAMPercentage=75.0", "-XX:TieredStopAtLevel=1", "-noverify", "-jar", "app.jar" ]