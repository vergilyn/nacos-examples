package com.vergilyn.examples.nacos.runtwice.event;

import org.springframework.context.ApplicationEvent;

public class CustomDelayEvent extends ApplicationEvent {

	public CustomDelayEvent(Object source) {
		super(source);
	}
}
