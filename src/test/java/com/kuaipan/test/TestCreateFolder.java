package com.kuaipan.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanFile;

public class TestCreateFolder {

	@Test
	public void testCreateFolder() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		
		// root
		String path_root = "/";
		KuaipanFile file = api.createFolder(path_root);
//		System.out.println(file);
		
		// non-existed folder
		String path_n_folder = "/mYfIlEΙΝλ"+System.currentTimeMillis();
		file = api.createFolder(path_n_folder);
		assertTrue(file.path.equals(path_n_folder));
		
		// non-existed path
		String path_n_path = "/mYfIlEΙΝλ"+System.currentTimeMillis()+"/child文件";
		file = api.createFolder(path_n_path);
		assertTrue(file.path.equals(path_n_path));
		
		// existed file
		String path_existed = "/我的应用";
		file = api.createFolder(path_existed);
		assertTrue(file.path.equals(path_existed));
		
		// partial existed path
		String path_part_path = path_n_folder+"/mYfIlEΙΝλ"+System.currentTimeMillis();
		file = api.createFolder(path_part_path);
		assertTrue(file.path.equals(path_part_path));
	}

}
