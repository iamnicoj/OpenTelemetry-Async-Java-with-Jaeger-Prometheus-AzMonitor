$(info Using OS    : $(OS))
$(info Using shell : $(SHELL))

APP_NAME := oteljava/rabbitmq:3-management-alpine
DOCKER := docker

# Yeah, it's hacky to handle cross-platform using if/else. :D
ifeq ($(OS), Windows_NT)
	MVN := .\mvnw.cmd
else
	MVN := mvn
endif

.PHONY: all 

all: docker

docker:
	$(info =====[ BUILDING DOCKER PACKAGE: $(APP_NAME) ]=====)
ifeq ($(OS), Windows_NT)
# if not exist .\tmp mkdir .\tmp
# copy ..\agents\applicationinsights-agent-3.1.1.jar .\tmp\agent.jar
else
# mkdir -p ./tmp
# cp ../agents/applicationinsights-agent-3.1.1.jar ./tmp/agent.jar
endif
	$(DOCKER) build -t $(APP_NAME) . 
