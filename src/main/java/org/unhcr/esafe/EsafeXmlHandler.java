package org.unhcr.esafe;

import java.io.File;
import java.io.IOException;

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
			// TODO Auto-generated catch block
			throw new IllegalStateException(
					"Couldn't initialise SAX XML Parser.", excep);
		}
	}
	private String currEleName;
	private XmlCharBuffer buffer = new XmlCharBuffer();
	private final ProcessorOptions opts;
	private final ElementProcessor eleProc = new ElementProcessor();
	private final RecordProcessor recProc = new RecordProcessor();

	public EsafeXmlHandler(final ProcessorOptions opts) {
		this.opts = opts;
	}
	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================

	public void processExports() throws IOException, SAXException {
		for (File dirToParse : this.opts.toProcess) {
			for (File child : dirToParse.listFiles()) {
				if (child.isFile()
						&& child.getName().toLowerCase().endsWith(".xml")) {
					saxParser.parse(child, this);
				}
			}
		}

	}

	@Override
	public void startDocument() throws SAXException {
		// Output the XML processing instruction
		if (!this.opts.isEnhanced)
			return;
//		this.outHandler.emit("<?xml version='1.0' encoding='UTF-8'?>");
//		this.outHandler.nl();
	}

	@Override
	public void endDocument() throws SAXException {
		assert(this.eleProc.getRecordCount() == this.eleProc.getMaxRecNum());
		if (!this.opts.isEnhanced)
			return;
//			this.outHandler.nl();
//			this.outHandler.nl();
	}

	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		// Throw the text to output
//		if (this.opts.isEnhanced)
//			this.outHandler.echoText();
//		else
//			this.outHandler.voidBuffer();
//		// Get the current ele name
		this.currEleName = deriveEleName(sName, qName);
		if (ElementProcessor.isRecordEle(this.currEleName)) {
			this.eleProc.recordStart(attrs);
		}
//		if (this.isRecordEle()) {
//			this.recBuilder = new RecordBuilder();
//		}
//		if (this.opts.isEnhanced)
//			outputEleStart(this.outHandler, this.currEleName, attrs);
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
//		if (this.isRecordEle()) {
//			this.processRecord();
//		} else if (!"dataextract".equals(this.currEleName)) {
//			this.recBuilder.processEle(this.currEleName,
//					this.outHandler.getBufferValue());
//		}
//		if (this.opts.isEnhanced) {
//			this.outHandler.echoText();
//			this.outHandler.emit("</" + this.currEleName + ">");
//		} else
//			this.outHandler.voidBuffer();
		
	}

	private static String deriveEleName(final String sName,
			final String qName) {
		return ("".equals(sName)) ? qName : sName; // element name
	}

//
//	private static void outputEleStart(final OutputHandler handler,
//			final String eleName, final Attributes attrs) throws SAXException {
//		handler.emit("<" + eleName);
//		if (attrs != null) {
//			for (int i = 0; i < attrs.getLength(); i++) {
//				String aName = attrs.getLocalName(i); // Attr name
//				if ("".equals(aName))
//					aName = attrs.getQName(i);
//				handler.emit(" ");
//				handler.emit(aName + "=\"" + attrs.getValue(i) + "\"");
//			}
//		}
//		handler.emit(">");
//	}

	@Override
	public void characters(char buf[], int offset, int len) {
		String toAdd = new String(buf, offset, len);
		this.buffer.addToBuffer(toAdd);
	}

	public static File findObjParentDir(final File currentDir,
			final String objPath) {
		if (objPath == null)
			return null;
		String[] pathParts = objPath.split("\\\\");
		return findExportParent(currentDir, pathParts);
	}

	public static File findExportParent(final File rootDir,
			String[] pathParts) {
		String rootName = getRootName(rootDir);
		StringBuffer itemPath = new StringBuffer(rootDir.getAbsolutePath());
		boolean isAppend = false;
		for (String pathPart : pathParts) {
			if (isAppend) {
				itemPath.append(File.separator);
				itemPath.append(pathPart);
			} else if (rootName.equals(pathPart)) {
				isAppend = true;
			}
		}
		return new File(itemPath.toString());
	}

	private static String getRootName(final File root) {
		String rootName = root.getName();
		return (".".equals(rootName)) ? root.getParentFile().getName()
				: rootName;
	}

//	private void report() {
//		RecordRenamer renamer = new RecordRenamer(this.currentDir);
//		for (RecordDetails record : this.details) {
//			renamer.checkRecord(record.objPath, record.objName);
//			System.out.println("");
//		}
//		System.out.println(String.format("Total files in export: %d",
//				Integer.valueOf(this.details.size())));
//	}

}
