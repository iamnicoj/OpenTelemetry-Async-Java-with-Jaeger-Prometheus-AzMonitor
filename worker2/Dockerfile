# For Java 11, try this
FROM adoptopenjdk/openjdk11:alpine-jre

ENV APP_NAME worker2

# Copy the application .jar 
COPY ./target/${APP_NAME}-*.jar ./${APP_NAME}.jar
# Copy over telemetry agent .jar identify in the AGENTTYPE var and processed in the Makefile
COPY ./tmp/agent.jar agent.jar
# copy over applicationinsights.json as it may be used when using the Application Insights mode
COPY src/main/resources/applicationinsights.json applicationinsights.json

ENTRYPOINT ["java", "-javaagent:agent.jar", "-jar", "worker2.jar"]