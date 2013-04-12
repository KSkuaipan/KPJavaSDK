package com.kuaipan.client.exception;

public class KuaipanException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected KuaipanException() {
        super();
    }

    public KuaipanException(String detailMessage) {
        super(detailMessage);
    }

    public KuaipanException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public KuaipanException(Throwable throwable) {
        super(throwable);
    }
}
