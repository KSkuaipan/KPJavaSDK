package com.kuaipan.client.exception;

import java.io.IOException;

public class KuaipanIOException extends KuaipanException {

	private static final long serialVersionUID = 1L;

    public KuaipanIOException(IOException e) {
        super(e);
    }

    public KuaipanIOException(String detailMessage) {
        super(detailMessage);
    }

	public KuaipanIOException() {
	}
}
