package com.kuaipan.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.junit.Test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.KuaipanAPI.ConvType;
import com.kuaipan.client.KuaipanAPI.ConvView;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.hook.CountingOutputStream;
import com.kuaipan.client.hook.SleepyProgressListener;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.client.model.KuaipanHTTPResponse;
import com.kuaipan.client.model.KuaipanUser;

public class TestConv {

	@Test
	public void testThumbnail() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException, IOException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		String path = "/我的应用/测试图片.gif";
		
		File f = new File("res/test.gif");
		long size = f.length();
		
		InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			assertTrue(false);
		}
		
		KuaipanFile file_before = api.uploadFile(path, is, size, true, null);
		try {
			is.close();
		} catch (IOException e) {}
		System.out.println(file_before);
		
		CountingOutputStream os = new CountingOutputStream();
		KuaipanHTTPResponse resp = api.thumbnail(path, os, null);
		os.close();
		KPTestUtility.openBrowser(resp.url.url);		
	}
	
	
	@Test
	public void testDocumentView() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException, IOException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		String path = "/我的应用/测试文档.doc";
		
		File f = new File("res/test.doc");
		long size = f.length();
		
		InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			assertTrue(false);
		}
		
		KuaipanFile file_before = api.uploadFile(path, is, size, true, null);
		try {
			is.close();
		} catch (IOException e) {}
		System.out.println(file_before);
		
		CountingOutputStream os = new CountingOutputStream();
		KuaipanHTTPResponse resp = api.documentView(ConvType.DOC, ConvView.IPAD, path, os, null);
		os.close();
		KPTestUtility.openBrowser(resp.url.url);		
	}
}
