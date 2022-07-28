package com.vergilyn.examples.nacos.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.param.Query;
import com.vergilyn.examples.nacos.AbstractNacos2Tests;
import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullStringAsEmpty;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author vergilyn
 * @since 2021-03-24
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigServiceTests extends AbstractNacos2Tests {

	@BeforeAll
	public void beforeAll() throws NacosException {
	}

	@SneakyThrows
	@ParameterizedTest
	@ValueSource(strings = {DEFAULT_NAMESPACE_ID})
	public void publishConfig(String namespaceId){
		String type = ConfigType.PROPERTIES.getType();
		String dataId = "api-test." + type;
		String group = DEFAULT_GROUP;

		String content = "vergilyn.nacos2.first=hello\n"
						+ "vergilyn.nacos2.second=world";

		ConfigService configService = createConfigService(namespaceId);

		boolean bool = configService.publishConfig(dataId, group, content, type);
		assertThat(bool).isTrue();

		String config = configService.getConfig(dataId, group, 6_000);
		println(StringEscapeUtils.escapeJava(config));
		assertThat(config).isEqualTo(content);

		String configInfo = getConfigInfo(dataId, group, namespaceId);
		println("config-info: " + configInfo);
		JSONObject jsonObject = JSON.parseObject(configInfo);

		assertThat(jsonObject.getString("type")).isEqualTo(type);
	}

	@Test
	public void specifyNamespace(){
		String namespace = "api-test";
		boolean isCreate = createNamespace(namespace, "", namespace);
		assertThat(isCreate).isTrue();

		publishConfig(namespace);

	}

	@SneakyThrows
	@Test
	public void getConfigObject(){
		String dataId = "api-test.properties";
		String group = DEFAULT_GROUP;
		String namespaceId = "api-test";

		String configInfo = getConfigInfo(dataId, group, namespaceId);
		println(configInfo);
		println(JSON.parseObject(configInfo).toString(WriteNullStringAsEmpty, PrettyFormat));
	}

	@SneakyThrows
	protected String getConfigInfo(String dataId, String group, String namespaceId){
		String url = NACOS_API_HOST + "/nacos/v1/cs/configs";
		Query query = Query.newInstance()
				.addParam("dataId", dataId)
				.addParam("group", group)
				.addParam("namespaceId", namespaceId)
				.addParam("tenant", namespaceId)
				.addParam("show", "all");

		HttpRestResult<String> result = _nacosRestTemplate.get(url, null, query, String.class);

		return result.getData();
	}
}
