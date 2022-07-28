package com.vergilyn.examples.nacos.autorefresh.controller;

import com.alibaba.nacos.client.config.NacosConfigService;
import com.vergilyn.examples.nacos.AbstractSpringbootAutoRefreshApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * nacos-config的刷新机制：
 * <pre>
 *   org.springframework.context.ApplicationEventPublisher.publishEvent(
 * 				new NacosConfigReceivedEvent(configService, dataId, groupId, content, type));
 * </pre>
 *
 * @see com.alibaba.boot.nacos.config.autoconfigure.NacosConfigEnvironmentProcessor
 */
class ConfigControllerTests extends AbstractSpringbootAutoRefreshApplication {

	@Autowired
	private TestRestTemplate restTemplate;

	/**
	 * {@linkplain NacosConfigService#getConfig(String, String, long)} 内部实现也是"优先使用本地配置"
	 *
	 * @see NacosConfigService#getConfig(String, String, long)
	 */
	@SneakyThrows
	@RepeatedTest(4)
	void get(RepetitionInfo repetitionInfo) {
		int currentRepetition = repetitionInfo.getCurrentRepetition();
		String logPrefix = "[vergilyn][" + currentRepetition + "] ";

		String result = restTemplate.getForObject("/config/get", String.class);

		System.out.println(logPrefix + "bean-info >>>> \n" + result);

		// 2021-12-10, 内部也会"优先使用本地配置"
		// System.out.println(logPrefix + "nacos-server info >>>> \n" + nacosConfigService.getConfig(...));

		System.out.println();
		TimeUnit.SECONDS.sleep(10);
	}
}