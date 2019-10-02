package org.unhcr.archives.esafe.blubaker;

import java.io.IOException;
import java.nio.file.Files;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.unhcr.archives.utils.ExportDetails;
import org.unhcr.archives.utils.XmlCharBuffer;
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

public final class BluBakerXmlHandler extends DefaultHandler {
	static final SAXParserFactory spf = SAXParserFactory.newInstance();
	static {
		spf.setNamespaceAware(true);
	}
	static final SAXParser saxParser;
	static {
		try {
			saxParser = spf.newSAXParser();
		} catch (ParserConfigurationException | SAXException excep) {
			throw new IllegalStateException("Couldn't initialise SAX XML Parser.", excep); //$NON-NLS-1$
		}
	}
	private String currEleName;
	private XmlCharBuffer buffer = new XmlCharBuffer();
	private final ElementProcessor eleProc = new ElementProcessor();
	private final RecordProcessor recProc;

	public BluBakerXmlHandler(final ExportDetails expDets) {
		super();
		this.recProc = new RecordProcessor(expDets);
	}

	public RecordProcessor processExports() throws IOException, SAXException {
		saxParser.parse(Files.newInputStream(ExportDetails.bluExportXmlPath(this.recProc.exportDetails.exportRoot)),
				this);
		return this.recProc;

	}

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================

	@Override
	public void endDocument() {
		assert (this.eleProc.getRecordCount() == this.eleProc.getMaxRecNum());
	}

	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) {
		// Get the current ele name
		this.currEleName = deriveEleName(sName, qName);
		if (ElementProcessor.isRecordEle(this.currEleName)) {
			this.eleProc.recordStart(attrs);
		}
	}

	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) {
		this.currEleName = deriveEleName(sName, qName);
		if (ElementProcessor.isRecordEle(this.currEleName)) {
			this.recProc.addRecord(this.eleProc.buildRecord());
		} else {
			this.eleProc.processElement(this.currEleName, this.buffer.voidBuffer());
		}

	}

	private static String deriveEleName(final String sName, final String qName) {
		return ("".equals(sName)) ? qName : sName; // element name //$NON-NLS-1$
	}

	@Override
	public void characters(char buf[], int offset, int len) {
		String toAdd = new String(buf, offset, len);
		this.buffer.addToBuffer(toAdd);
	}
}
