#build frontend
FROM node:lts-alpine as build-frontend
WORKDIR /app
COPY frontend/ .
RUN npm install
RUN npm run build
RUN find .

#build backend
FROM maven:3-amazoncorretto-17 as build-backend
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src src
COPY --from=build-frontend /app/dist src/main/resources/static
RUN find .
RUN mvn install

#run merged
FROM amazoncorretto:17-alpine as run
WORKDIR /app
COPY --from=build-backend /app/target/trivia-game-stats-0.0.1-SNAPSHOT.jar app.jar
ENV XDG_DATA_HOME=/app/data
ENV AVATAR_URL_PATTERN=https://localhost/%s
ENTRYPOINT java -Davatar.url.pattern=$AVATAR_URL_PATTERN -jar /app/app.jar
EXPOSE 8080
