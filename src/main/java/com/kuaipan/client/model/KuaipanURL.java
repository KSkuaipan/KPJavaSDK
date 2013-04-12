package com.kuaipan.client.model;

import java.util.Map;

public class KuaipanURL {
	public String url;
	public String query;
	public Map<String, String> headers;	
	
	public KuaipanURL(String url) {
		this(url, null);
	}
	
	public KuaipanURL(String url, String query) {
		this(url, query, null);
	}
	
	public KuaipanURL(String url, String query, Map<String, String> headers) {
		this.url = url;
		this.query = query;
		this.headers = headers;
	}
	
	public void convert2Get() {
		url = queryByGetUrl();
		query = null;
	}
	
	public String queryByGetUrl() {
		if (query != null)
			return  url + "?" + query;
		return url;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nurl=");
		buf.append(url);
		buf.append("\nquery=");
		if (query != null)
			buf.append(query);
		buf.append("\nheaders=");
		if (headers != null)
			buf.append(headers.toString());
		return buf.toString();
	}
}
