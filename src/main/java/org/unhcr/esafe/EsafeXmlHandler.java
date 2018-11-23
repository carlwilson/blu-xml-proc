package org.unhcr.esafe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.unhcr.esafe.blubaker.Record;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 22 Jun 2018:01:25:39
 */

public final class EsafeXmlHandler extends DefaultHandler {
	static final SAXParserFactory spf = SAXParserFactory.newInstance();
	static {
		spf.setNamespaceAware(true);
	}
	static final SAXParser saxParser;
	static {
		try {
			saxParser = spf.newSAXParser();
		} catch (ParserConfigurationException | SAXException excep) {
			throw new IllegalStateException(
					"Couldn't initialise SAX XML Parser.", excep);
		}
	}
	private String currEleName;
	private XmlCharBuffer buffer = new XmlCharBuffer();
	private final ElementProcessor eleProc = new ElementProcessor();
	private final RecordProcessor recProc;

	public EsafeXmlHandler(final Path exportRoot) {
		this.recProc = new RecordProcessor(exportRoot);
	}
	
	public RecordProcessor processExports() throws IOException, SAXException {
		File dirToParse = this.recProc.exportRoot.toFile();
		for (File child : dirToParse.listFiles()) {
			if (child.isFile()
					&& child.getName().toLowerCase().endsWith(".xml")) {
				saxParser.parse(child, this);
			}
		}
		return this.recProc;

	}

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================

	@Override
	public void endDocument() throws SAXException {
		assert(this.eleProc.getRecordCount() == this.eleProc.getMaxRecNum());
	}

	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		// Get the current ele name
		this.currEleName = deriveEleName(sName, qName);
		if (ElementProcessor.isRecordEle(this.currEleName)) {
			this.eleProc.recordStart(attrs);
		}
	}

	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName  // qualified name
	) throws SAXException {
		this.currEleName = deriveEleName(sName, qName);
		if (ElementProcessor.isRecordEle(this.currEleName)) {
			Record rec = this.eleProc.buildRecord();
			this.recProc.addRecord(rec);
		} else {
			this.eleProc.processElement(this.currEleName, this.buffer.voidBuffer().trim());
		}
	
	}

	private static String deriveEleName(final String sName,
			final String qName) {
		return ("".equals(sName)) ? qName : sName; // element name
	}

	@Override
	public void characters(char buf[], int offset, int len) {
		String toAdd = new String(buf, offset, len);
		this.buffer.addToBuffer(toAdd);
	}
}
