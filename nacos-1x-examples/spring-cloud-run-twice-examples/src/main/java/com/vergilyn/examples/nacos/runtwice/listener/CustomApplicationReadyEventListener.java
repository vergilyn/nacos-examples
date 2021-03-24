package com.vergilyn.examples.nacos.runtwice.listener;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 跟踪为什么会被调用 4次？
 */
public class CustomApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent>, DefaultPrintStacktrace{
	private static final AtomicInteger INDEX = new AtomicInteger(0);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {

		System.out.printf("Ready-%d: application[%s]\n"
				, INDEX.incrementAndGet(), event.getSpringApplication());

		printStacktrace();
	}
}
