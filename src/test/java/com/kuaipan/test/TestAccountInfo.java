package com.kuaipan.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanUser;

public class TestAccountInfo {

	@Test
	public void testAccountInfo() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		KuaipanAPI api = AuthorizedAPIFactory.getInstance();
		
		KuaipanUser user = api.accountInfo();
		
		assertTrue(user.max_file_size == 300 * 1024 * 1024); // 300M
		assertTrue(user.quota_total != 0);
		assertTrue(user.quota_used != 0);
		assertTrue(user.quota_recycled == 0);
		assertTrue(user.user_id != 0);
	}

}
