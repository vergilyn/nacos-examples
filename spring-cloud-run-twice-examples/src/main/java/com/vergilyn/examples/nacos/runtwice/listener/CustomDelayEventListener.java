package com.vergilyn.examples.nacos.runtwice.listener;

import java.util.concurrent.atomic.AtomicInteger;

import com.vergilyn.examples.nacos.runtwice.event.CustomDelayEvent;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

public class CustomDelayEventListener implements ApplicationListener<CustomDelayEvent>, ApplicationContextAware, DefaultPrintStacktrace {
	private static final AtomicInteger INDEX = new AtomicInteger(0);

	private ApplicationContext applicationContext;

	@Override
	public void onApplicationEvent(CustomDelayEvent event) {

		System.out.printf("Delay-%d:\n", INDEX.incrementAndGet());
		System.out.printf("\taware-context >> %s\n", applicationContext);
		System.out.printf("\tsource-context >> %s\n", event.getSource());

		printStacktrace();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
