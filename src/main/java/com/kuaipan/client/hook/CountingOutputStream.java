package com.kuaipan.client.hook;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {

	@Override
	public void write(int arg0) throws IOException {
//		System.out.println("INFO - "+arg0+" bytes are written.");
	}

}
