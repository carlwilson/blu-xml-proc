package org.unhcr.archives.esafe.blubaker.model;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 26 Apr 2019:02:43:12
 */

public class BadRecordException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8062832162553198019L;
	final int recordId;

	/**
	 * 
	 */
	public BadRecordException() {
		this(-1);
	}

	/**
	 * 
	 */
	public BadRecordException(final int recordId) {
		super();
		this.recordId = recordId;
	}

	/**
	 * @param message
	 */
	public BadRecordException(final int recordId, String message) {
		super(message);
		this.recordId = recordId;
	}

	/**
	 * @param cause
	 */
	public BadRecordException(final int recordId, Throwable cause) {
		super(cause);
		this.recordId = recordId;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BadRecordException(final int recordId, String message, Throwable cause) {
		super(message, cause);
		this.recordId = recordId;
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public BadRecordException(final int recordId, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.recordId = recordId;
	}

}
