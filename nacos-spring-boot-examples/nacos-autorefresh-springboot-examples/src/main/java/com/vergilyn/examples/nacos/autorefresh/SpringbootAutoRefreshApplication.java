package com.vergilyn.examples.nacos.autorefresh;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.vergilyn.examples.nacos.autorefresh.config.NacosCustomProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.vergilyn.examples.nacos.autorefresh.SpringbootAutoRefreshApplication.DATA_ID;
import static com.vergilyn.examples.nacos.autorefresh.SpringbootAutoRefreshApplication.GROUP_ID;

/**
 * Document: https://nacos.io/zh-cn/docs/quick-start-spring-boot.html
 * <p>
 * Nacos 控制台添加配置：
 * <pre>
 *   data-id: boot-autorefresh.properties
 *   group: BOOT_GROUP
 *   content:
 *      name=vergilyn
 *      spring.redis.database=6
 * </pre>
 */
@SpringBootApplication
@NacosPropertySource(dataId = DATA_ID, groupId = GROUP_ID, autoRefreshed = true)
public class SpringbootAutoRefreshApplication {
	/**
	 * `nacos-spring-context:1.0.0`默认从data-id解析config-type
	 *
	 * @see com.alibaba.nacos.spring.context.annotation.NacosBeanDefinitionRegistrar
	 * @see com.alibaba.nacos.spring.context.annotation.EnableNacos
	 */
	public static final String DATA_ID = "boot-autorefresh.properties";
	public static final String GROUP_ID = "BOOT_GROUP";

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(factory);
		stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
		stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
		return stringRedisTemplate;
	}

	@Bean
	public NacosCustomProperties nacosCustomProperties() {
		return new NacosCustomProperties();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAutoRefreshApplication.class, args);
	}
}
