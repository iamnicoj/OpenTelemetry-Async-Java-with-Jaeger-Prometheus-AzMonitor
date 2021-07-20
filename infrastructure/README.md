## Dev env instructions 
* run `docker-compose up --build`
* RabbitMQ should be up and running. To verify, open http://localhost:15672/
* Login with the credentials found in the definitions.json file
* Open the queues tab and verify that both #orders# and #fulfillment# queues were provision

The definitions.json will also contain the information of the queues that are provision during deployment.