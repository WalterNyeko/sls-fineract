FROM tomcat:7.0.91-jre8
VOLUME /tmp
COPY build/libs/fineract-provider.war /usr/local/tomcat/webapps
COPY server.xml /usr/local/tomcat/conf/server.xml
COPY tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY drizzle-jdbc-1.3.jar /usr/local/tomcat/lib
COPY mysql-connector-java-5.1.42-bin.jar /usr/local/tomcat/lib
EXPOSE 8080



