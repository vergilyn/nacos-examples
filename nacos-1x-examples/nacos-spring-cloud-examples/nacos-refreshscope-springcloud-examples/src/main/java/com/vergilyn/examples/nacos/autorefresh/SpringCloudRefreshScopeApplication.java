package com.vergilyn.examples.nacos.autorefresh;

import com.vergilyn.examples.nacos.autorefresh.config.NacosCustomProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Document: https://nacos.io/zh-cn/docs/quick-start-spring-boot.html
 * <p>
 * Nacos 控制台添加配置：
 * <pre>
 *   data-id: cloud-autorefresh.properties
 *   group: CLOUD_GROUP
 *   content:
 *      vergilyn.custom.name=vergilyn
 *      vergilyn.custom.database=0
 *      spring.redis.database=${vergilyn.custom.database}
 * </pre>
 */
@SpringBootApplication
public class SpringCloudRefreshScopeApplication {

	@Bean
	@RefreshScope
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(factory);
		stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
		stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
		return stringRedisTemplate;
	}

	@Bean
	@RefreshScope
	public NacosCustomProperties nacosCustomProperties(){
		return new NacosCustomProperties();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudRefreshScopeApplication.class, args);
	}
}
