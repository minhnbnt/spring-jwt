FROM gradle:jdk17-ubi-minimal AS builder

COPY build.gradle.kts .
RUN gradle dependencies --no-daemon

COPY . .
RUN gradle bootJar

FROM eclipse-temurin:17-jre-ubi9-minimal

COPY --from=builder /home/gradle/build/libs/ .

CMD java -jar *.jar
