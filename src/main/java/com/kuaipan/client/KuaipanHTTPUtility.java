package com.kuaipan.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.model.KuaipanHTTPResponse;
import com.kuaipan.client.model.KuaipanURL;

public class KuaipanHTTPUtility {
	public final static int BUFFER_SIZE = 4048;
	public final static String API_HOST = "openapi.kuaipan.cn";
	public final static String CONTENT_HOST = "api-content.dfs.kuaipan.cn";
	public final static String CONV_HOST = "conv.kuaipan.cn";
	
	public final static String AUTH_URL = "https://www.kuaipan.cn/api.php?ac=open&op=authorise&oauth_token=";
	public final static String UPLOAD_LOCATE_URL = "http://api-content.dfs.kuaipan.cn/1/fileops/upload_locate";

	
	private KuaipanHTTPUtility() {}
	
	static {
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
	}
	
	public static KuaipanHTTPResponse requestByGET(KuaipanURL url) 
			throws KuaipanIOException {
		KuaipanHTTPResponse resp = doGet(url);
		return resp;
	}
	
	public static KuaipanHTTPResponse doUpload(KuaipanURL baseurl, InputStream datastream,
			long size, ProgressListener lr) throws KuaipanIOException {
		KuaipanHTTPResponse resp = new KuaipanHTTPResponse();

		HttpURLConnection con = getConnectionFromUrl(baseurl.queryByGetUrl(), "POST");
        
		multipartUploadData(con, datastream, size, lr);
		
		resp.code = getResponseHTTPStatus(con);
		resp.content = getStringDataFromConnection(con);
		resp.url = baseurl;
		
		if (con != null)
			con.disconnect();
		
		return resp;
	}
	
	public static KuaipanHTTPResponse doDownload(KuaipanURL baseurl, OutputStream os, ProgressListener lr) 
			throws KuaipanIOException{
		KuaipanHTTPResponse resp = new KuaipanHTTPResponse();		
		HttpURLConnection con = getConnectionFromUrl(baseurl.url, "GET");		
		resp.code = getResponseHTTPStatus(con);
		resp.url = baseurl;
		
		if(resp.code == KuaipanHTTPResponse.KUAIPAN_OK) {
			writeStreamFromConnection(con, os, lr);
			resp.content = null;
		} else {
			resp.content = getStringDataFromConnection(con);
		}
		
		if (con != null) 
			con.disconnect();
		
		return resp;
	}
	
	private static int getResponseHTTPStatus(HttpURLConnection con) 
			throws KuaipanIOException {
		int code = KuaipanHTTPResponse.KUAIPAN_UNKNOWNED_ERROR;
		try {
			code = con.getResponseCode();
		} catch (IOException e) {
			// bad HTTP format from server, it may never happen
			if (con != null) con.disconnect();
			throw new KuaipanIOException(e);
		}
		return code;
	}
	
	private static String stream2String(InputStream is) 
			throws KuaipanIOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[BUFFER_SIZE];
		int len = 0;
		try {
			while((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
		} catch (IOException e) {
			// 
			throw new KuaipanIOException(e);
		}
		
		String result = null;
		try {
			result = new String(baos.toByteArray(), "UTF-8");	
		} catch (UnsupportedEncodingException e) {
			// bug??
			result = new String(baos.toByteArray());
		} finally {
			try {
				baos.close();
			} catch (IOException e) {}
		}
		return result;
	}
	
	private static String getStringDataFromConnection(HttpURLConnection con) 
			throws KuaipanIOException{
		InputStream is = null;
		try {
			is = con.getInputStream();
		} catch (IOException e) {
			is = con.getErrorStream();
		}		
		
		try {
			return stream2String(is);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) { }
		}
	}
	
	private static void writeStreamFromConnection(HttpURLConnection con, OutputStream os, ProgressListener lr) 
			throws KuaipanIOException {
		InputStream is = null;
		try {
			is = con.getInputStream();
		} catch (IOException e) {
			is = con.getErrorStream();
		}
		int total = con.getContentLength();
		
		if (lr != null) lr.started();
		
		try {
			bufferedWriting(os, is, total, lr);
		} finally {
			if (lr != null) lr.completed();
			if (is != null)
				try {
					is.close();
				} catch (IOException e) { }
		}
	}
	
	private static HttpURLConnection getConnectionFromUrl(String baseurl, String method) 
			throws KuaipanIOException {
		URL url = null;
		HttpURLConnection con = null;
		try {
			url = new URL(baseurl);
		} catch (MalformedURLException e) {
			// never come here
			e.printStackTrace();
		}
		
		try {
			con = (HttpURLConnection)url.openConnection();
		} catch (IOException e2) {
			// some IO error, maybe timeout.
			throw new KuaipanIOException(e2);	
		}

		try {
			con.setRequestMethod(method);
		} catch (ProtocolException e1) {
			// never come here
			if (con != null) {
				con.disconnect();
				con = null;
			}
		}
		return con;
	}
	
