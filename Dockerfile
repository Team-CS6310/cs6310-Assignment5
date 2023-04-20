# Compile our java files in this container
FROM openjdk:17-alpine AS builder
COPY src /usr/src/cs6310/src
WORKDIR /usr/src/cs6310
RUN find . -name "*.java" | xargs javac -d ./target
RUN jar cfe drone_delivery.jar Main -C ./target/ .

# Install MySQL client and server and Java
FROM alpine:3.15
RUN apk add --no-cache mysql mysql-client openjdk17
RUN mkdir /var/run/mysqld && chown mysql:mysql /var/run/mysqld
RUN mysql_install_db --user=mysql --datadir=/var/lib/mysql
COPY my.cnf /etc/mysql/my.cnf
COPY init.sql /docker-entrypoint-initdb.d/
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Download and copy the JDBC driver JAR file
RUN apk add --no-cache wget && \
    wget -O /usr/lib/mysql-connector-java.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar && \
    chmod 644 /usr/lib/mysql-connector-java.jar && \
    apk del wget

ENTRYPOINT ["docker-entrypoint.sh"]
EXPOSE 3306

# Copy the jar and test scenarios into our final image
WORKDIR /usr/src/cs6310
COPY test_scenarios ./
COPY test_results ./
COPY --from=builder /usr/src/cs6310/drone_delivery.jar ./drone_delivery.jar
CMD ["java", "-cp", "/usr/lib/mysql-connector-java.jar:./drone_delivery.jar", "Main", "commands_00.txt"]
