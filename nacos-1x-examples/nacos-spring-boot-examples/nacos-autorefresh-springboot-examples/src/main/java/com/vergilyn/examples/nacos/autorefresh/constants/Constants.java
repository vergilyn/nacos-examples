package com.vergilyn.examples.nacos.autorefresh.constants;

public class Constants {

	/**
	 * `nacos-spring-context:1.0.0`默认从data-id解析config-type
	 *
	 * @see com.alibaba.nacos.spring.context.annotation.NacosBeanDefinitionRegistrar
	 * @see com.alibaba.nacos.spring.context.annotation.EnableNacos
	 */
	public static final String DATA_ID = "spring-boot-autorefresh.properties";
	public static final String GROUP_ID = "SPRINGBOOT_AUTOREFRESH_GROUP";

	public static final String PREFIX_VERGILYN = "vergilyn.custom";
	public static final String CRLF = "\r\n";
}
