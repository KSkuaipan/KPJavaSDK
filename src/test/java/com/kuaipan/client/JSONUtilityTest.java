package com.kuaipan.client;

import org.junit.Test;

/**
 * Author: wjw
 * Date: 13-4-12 下午4:52
 */
public class JSONUtilityTest {
	@Test
	public void testJSONParse() throws Exception {
		System.out.println(JSONUtility.parse("{\"name\": \"\\u65b0\\u5efa\\u6587\\u4ef6\"}"));
		System.out.println(JSONUtility.parse("{'a': '23'}"));
	}
}
