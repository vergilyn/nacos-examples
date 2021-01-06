package com.vergilyn.examples.nacos.runtwice;

import java.util.concurrent.TimeUnit;

import com.vergilyn.examples.nacos.runtwice.event.CustomDelayEvent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringCloudRunTwiceApplication {

	/*@Bean
	public CustomDelayEventListener customDelayEventSmartListener(){
		return new CustomDelayEventListener();
	}*/

	public static void main(String[] args) throws InterruptedException {
		SpringApplication application = new SpringApplication(SpringCloudRunTwiceApplication.class);

		ConfigurableApplicationContext context = application.run(args);

		TimeUnit.SECONDS.sleep(5);
		context.publishEvent(new CustomDelayEvent(context));
	}
}
