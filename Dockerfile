FROM maven:3.6.3-openjdk-15 as builder-server
COPY server ./builder/
WORKDIR /builder/
RUN mvn clean package -U

FROM openjdk:15-slim
COPY --from=builder-server /builder/target/runui-*-shade.jar /app/runui.jar
WORKDIR /app
RUN mkdir config
EXPOSE 8080
CMD java -jar /app/runui.jar
