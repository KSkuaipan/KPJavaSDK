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
import com.kuaipan.client.model.KuaipanUser;

public class TestMetadata {

	@Test
	public void testMetadata() 
			throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException, UnsupportedEncodingException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		
		// root
		KuaipanFile file = api.metadata("/", false);
//		assertTrue(file.file_id==null);
//		assertTrue(file.name==null);
//		assertTrue(file.rev==null);
//		assertTrue(file.type==null);
		assertTrue(file.hash!=null);
		assertTrue(file.root.equals("app_folder") || file.root.equals("kuaipan"));
		assertTrue(file.path.equals("/"));
		assertTrue(file.is_deleted==false);
		assertTrue(file.size==0);
		assertTrue(file.files==null || file.files.size()==0);
		
		try {
			file = api.metadata("/不存在的文件", null);
		} catch (KuaipanServerException e) {
			assertTrue(e.msg.equals("file not exist"));
		}
		
		// list folder
		file = api.metadata("/我的应用", true);
		assertTrue(file.file_id != null);
		assertTrue(file.name.equals("我的应用"));
		assertTrue(file.rev != null);
		assertTrue(file.type.equals("folder"));
		assertTrue(file.hash != null);
		assertTrue(file.root.equals("app_folder") || file.root.equals("kuaipan"));
		assertTrue(file.path.equals("/我的应用"));
		assertTrue(file.is_deleted == false);
		assertTrue(file.size==0);
		assertTrue(file.files.size() > 0);
		
		// child
		KuaipanFile child = file.files.get(0);
		assertTrue(child.file_id != null);
		assertTrue(child.name != null);
		assertTrue(child.rev != null);
		assertTrue(child.type.equals("folder") || child.type.equals("file"));
		assertTrue(child.hash == null);
		assertTrue(child.root == null);
		assertTrue(child.path == null);
		assertTrue(child.is_deleted == false);
		assertTrue(child.size>=0);
		assertTrue(child.files == null);
		
		// empty folder
		String folder = "/空文件夹mYfIlEΙΝλ" + System.currentTimeMillis();
		api.createFolder(folder);
		file = api.metadata(folder, true);
		assertTrue(file.files.size() == 0);
		
		String content = KPTestUtility.upload(api, folder+"/1.txt");
		file = api.metadata(folder, true);
		child = file.files.get(0);
		assertTrue(file.files.size() == 1);
		assertTrue(child.size == content.length());
		
	}
}
