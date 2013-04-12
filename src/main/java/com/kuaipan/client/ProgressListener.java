package com.kuaipan.client;

public interface ProgressListener {
	/**
	 * be called when uploading/downloading starts.
	 */
	public void started();
	
	/**
	 * determine at least how long (in milliseconds) between each call {@link ProgressListener#processing(long)} .
	 */
	public int getUpdateInterval();
	
	/**
	 * be called when some amount of bytes is uploaded/downloaded.
	 * @param bytes
	 */
	public void processing(long bytes, long total);
	
	/**
	 * be called when the task is completed.
	 */
	public void completed();
}
