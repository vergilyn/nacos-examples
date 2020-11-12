package com.vergilyn.examples.nacos.autorefresh.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;

import lombok.Data;

import static com.vergilyn.examples.nacos.autorefresh.SpringbootAutoRefreshApplication.DATA_ID;
import static com.vergilyn.examples.nacos.autorefresh.SpringbootAutoRefreshApplication.GROUP_ID;

@NacosConfigurationProperties(/*prefix = "spring.redis",*/dataId = DATA_ID, groupId = GROUP_ID, autoRefreshed = true)
@Data
public class NacosCustomProperties {

	private String name;

	/* 如果依赖nacos-spring-boot-autoconfigure，会导致`@NacosProperty`失效，因为builder被autoconfigure重新实现。
	 * 所以，造成无法通过`@NacosProperty`指定属性别名。
	 * 解决方法：用prefix，同时保证同一个properties中都是相同prefix的属性。
	 *
	 * @see com.alibaba.boot.nacos.config.autoconfigure.NacosConfigBootBeanDefinitionRegistrar
	 * @see com.alibaba.boot.nacos.config.binder.NacosBootConfigurationPropertiesBinder
	 * @see com.alibaba.nacos.spring.context.properties.config.NacosConfigurationPropertiesBinder
	 */
//	@NacosProperty("spring.redis.database")
	/* 实质是SpEL填充属性，而不是nacos-properties-builder */
//	@Value("${spring.redis.database}")
	private String database;
}
