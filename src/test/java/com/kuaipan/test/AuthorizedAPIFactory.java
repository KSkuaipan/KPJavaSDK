package com.kuaipan.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.AccessToken;
import com.kuaipan.client.model.TokenPair;
import com.kuaipan.client.session.OauthSession;

public class AuthorizedAPIFactory {

	private AuthorizedAPIFactory() {}
	private static KuaipanAPI api = null;
	
	public static synchronized KuaipanAPI getInstance() {
		if (api == null) {
			if (KPTestUtility.CONSUMER_KEY.isEmpty() || KPTestUtility.CONSUMER_SECRET.isEmpty()) {
				System.err.println("请先在 com.kuaipan.test.KPTestUtility 中设置你的consumer_key 和 consumer_secret。");
				System.exit(1);
			}
			
			OauthSession session = new OauthSession(KPTestUtility.CONSUMER_KEY, 
					KPTestUtility.CONSUMER_SECRET, OauthSession.Root.APP_FOLDER);
			api = new KuaipanAPI(session);
			
			AccessToken t = loadAuthFile();
			if (t != null) {
				api.getSession().setAuthToken(t.key, t.secret);

				try {
					api.accountInfo();
					return api;
				} catch (KuaipanException e) {
					api.getSession().unAuth();
				} 
			}

			
			try {
				String url = api.requestToken();
				System.out.println("到以下网址中使用你的快盘帐号授权，完成后按下ENTER键：\n" + url);					
				
				try {
					System.in.read();
				} catch (IOException e) {}
				
				api.accessToken();
				
				if (api.getSession().isAuth())
					saveAuthFile(api.getSession().token);
				
			} catch (KuaipanException e) {
				e.printStackTrace();
				return null;
			}

		}		
		return api;
	}
	
	
	private static AccessToken loadAuthFile() {
        File file=new File(KPTestUtility.AUTH_FILE_PATH);
        if(!file.exists())
            return null;
        
        BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			return null;
		}
		
		String key = null;
		String secret = null;
		try {
			key = br.readLine();
			secret = br.readLine();
		} catch (IOException e) {
			return null;
		}
		
		if (key == null || secret == null)
			return null;
		
		return new AccessToken(key, secret);
	}
	
	
	private static boolean saveAuthFile(TokenPair token) {
		FileWriter f = null;		
		try {
			f = new FileWriter(KPTestUtility.AUTH_FILE_PATH, false);
			f.write(token.key);
			f.write('\n');
			f.write(token.secret);		
			f.flush();
		} catch (IOException e) {
			return false;
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException e) {}
		}
		return true;
	}
	
	
	public static void main(String[] args) 
			throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
//		AccessToken t = new AccessToken("111111111111", "22222222222222");
//		
//		System.out.println(loadAuthFile());
//		System.out.println(saveAuthFile(t));
//		System.out.println(loadAuthFile());
		
		api = getInstance();
		System.out.println(api.accountInfo());
	}
}
