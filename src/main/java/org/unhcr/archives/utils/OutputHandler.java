package org.unhcr.archives.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.xml.sax.SAXException;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 10 Jul 2018:09:00:06
 */

public final class OutputHandler {
	private StringBuffer textBuffer = null;
	private final Writer out;

	/**
	 * Default constructor, output to STDOUT
	 */
	public OutputHandler() throws UnsupportedEncodingException {
		this.out = new OutputStreamWriter(System.out, "UTF8"); //$NON-NLS-1$
	}

	// ===========================================================
	// Utility Methods ...
	// ===========================================================

	// Display text accumulated in the character buffer
	public void echoText() throws SAXException {
		if (this.textBuffer == null)
			return;
		emit(this.textBuffer.toString());
		this.textBuffer = null;
	}

	// Wrap I/O exceptions in SAX exceptions, to
	// suit handler signature requirements
	public void emit(String s) throws SAXException {
		try {
			this.out.write(s);
			this.out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e); //$NON-NLS-1$
		}
	}

	// Start a new line
	public void nl() throws SAXException {
		String lineEnd = System.getProperty("line.separator"); //$NON-NLS-1$
		try {
			this.out.write(lineEnd);
			this.out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e); //$NON-NLS-1$
		}
	}

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
	 * Add text to the buffer / start a new bufer if necessary
	 */
	public void addToBuffer(final String toAdd) {
		if (this.textBuffer == null) {
			this.textBuffer = new StringBuffer(toAdd);
		} else {
			this.textBuffer.append(toAdd);
		}
	}
}
