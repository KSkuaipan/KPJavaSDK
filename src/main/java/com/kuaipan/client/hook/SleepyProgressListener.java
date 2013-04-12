package com.kuaipan.client.hook;

import com.kuaipan.client.ProgressListener;

/**
 * a sample listener for debugging.
 * @author Ilcwd
 *
 */
public class SleepyProgressListener implements ProgressListener {

	@Override
	public void started() {
		System.out.println("INFO - Started.");
	}

	@Override
	public int getUpdateInterval() {
		return 500;
	}

	@Override
	public void processing(long bytes, long total) {
		System.out.println("INFO - "+bytes+" / "+total+" bytes are done.");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
	}

	@Override
	public void completed() {
		System.out.println("INFO - Ended.");
	}

}
