package com.kuaipan.client.model;

import java.util.Map;


public class KuaipanPublicLink {
	public String url;
	public String access_code = null;
	
	public KuaipanPublicLink() {}
	
	public KuaipanPublicLink(Map<String, Object> map) {
		parseFromMap(map);
	}
	
	protected void parseFromMap(Map<String, Object> map) {
		this.url = (String) map.get("url");
		this.access_code = (String) map.get("access_code");
		
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nurl=");
		buf.append(url);
		
		buf.append("\naccess_code=");
		buf.append(access_code);
		return buf.toString();
	}
}
