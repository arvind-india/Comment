package ru.commenthere.comment.net;

public class ConnectionClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionClientException() {
		super();
	}

	public ConnectionClientException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ConnectionClientException(String detailMessage) {
		super(detailMessage);
	}

	public ConnectionClientException(Throwable throwable) {
		super(throwable);
	}

}
