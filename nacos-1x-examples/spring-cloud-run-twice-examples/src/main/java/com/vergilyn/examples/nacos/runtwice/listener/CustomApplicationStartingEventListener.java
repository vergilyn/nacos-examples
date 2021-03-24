package com.vergilyn.examples.nacos.runtwice.listener;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

public class CustomApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent>, DefaultPrintStacktrace {
	private static final AtomicInteger INDEX = new AtomicInteger(0);

	@Override
	public void onApplicationEvent(ApplicationStartingEvent event) {

		System.out.printf("Starting-%d: application[%s]\n"
				, INDEX.incrementAndGet(), event.getSpringApplication());

		printStacktrace();
	}
}
