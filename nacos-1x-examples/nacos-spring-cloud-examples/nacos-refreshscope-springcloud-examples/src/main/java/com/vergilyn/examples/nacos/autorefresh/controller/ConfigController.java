package com.vergilyn.examples.nacos.autorefresh.controller;

import com.alibaba.fastjson.JSON;
import com.vergilyn.examples.nacos.autorefresh.config.NacosCustomProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("config")
@RefreshScope
public class ConfigController {

	@Autowired
	private NacosCustomProperties nacosProperties;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisProperties redisProperties;
	@Autowired
	private Environment environment;

	@RequestMapping(value = "/get", method = GET)
	public String get() {
		LettuceConnectionFactory factory = (LettuceConnectionFactory)stringRedisTemplate.getConnectionFactory();

		int fdb = factory.getDatabase();
		stringRedisTemplate.opsForValue().increment("key", 1);

		int rdb = redisProperties.getDatabase();
		String edb = environment.getProperty("spring.redis.database");

		return String.format("fdb: %d, rdb: %d, edb: %s, nacos-properties: %s",
				fdb, rdb, edb, JSON.toJSONString(nacosProperties));
	}
}