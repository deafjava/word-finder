FROM java:8-alpine
MAINTAINER Caio Cascaes <caio@cascaes.net>

ADD target/bras-cubas-0.0.1-SNAPSHOT-standalone.jar /bras-cubas/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/bras-cubas/app.jar"]
