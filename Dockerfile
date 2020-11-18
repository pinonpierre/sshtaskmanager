FROM maven:3.6.3-openjdk-15 as builder-server
COPY server ./builder/
WORKDIR /builder/
RUN mvn clean package -U

FROM node:12-alpine as builder-ui
COPY ui /builder/
WORKDIR /builder/
RUN yarn install
RUN yarn tsc
RUN CI=true yarn test
RUN yarn build

FROM openjdk:15-slim
COPY --from=builder-server /builder/target/runui-*-shade.jar /app/runui.jar
COPY --from=builder-ui /builder/build /app/www
WORKDIR /app
RUN mkdir config
EXPOSE 8080
CMD java -jar /app/runui.jar
