package com.kuaipan.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.kuaipan.client.model.Consumer;
import com.kuaipan.client.model.KuaipanURL;
import com.kuaipan.client.model.TokenPair;

public class OauthUtility {	
	
	private final static String NONCE_SAMPLE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final static String ENCODE = "UTF-8";
	private final static String HASH_METHOD = "HmacSHA1";
	
	private final static char JOIN_AND = '&';
	private final static char JOIN_EQUAL = '=';
	
	
	
	private OauthUtility() {}
	
	private static String buildURI(String host, String location, boolean isSecure) {
		StringBuffer uri = new StringBuffer(); 
		if (isSecure)
			uri.append("https://");
		else
			uri.append("http://");
		
		uri.append(host);
		uri.append(location);
		return uri.toString();
	}
	
	public static KuaipanURL buildPostURL(String host, String location, 
			Map<String, String> params,
			Consumer consumer, TokenPair token,
			boolean isSecure) {
		KuaipanURL kpurl= buildURL("POST", host, location, params, consumer, token, isSecure); 
		return kpurl;
	}
	
	public static KuaipanURL buildGetURL(String host, String location, 
			Map<String, String> params,
			Consumer consumer, TokenPair token,
			boolean isSecure) {
		KuaipanURL kpurl= buildURL("GET", host, location, params, consumer, token, isSecure); 
		kpurl.convert2Get();
		return kpurl;
	}
	
	private static KuaipanURL buildURL(String method, String host, String location, 
			Map<String, String> params,
			Consumer consumer, TokenPair token,
			boolean isSecure) {
		
		TreeMap<String, String> signed_params;
		location = urlEncode(location);
		if (params != null)
			signed_params = new TreeMap<String, String>(params);
		else
			signed_params = new TreeMap<String, String>();
		
		signed_params.put("oauth_nonce", generateNonce());
		signed_params.put("oauth_timestamp", Long.toString((System.currentTimeMillis()/1000)));
		signed_params.put("oauth_version", "1.0");
		signed_params.put("oauth_signature_method", "HMAC-SHA1");
		
		String signature;
		StringBuffer requestUrl = new StringBuffer(); 
		
		try {
			signature = generateSignature(method, host, location, signed_params, consumer, token, isSecure);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (InvalidKeyException e) {
			return null;
		}
		
		signed_params.put("oauth_signature", signature);
		signed_params.put("oauth_consumer_key", consumer.key);
		if (token != null)
			signed_params.put("oauth_token", token.key);
		
		requestUrl.append(buildURI(host, location, isSecure));
		
		return new KuaipanURL(requestUrl.toString(), encodeParameters(signed_params));
	}
	
	/**
	 * prepare parameters for HTTP request.
	 * @param signed_params
	 * @return
	 */
	private static String encodeParameters(Map<String, String> signed_params) {
		StringBuffer buf = new StringBuffer();
		for (Iterator<String> iter = signed_params.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			buf.append(key);
			buf.append(JOIN_EQUAL);
			try {
				buf.append(URLEncoder.encode(signed_params.get(key), ENCODE));
			} catch (UnsupportedEncodingException e) {
				// never come here
			}
			if (iter.hasNext())
				buf.append(JOIN_AND);			
		}
		return buf.toString();
		
	}
	
	private static String generateNonce() {
		return generateNonce(10);
	}
	
	private static String generateNonce(int length) {
		Random random = new Random(System.currentTimeMillis());
		if (length < 10)
			length = 10;
		
		int MAX_LEN = NONCE_SAMPLE.length();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buf.append(NONCE_SAMPLE.charAt(random.nextInt(MAX_LEN)));
		}
		return buf.toString();
	}
	
	private static String generateSignature(String method, String host, String location, 
			TreeMap<String, String> params,
			Consumer consumer, TokenPair token,
			boolean isSecure) throws NoSuchAlgorithmException, InvalidKeyException{
		
		String msg = generateBaseString(method, host, location, params, consumer, token, isSecure);
		String key = generateSecret(consumer, token);
		
		Mac mac = Mac.getInstance(HASH_METHOD);
		SecretKeySpec skey = new SecretKeySpec(key.getBytes(), HASH_METHOD);
		
		mac.init(skey);
		byte[] data = mac.doFinal(msg.toString().getBytes());
		return Base64Utility.encode(data);
	}
	
	private static String generateSecret(Consumer consumer, TokenPair token) {
		StringBuffer buf = new StringBuffer();
		buf.append(consumer.secret);
		buf.append(JOIN_AND);
		if (token != null)
			buf.append(token.secret);
		
		return buf.toString();
	}
	
	/**
	 * 
	 * @param method GET or POST.
	 * @param host like 'openapi.kuaipan.cn', without scheme.
	 * @param location starts with '/', no non-ASCII characters.
	 * @param params without tokens.
	 * @param consumer
	 * @param token
	 * @param isSecure whether uses HTTPS or not.
	 * @return basestring defined in RFC 5849 3.4.1. Signature Base String
	 */
	private static String generateBaseString(String method, String host, String location, 
			TreeMap<String, String> params,
			Consumer consumer, TokenPair token,
			boolean isSecure) {
		
		StringBuffer buf = new StringBuffer();
		buf.append(method);
		buf.append(JOIN_AND);
		if (isSecure)
			buf.append(oauthEncode("https://" + host + location));
		else
			buf.append(oauthEncode("http://" + host + location));
		
		buf.append(JOIN_AND);
		buf.append(oauthEncode(normalizeParameters(params, consumer, token)));
		
		return buf.toString();
	}
	
	/**
	 * 
	 * @param params
	 * @param consumer
	 * @param token
	 * @return the third part of basestring
	 */
	private static String normalizeParameters(TreeMap<String, String> params,
			Consumer consumer, TokenPair token) {
		StringBuffer buf = new StringBuffer();
		TreeMap<String, String> tm = new TreeMap<String, String>(params);
		
		tm.put("oauth_consumer_key", consumer.key);	
		if (token != null) 
			tm.put("oauth_token", token.key);

		
		for (Iterator<String> iter = tm.keySet().iterator(); iter.hasNext(); ) {
			String k = iter.next();			
			buf.append(k);
			buf.append(JOIN_EQUAL);
			buf.append(oauthEncode(tm.get(k)));
			if (iter.hasNext())
				buf.append(JOIN_AND);
		}
		return buf.toString();
	}
	
	/**
	 * unreserved characters (ALPHA / DIGIT / "-" / "." / "_" / "~") MUST not be encoded, 
	 * others MUST be encoded.
	 * @param str
	 * @return percent encoding defined in RFC 5849 3.6. Percent Encoding
	 */
	private static String oauthEncode(String str) {
		try {
			return URLEncoder.encode(str, ENCODE)
					.replace("*", "%2A")					
					.replace("%7E", "~")
					.replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private static String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, ENCODE).replace("%2F", "/");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
