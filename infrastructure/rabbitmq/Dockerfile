FROM rabbitmq:3-management-alpine

# Define environment variables.
ENV RABBITMQ_USER adminuser
ENV RABBITMQ_PASSWORD passw0rd
ENV RABBITMQ_PID_FILE /var/lib/rabbitmq/mnesia/rabbitmq

ADD init.sh /init.sh
RUN chmod +x /init.sh
ADD rabbitmq.conf /etc/rabbitmq
ADD definitions.json /etc/rabbitmq
RUN chown rabbitmq:rabbitmq /etc/rabbitmq/rabbitmq.conf /etc/rabbitmq/definitions.json
EXPOSE 15672

# Define default command to start executing
CMD ["/init.sh"]