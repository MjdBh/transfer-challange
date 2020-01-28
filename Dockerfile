FROM maven:3.6-jdk-11-openj9

WORKDIR /app

COPY . .

RUN  mvn clean verify ;

EXPOSE 4000

CMD ["sh", "-c", "java  -jar /app/target/transfer-jar-with-dependencies.jar"]
