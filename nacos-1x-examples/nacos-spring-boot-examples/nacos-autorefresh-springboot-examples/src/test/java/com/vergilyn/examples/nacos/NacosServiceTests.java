package com.vergilyn.examples.nacos;

import java.util.Properties;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;

import org.junit.jupiter.api.Test;

import static com.alibaba.nacos.api.PropertyKeyConst.NAMESPACE;
import static com.alibaba.nacos.api.PropertyKeyConst.SERVER_ADDR;

public class NacosServiceTests {


	@Test
	public void test() throws NacosException {
		Properties properties = new Properties();
		properties.put(SERVER_ADDR, "127.0.0.1:8848");
//		properties.put(NAMESPACE, "hmily-test");
		properties.put(NAMESPACE, "d9f52e47-c512-4cf3-9b5e-0f42545e43c1");

		NacosConfigService configService = new NacosConfigService(properties);

		String config = configService.getConfig("hmily-namespace.properties", "DEFAULT_GROUP", 60_000);

		System.out.println(config);
	}
}
