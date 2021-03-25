package com.vergilyn.examples.nacos.utils;

import com.alibaba.nacos.common.utils.MD5Utils;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *
 * @author vergilyn
 * @since 2021-03-24
 */
@SuppressWarnings("JavadocReference")
public class MD5UtilsTests {

	/**
	 * nacos 会保存config-content的md5hex，用于判断config-content是否更改。
	 * @see com.alibaba.nacos.client.config.impl.CacheData#setContent(String)
	 * @see com.alibaba.nacos.client.config.impl.CacheData#checkListenerMd5()
	 */
	@Test
	public void md5Hex(){
		String content = "vergilyn";

		String md5Hex = MD5Utils.md5Hex(content, UTF_8.toString());

		System.out.println(md5Hex);
	}
}
