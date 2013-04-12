package com.kuaipan.client.model;


public abstract class TokenPair {
	public final String key;
	public final String secret;
	
	public TokenPair(String key, String secret) {
		this.key = key;
		this.secret = secret;
	}
	
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("key=");
		buf.append(key);
		buf.append(";");
		buf.append("secret=");
		buf.append(secret);
		return buf.toString();
	}
}
