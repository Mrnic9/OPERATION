FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY ms_expense/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]