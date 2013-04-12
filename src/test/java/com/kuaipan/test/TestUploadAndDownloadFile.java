package com.kuaipan.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.hook.SleepyProgressListener;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.client.model.KuaipanHTTPResponse;
import com.kuaipan.client.model.KuaipanUser;

public class TestUploadAndDownloadFile {

	@Test
	public void testUploadandDownload() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException, IOException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		String root_path = null;
		
		if (KPTestUtility.randomSize() % 2 == 0) 
			root_path = "/文件FileΓε"+System.currentTimeMillis()+".txt";
		else
			root_path = "/我的应用/应用下面的FileΓε"+System.currentTimeMillis()+".txt";
		
		System.out.println("INFO - " + root_path);
		
		long size_before = KPTestUtility.randomSize();
		InputStream is = KPTestUtility.generateByteStream((int)size_before);
		KuaipanFile file_before = api.uploadFile(root_path, is, size_before, true, null);
		try {
			is.close();
		} catch (IOException e) {}
		
//		System.out.println(file_before);
		
		assertTrue(file_before.size == size_before);
		assertTrue(file_before.rev.equals("1"));
		assertTrue(file_before.is_deleted == false);
		assertTrue(file_before.file_id != null);
		
		// upload again
		long size_after = KPTestUtility.randomSize();
		String upload_content = KPTestUtility.generateByteString((int)size_after);
		is = new ByteArrayInputStream(upload_content.getBytes());
		KuaipanFile file_after = api.uploadFile(root_path, is, size_after, true, null);
		is.close();
		
		assertTrue(file_after.size == size_after);
		assertTrue(file_after.rev.equals("2"));
		assertTrue(file_after.is_deleted == false);
		assertTrue(file_after.file_id.equals(file_before.file_id));
		
		// no overwrite
		long size_3rd = KPTestUtility.randomSize();
		is = KPTestUtility.generateByteStream((int)size_3rd);
		try {
			api.uploadFile(root_path, is, size_3rd, false, null);
		} catch (KuaipanServerException e) {
			// 405 not allowed
			assertTrue(e.code == 405);
		} finally {
			is.close();
		}
		
		// download file
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		KuaipanHTTPResponse resp = api.downloadFile(root_path, os, null);
		assertTrue(resp.code == 200);
		String download_content = KPTestUtility.outputStream2String(os);
		os.close();
		assertTrue(download_content.length() == size_after);
		assertTrue(download_content.equals(upload_content));
	}
	
	@Test
	public void testInvalidUploadAndDownload() 
			throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException, IOException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		
		String root_path = "/不存在的路径"+System.currentTimeMillis()+"/foo.txt";
		
		// upload to non-existed path
		long size = KPTestUtility.randomSize();
		InputStream is = KPTestUtility.generateByteStream((int)size);
		try {
			api.uploadFile(root_path, is, size, false, null);
		} catch (KuaipanServerException e) {
			// 405 not allowed
			assertTrue(e.code == 405);
		} finally {
			is.close();
		}
		
		// download from non-existed path		
		OutputStream os = new ByteArrayOutputStream();
		try {
			api.downloadFile(root_path, os, null);
		} catch (KuaipanServerException e) {
			assertTrue(e.code == 403);
		}
		os.close();
		
		// download directory
		os = new ByteArrayOutputStream();
		String my_directory = "/我的应用";
		api.createFolder(my_directory);
		try {
			
			api.downloadFile(my_directory, os, null);
		} catch (KuaipanServerException e) {
			assertTrue(e.code == 500);
		}
		os.close();
	}

}
