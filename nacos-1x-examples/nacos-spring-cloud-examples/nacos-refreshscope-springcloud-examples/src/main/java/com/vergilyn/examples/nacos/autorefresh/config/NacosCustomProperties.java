package com.vergilyn.examples.nacos.autorefresh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "vergilyn.custom")
public class NacosCustomProperties {

	private String name;

	private String database;
}
