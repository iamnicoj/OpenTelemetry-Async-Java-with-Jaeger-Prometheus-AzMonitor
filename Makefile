include .env
export 
ENV_FILE_PARAM = --env-file .env

.PHONY: all

all: clean package docker

clean:
	docker compose down
	mvn clean -f "./pom.xml"
	cd client && $(MAKE) clean && cd ..
	cd worker1 && $(MAKE) clean && cd ..
	cd worker2 && $(MAKE) clean && cd ..

package:
	mvn install -e -f "./pom.xml"

docker:
	cd client && $(MAKE) docker && cd ..
	cd worker1 && $(MAKE) docker && cd ..
	cd worker2 && $(MAKE) docker && cd ..
	cd infrastructure/rabbitmq && $(MAKE) docker && cd ..

