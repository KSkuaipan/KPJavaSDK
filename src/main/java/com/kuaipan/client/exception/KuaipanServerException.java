package com.kuaipan.client.exception;

import com.kuaipan.client.model.KuaipanHTTPResponse;
import com.kuaipan.client.model.KuaipanURL;

public class KuaipanServerException extends KuaipanException {

	private static final long serialVersionUID = 1L;
	
	public int code;
	public String raw;
	public KuaipanURL url;
	public String msg = "UNKNOWNED ERROR";
	
	public KuaipanServerException(KuaipanHTTPResponse response) {
		this.code = response.code;
		this.raw = response.content;
		this.url = response.url;
		this.msg = retrieveMsg(response.content);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("\ncode=");
		buf.append(this.code);
		
		buf.append("\nmsg=");
		buf.append(this.msg);
		
		buf.append("\nraw=");
		buf.append(this.raw);
		
		buf.append("\nurl=");
		buf.append(this.url);
		return buf.toString();
	}
		
	private static String retrieveMsg(String content) {
		final String start = "msg\": \"";
		final String end = "\"";
		int idx_start = content.indexOf(start);
		if (idx_start < 0)
			return null;
		
		idx_start += start.length();
		
		int idx_end = content.indexOf(end, idx_start);
		if (idx_end < 0)
			return null;
		return content.substring(idx_start, idx_end);
	}
	
	
	public static void main(String[] args) {
		System.out.println(retrieveMsg("\"{\"msg\": \"hello\"}"));
	}
}
