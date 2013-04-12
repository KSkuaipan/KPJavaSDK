package com.kuaipan.test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.hook.SleepyProgressListener;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class KPTestUtility {
	private KPTestUtility() {}
	
	public final static String CONSUMER_KEY = "xcNOxsWyKuCuZskF";
	public final static String CONSUMER_SECRET = "GllItIcJNsmkj4nk";
	
	public final static String USERNAME = "";
	public final static String PASSWROD = "";
	
	public final static String AUTH_FILE_PATH = "myauth.json";
	
	
	private static final String RANDOM_SAMPLE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	/**
	 * generate some random bytes stream.
	 * @param size 
	 * 			do not be too large or it may eat up all your memory.
	 * @return
	 */
	public static String generateByteString(int size) {
		StringBuffer buf = new StringBuffer();
		Random random = new Random();
		
		int MAX_LEN = RANDOM_SAMPLE.length();
		for (int i = 0; i < size; i++) {
			buf.append(RANDOM_SAMPLE.charAt(random.nextInt(MAX_LEN)));
		}
		return buf.toString();
	}
	
	public static InputStream generateByteStream(int size) {
		InputStream is = new ByteArrayInputStream(generateByteString(size).getBytes());
		return is;
	}
	
	public static long randomSize() {
		Random random = new Random();
		return random.nextInt(10000) + 200L;
	}
	
	public static String outputStream2String(ByteArrayOutputStream os) {
			String result = null;
			try {
				result = new String(os.toByteArray(), "UTF-8");	
			} catch (UnsupportedEncodingException e) {
				// bug??
				result = new String(os.toByteArray());
			} 
			return result;
	}
	
	public static String upload(KuaipanAPI api, String path) 
			throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		return upload(api, path, randomSize());
	}
	
	public static String upload(KuaipanAPI api, String path, long size) 
			throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		long size_before = KPTestUtility.randomSize();
		String upload_content = KPTestUtility.generateByteString((int)size);
		InputStream is = new ByteArrayInputStream(upload_content.getBytes());
		api.uploadFile(path, is, size_before, true, new SleepyProgressListener());
		try {
			is.close();
		} catch (IOException e) {}
		return upload_content;
	}
	
	public static boolean isExisted(String path, KuaipanAPI api) 
			throws KuaipanIOException, KuaipanAuthExpiredException {
		try {
			api.metadata(path, null);
		} catch (KuaipanServerException e) {
			if (e.code == 404 && e.msg.equals("file not exist"))
				return false;
		}
		return true;
	}
	
	public static void openBrowser(String url) {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
		}
		try {
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
		}
	}
}