	private static void multipartUploadData(
			HttpURLConnection con, InputStream datastream, 
			long size, ProgressListener lr) 
			throws KuaipanIOException {
		con.setDoOutput(true);
		String boundary = "--------------------------"+System.currentTimeMillis();
        con.setRequestProperty("connection", "keep-alive");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        con.setRequestProperty("Cache-Control", "no-cache");
        boundary = "--" + boundary;
        StringBuffer sb = new StringBuffer();
        sb.append(boundary);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"Filedata\"; filename=\""
                + "myfile" + "\"\r\n");
        sb.append("Content-Type: application/octet-stream\r\n\r\n");
        String endStr = "\r\n" 
        				+ boundary 
        				+ "\r\nContent-Disposition: form-data; name=\"Upload\"\r\n\r\nSubmit Query\r\n" 
        				+ boundary 
        				+ "--\r\n";
        byte[] endData = endStr.getBytes();
        OutputStream os = null;
        try {
            con.connect();            
            os = con.getOutputStream();
            os.write(sb.toString().getBytes());
            
            if (lr != null) {
    			lr.started();
    		}
    		bufferedWriting(os, datastream, size, lr);
	        os.write(endData);
	        
        } catch (IOException e5) {
        	throw new KuaipanIOException(e5);
        } finally {
        	try {
        		if (os != null) {			        
			        os.flush();
			        os.close();
	        	}
        	} catch (IOException e) {}
        	if (lr != null)
        		lr.completed();
        }
	}
	
	private static KuaipanHTTPResponse doGet(KuaipanURL kpurl) 
			throws KuaipanIOException {
		KuaipanHTTPResponse resp = new KuaipanHTTPResponse();
		
		HttpURLConnection con = getConnectionFromUrl(kpurl.url, "GET");
		resp.code = getResponseHTTPStatus(con);
		resp.content = getStringDataFromConnection(con);
		resp.url = kpurl;
		
		if (con != null)
			con.disconnect();
		
		return resp;
	}
	
	/**
	 * writing with buffer and progress listening
	 * @param to_write
	 * @param to_read
	 * @param total
	 * @param lr
	 * @throws KuaipanIOException
	 */
	private static void bufferedWriting(OutputStream to_write, InputStream to_read, long total, ProgressListener lr)
			throws KuaipanIOException {
		long last_triggered_time = 0L;
		int len = 0;
		int count = 0;
		byte[] buf = new byte[BUFFER_SIZE];
		try {
	        while((len = to_read.read(buf)) != -1) {
	        	to_write.write(buf, 0, len);
	        	count += len;
	        	long current_time = System.currentTimeMillis();
	        	if (lr != null && 
	        			(current_time-last_triggered_time) > lr.getUpdateInterval()) {
	        		lr.processing(count, total);
					last_triggered_time = current_time;
				}
	        }	        
        } catch (IOException e) {
        	throw new KuaipanIOException(e);
        }
	}
	

	
	public static class UploadHostFactory {
		private static String upload_host = null;
		private static long last_refresh_time = 0;
		
		private static final long REFRESH_INTERVAL = 3600 * 10 * 1000;
		
		private UploadHostFactory() {}
		
		public static String getUploadHost() throws KuaipanIOException {
			refreshUploadHost();
			return upload_host;
		}
		
		private static synchronized void refreshUploadHost() 
				throws KuaipanIOException {
			if (upload_host == null || 
					(System.currentTimeMillis() - last_refresh_time) > REFRESH_INTERVAL ) {
				KuaipanHTTPResponse resp = doGet(new KuaipanURL(UPLOAD_LOCATE_URL));
				if (resp.content == null)
					throw new KuaipanIOException(resp.toString());
				
				Map<String, Object> mapresult = JSONUtility.parse(resp.content);
				if (mapresult == null)
					throw new KuaipanIOException(resp.toString());
				
				String url = (String) mapresult.get("url");
				if (url == null)
					throw new KuaipanIOException(resp.toString());
				
				if (url.startsWith("http://"))
					url = url.replaceFirst("http://", "");
				else if (url.startsWith("https://"))
					url = url.replaceFirst("https://", "");
				
				if (url.endsWith("/"))
					url = url.substring(0, url.length()-1);
				
				upload_host = url;
				last_refresh_time = System.currentTimeMillis();
			}
		}
	}
	

}
