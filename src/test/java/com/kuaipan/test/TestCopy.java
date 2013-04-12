package com.kuaipan.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanFile;

public class TestCopy {

	@Test
	public void testMove() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();

//		System.out.println(file);
		
		String path_root = "/复制mYfIΝλ"+System.currentTimeMillis();
		String path_from = path_root + "/复制前IΝ";
		String path_to = path_root + "/复制后IΝ";
		KuaipanFile file = api.createFolder(path_from);
		
		try {
			file = api.copy(path_from, path_root);
			assertTrue(false);
		} catch (KuaipanServerException e) {
			assertTrue(e.code == 403);
			assertTrue(e.msg.equals("file exist"));
		}
		
		file = api.copy(path_from, path_to);
		
		assertTrue(KPTestUtility.isExisted(path_to, api));
		assertTrue(KPTestUtility.isExisted(path_from, api));
	}

}
