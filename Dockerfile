FROM openjdk:17-jdk

WORKDIR /app

COPY "target/vocabulary-quiz-0.0.1-SNAPSHOT.jar" "/app/vocabulary-quiz-0.0.1-SNAPSHOT.jar"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "vocabulary-quiz-0.0.1-SNAPSHOT.jar"]