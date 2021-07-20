$(info Using OS    : $(OS))
$(info Using shell : $(SHELL))

APP_NAME := oteljava/client:v2
DOCKER := docker
OTEL_AGENT := otel

# Yeah, it's hacky to handle cross-platform using if/else. :D
ifeq ($(OS), Windows_NT)
	MVN := .\mvnw.cmd
else
	MVN := mvn
endif

.PHONY: all 

all: clean  docker

clean:
	$(info =====[ CLEANING MAVEN PACKAGE: $(APP_NAME) ]=====)
ifeq ($(OS), Windows_NT)
	if exist .\tmp rmdir /Q /S .\tmp
else
	rm -rf ./tmp
endif

docker:
	$(info =====[ BUILDING DOCKER PACKAGE: $(APP_NAME) ]=====)
ifeq ($(OS), Windows_NT)
	if not exist .\tmp mkdir .\tmp
ifeq ($(AGENTTYPE), $(OTEL_AGENT))
	$(info =====[ ADDING OTEL AGENT WIN]=====)
	copy ..\agents\opentelemetry-javaagent-all.jar .\tmp\agent.jar
else
	$(info =====[ ADDING APP INSIGHTS AGENT WIN]=====)
	copy ..\agents\applicationinsights-agent-3.1.1.jar .\tmp\agent.jar
endif
else
	mkdir -p ./tmp
ifeq ($(AGENTTYPE), $(OTEL_AGENT))
	$(info =====[ ADDING OTEL AGENT LNX]=====)
	cp ../agents/opentelemetry-javaagent-all.jar ./tmp/agent.jar
else
	$(info =====[ ADDING APP INSIGHTS AGENT LNXWIN]=====)
	cp ../agents/applicationinsights-agent-3.1.1.jar ./tmp/agent.jar
endif
endif
	$(DOCKER) build -t $(APP_NAME) . 
