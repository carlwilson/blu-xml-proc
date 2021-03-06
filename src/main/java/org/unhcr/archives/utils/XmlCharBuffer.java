/**
 * 
 */
package org.unhcr.archives.utils;

/**
 * @author cfw
 *
 */
public final class XmlCharBuffer {
	private StringBuffer textBuffer = null;
	/**
	 * Get the value of the text buffer
	 */
	public String getBufferValue() {
		return (this.textBuffer == null) ? null : this.textBuffer.toString();
	}

	/**
	 * Get the value of the text buffer and erase the buffer contents
	 */
	public String voidBuffer() {
		String retVal = this.getBufferValue();
		this.textBuffer = null;
		return retVal;
	}

	/**
	 * Add text to the buffer / start a new buffer if necessary
	 */
	public void addToBuffer(final String toAdd) {
		if (toAdd.trim().isEmpty()) {
			this.textBuffer = new StringBuffer("");
			return;
		}
		if (this.textBuffer == null) {
			this.textBuffer = new StringBuffer(toAdd);
		} else {
			this.textBuffer.append(toAdd);
		}
	}
}
