package com.kuaipan.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanFile;

public class TestDelete {

	@Test
	public void testMove() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();

//		System.out.println(file);
		
		String path_root = "/删除mYfIΝλ"+System.currentTimeMillis();
		String path = path_root + "/删除IΝ";
		KuaipanFile file = api.createFolder(path);
		
		
		file = api.delete(path);
		
		assertTrue(!KPTestUtility.isExisted(path, api));
		assertTrue(KPTestUtility.isExisted(path_root, api));
	}

}
