package com.kuaipan.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Map;

public class JSONUtility {
	private JSONUtility() {
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(String str) {
		if (str == null) {
			return null;
		}

		try {
			return JSON.parseObject(str, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			return null;
		}
	}

}
