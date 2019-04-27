FROM tomcat:7.0-alpine
VOLUME /tmp
COPY build/libs/fineract-provider.war /usr/local/tomcat/webapps/app.war
COPY server.xml /usr/local/tomcat/conf/server.xml
COPY drizzle-jdbc-1.3.jar /usr/local/tomcat/lib
COPY mysql-connector-java-5.1.42-bin.jar /usr/local/tomcat/lib
EXPOSE 8080
RUN sh -c 'touch /usr/local/tomcat/webapps/app.war'


