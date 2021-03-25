package com.vergilyn.examples.nacos;

import java.time.LocalTime;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.config.impl.ConfigHttpClientManager;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.Query;
import com.google.common.collect.Maps;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author vergilyn
 * @since 2021-03-24
 *
 * @see PropertyKeyConst
 * @see Constants
 * @see com.alibaba.nacos.common.utils.JacksonUtils
 */
public abstract class AbstractNacos1Tests {

	protected static final String SERVER_ADDR = "127.0.0.1:18848";
	protected static final String DEFAULT_NAMESPACE_ID = "";

	protected static final String NACOS_API_HOST = "http://" + SERVER_ADDR;
	protected static final String DEFAULT_GROUP = Constants.DEFAULT_GROUP;
	protected final ConfigService _defaultConfigService;
	protected final NamingService _defaultNamingService;
	protected final NacosRestTemplate _nacosRestTemplate;

	@SneakyThrows
	public AbstractNacos1Tests() {
		this._defaultConfigService = createConfigService();
		this._defaultNamingService = createNamingService();
		_nacosRestTemplate = ConfigHttpClientManager.getInstance().getNacosRestTemplate();;
	}

	protected ConfigService createConfigService() throws NacosException {
		return createConfigService(DEFAULT_NAMESPACE_ID);
	}

	protected ConfigService createConfigService(@NotNull String namespaceId) throws NacosException {
		Properties properties = createProperties(namespaceId);
		return NacosFactory.createConfigService(properties);
	}

	protected NamingService createNamingService() throws NacosException {
		return createNamingService(DEFAULT_NAMESPACE_ID);
	}

	protected NamingService createNamingService(@NotNull String namespaceId) throws NacosException {
		Properties properties = createProperties(namespaceId);
		return NacosFactory.createNamingService(properties);
	}

	protected Properties createProperties(@NotNull String namespaceId){
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, SERVER_ADDR);
		// value NotNull!
		properties.put(PropertyKeyConst.NAMESPACE, namespaceId);
		return properties;
	}

	/**
	 * nacos 未提供 SDK-API
	 * @param namespace 命名空间ID
	 * @param namespaceId 命名空间名
	 * @param namespaceDesc 命名空间描述
	 *
	 * @see <a href="https://nacos.io/zh-cn/docs/open-api.html#3.2">创建命名空间</a>
	 */
	@SneakyThrows
	protected boolean createNamespace(@NotNull String namespace, @Nullable String namespaceId, @NotNull String namespaceDesc){
		String url = NACOS_API_HOST + "/nacos/v1/console/namespaces";
		String customNamespaceId = StringUtils.isNotBlank(namespaceId) ? namespaceId.trim() : namespace;

		Map<String, String> bodyValues = Maps.newHashMap();
		bodyValues.put("customNamespaceId", customNamespaceId);
		bodyValues.put("namespaceName", namespace);
		bodyValues.put("namespaceDesc", namespaceDesc);

		HttpRestResult<String> response = _nacosRestTemplate.postForm(url, Header.EMPTY, Query.EMPTY, bodyValues, String.class);
		println("nacos create-namespace response: " + response.getData());

		return response.ok();
	}

	protected void println(String c){
		System.out.printf("[vergilyn][%s] >>>> %s \n", LocalTime.now(), c);
	}

	@SneakyThrows
	protected void preventExit(){
		new Semaphore(0).acquire();
	}
}
