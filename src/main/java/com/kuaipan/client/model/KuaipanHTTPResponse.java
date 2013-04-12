package com.kuaipan.client.model;

public class KuaipanHTTPResponse {

	
	public final static int KUAIPAN_OK = 200;
	public final static int KUAIPAN_LOGICAL_ERROR = 202;
	
	public final static int KUAIPAN_BAD_REQUEST_ERROR = 400;
	public final static int KUAIPAN_AUTHORIZATION_ERROR = 401;
	public final static int KUAIPAN_FOBBIDEN_ERROR = 403;
	public final static int KUAIPAN_NOT_FOUND_ERROR = 404;
	public final static int KUAIPAN_TOO_MANY_FILES_ERROR = 406;
	public final static int KUAIPAN_TOO_LARGE_ERROR = 413;
	
	public final static int KUAIPAN_SERVER_ERROR = 500;	
	public final static int KUAIPAN_OVER_SPACE_ERROR = 507;
	public final static int KUAIPAN_UNKNOWNED_ERROR = 578;
	
	public final static String MSG_CONSUMER_EXPIRED = "bad consumer key";
	public final static String MSG_AUTHORIZATION_EXPIRED = "authorization expired";
	
	public int code=KUAIPAN_UNKNOWNED_ERROR;
	public String content;
	public KuaipanURL url;
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("code=");
		buf.append(Integer.toString(code));
		buf.append("\n");		
		if (content != null) {
			buf.append("body=");
			buf.append(content);
			buf.append("\n");
		}
		return buf.toString();
	}
}
