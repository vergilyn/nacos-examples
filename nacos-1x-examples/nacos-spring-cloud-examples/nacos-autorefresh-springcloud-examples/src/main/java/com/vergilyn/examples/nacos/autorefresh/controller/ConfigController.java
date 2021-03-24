package com.vergilyn.examples.nacos.autorefresh.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.vergilyn.examples.nacos.autorefresh.config.NacosCustomProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("config")
public class ConfigController {

	// FIXME 失效 可能是由于GROUP不是`DEFAULT_GROUP`导致
	@NacosValue(value = "${vergilyn.custom.name:null}", autoRefreshed = true)
	private String name;

	@Autowired
	private NacosCustomProperties nacosProperties;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisProperties redisProperties;
	@Autowired
	private Environment environment;

	@RequestMapping(value = "/get", method = GET)
	@ResponseBody
	public String get() {
		LettuceConnectionFactory factory = (LettuceConnectionFactory)stringRedisTemplate.getConnectionFactory();

		int fdb = factory.getDatabase();
		stringRedisTemplate.opsForValue().increment("key", 1);

		int rdb = redisProperties.getDatabase();
		String edb = environment.getProperty("spring.redis.database");

		return String.format("fdb: %d, rdb: %d, edb: %s, name: %s, nacos-properties: %s",
				fdb, rdb, edb, name, JSON.toJSONString(nacosProperties));
	}
}