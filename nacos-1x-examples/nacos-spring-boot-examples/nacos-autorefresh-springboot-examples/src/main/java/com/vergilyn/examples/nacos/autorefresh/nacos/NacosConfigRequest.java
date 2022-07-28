package com.vergilyn.examples.nacos.autorefresh.nacos;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.context.event.config.NacosConfigReceivedEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

import static com.vergilyn.examples.nacos.autorefresh.constants.Constants.*;

@Component
public class NacosConfigRequest implements InitializingBean {


	@Value("${nacos.config.server-addr}")
	private String nacosConfigServerAddr;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	private ConfigService nacosConfigService;

	@Override
	public void afterPropertiesSet() throws Exception {
		nacosConfigService = ConfigFactory.createConfigService(nacosConfigServerAddr);

		init();
	}

	private boolean init() throws NacosException {
		String content = buildContent("init-name", "0", "${" + PREFIX_VERGILYN + ".database}");
		return publishAndRefresh(content);
	}

	public boolean changeContent(String name, String database, String redisDatabase) throws NacosException {
		return publishAndRefresh(buildContent(name, database, redisDatabase));
	}

	public String getContent() throws NacosException {
		return nacosConfigService.getConfig(DATA_ID, GROUP_ID, 2000);
	}

	private boolean publishAndRefresh(String content) throws NacosException {
		boolean isPublish = nacosConfigService.publishConfig(DATA_ID, GROUP_ID, content);

		if (isPublish){
			applicationEventPublisher.publishEvent(
					new NacosConfigReceivedEvent(nacosConfigService, DATA_ID, GROUP_ID, content, "properties"));
		}

		return isPublish;
	}

	public static String buildContent(String name, String database, String redisDatabase){
		StringJoiner joiner = new StringJoiner(CRLF);

		joiner.add(PREFIX_VERGILYN + ".name=" + name);
		joiner.add(PREFIX_VERGILYN + ".database=" + database);
		joiner.add("spring.redis.database=" + redisDatabase);

		return joiner.toString();
	}
}
