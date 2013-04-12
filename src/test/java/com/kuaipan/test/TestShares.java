package com.kuaipan.test;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.junit.Test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.client.model.KuaipanPublicLink;
import com.kuaipan.client.model.KuaipanUser;

public class TestShares {

	@Test
	public void testMetadata() 
			throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException, UnsupportedEncodingException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		
		// empty folder
		String folder = "/空文件夹mYfIlEΙΝλ" + System.currentTimeMillis();
		api.createFolder(folder);
		
		String path = folder+"/文件1.txt";
		String content = KPTestUtility.upload(api, path);
		
		String link_url = null;
		KuaipanPublicLink link = api.shares(path, null, null);
		link_url = link.url;
		assertTrue(link.url.startsWith("http://www.kuaipan"));
		assertTrue(link.access_code == null);
		
		
		String access_code = "aaaaaa";
		link = api.shares(path, null, access_code);
		assertTrue(link.url.equals(link.url));
		assertTrue(link.access_code.equals(access_code));
	}
}
