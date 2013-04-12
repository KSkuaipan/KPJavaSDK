package com.kuaipan.client.session;


public class OauthSession extends Session {
	public OauthSession(String key, String secret) {
		super(key, secret, Root.APP_FOLDER);
	}
	
	public OauthSession(String key, String secret, Root root) {
		super(key, secret, root);
	}
}
