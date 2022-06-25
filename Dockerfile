FROM hseeberger/scala-sbt:8u312_1.6.2_2.13.8 as build
WORKDIR /app
COPY . /app
RUN sbt assembly

FROM openjdk:11
WORKDIR /app
COPY --from=build /app/target/scala-2.13/search-service-assembly-0.0.1.jar ./search-service.jar
COPY --from=build /app/src/main/resources/esMapping.json ./esMapping.json
CMD java -jar search-service.jar
