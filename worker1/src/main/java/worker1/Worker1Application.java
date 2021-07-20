package worker1;

import entity.OrderMessage;
import entity.OrderMessageRepository;
import otelpoc.OtelConfiguration;
import otelpoc.OtelOrderSpan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@ComponentScan(basePackageClasses = {OrderMessage.class, OrderMessageRepository.class,
	RedisConfig.class, RabbitMqReceiver.class, RabbitMqConfig.class,
	 OtelOrderSpan.class, OtelConfiguration.class})
	 
@EnableRedisRepositories(basePackageClasses = OrderMessageRepository.class)
@SpringBootApplication
public class Worker1Application {
	private static final Logger logger = LoggerFactory.getLogger(Worker1Application.class);
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext applicationContext = SpringApplication.run(Worker1Application.class, args);
		for (String name: applicationContext.getBeanDefinitionNames()) {
			logger.debug(name);
		}
	}

	public Worker1Application() {
		logger.info(System.getenv("AGENTTYPE"));

		if (System.getenv("AGENTTYPE").toLowerCase().equals("otel")){
			logger.info("Otel agent is enable, configuring the SDK...");
			OtelConfiguration.initOpenTelemetry();
		}
		else
			logger.info("App Insights agent is enable");
	}
}
