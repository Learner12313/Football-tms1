FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY lib/ lib/
COPY src/ src/
COPY public/ public/

RUN mkdir -p classes && javac -cp "lib/*" -d classes \
    src/config/ApiConfig.java \
    src/database/DatabaseManager.java \
    src/servlet/*.java \
    src/MainServer.java

EXPOSE 3000

CMD ["java", "-cp", "classes:lib/*", "MainServer"]
