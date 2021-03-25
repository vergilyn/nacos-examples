package com.vergilyn.examples.nacos.configs;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.client.config.filter.impl.ConfigFilterChainManager;
import com.alibaba.nacos.client.config.http.HttpAgent;
import com.alibaba.nacos.client.config.impl.ClientWorker;
import com.vergilyn.examples.nacos.AbstractNacos1Tests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 *
 * @author vergilyn
 * @since 2021-03-24
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigListenerTests extends AbstractNacos1Tests {

	private final String _type = ConfigType.PROPERTIES.getType();
	private final String _dataId = "api-config-listener." + _type;

	@SneakyThrows
	@BeforeEach
	public void beforeEach(){
		_defaultConfigService.publishConfig(_dataId, DEFAULT_GROUP, "index=1");
	}

	@SneakyThrows
	@Test
	public void listener(){
		/**
		 * invoke listener:
		 *   -> {@link ClientWorker#ClientWorker(HttpAgent, ConfigFilterChainManager, Properties)}  }
		 *   -  {@link ClientWorker.LongPollingRunnable#run()}
		 *   -> {@link ClientWorker#getServerConfig(String, String, String, long)}
		 */
		_defaultConfigService.addListener(_dataId, DEFAULT_GROUP, new Listener() {
			@Override
			public Executor getExecutor() {
				return null;
			}

			@Override
			public void receiveConfigInfo(String configInfo) {
				println("nacos-listener receive: " + configInfo);
			}
		});

		new Thread(() -> {
			println("update-config sleep...");
			try {
				TimeUnit.SECONDS.sleep(10);
				_defaultConfigService.publishConfig(_dataId, DEFAULT_GROUP, "index=2");
			} catch (Exception e) {
			}

			println("update-config completed...");
		}).start();

		preventExit();
	}
}
