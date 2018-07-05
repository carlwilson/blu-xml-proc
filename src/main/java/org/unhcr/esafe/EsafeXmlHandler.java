package org.unhcr.esafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

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

public class EsafeXmlHandler extends DefaultHandler {
	StringBuffer textBuffer;
	private Writer out;
	private String currentFileName;
	private String extension;
	private String objPath;
	private String newName;
	boolean isFileName = false;
	boolean isMime = false;
	boolean isObjPath = false;
	boolean toFile = false;

	public EsafeXmlHandler(boolean toFile) throws UnsupportedEncodingException {
		this.toFile = toFile;
		this.out = new OutputStreamWriter(System.out, "UTF8");
	}
	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================

	public void setCurrentFile(File file) throws IOException {
		if (this.toFile) {
			File parent = file.getParentFile();
			File output = new File(parent, file.getName() + ".fix");
			this.out = new FileWriter(output);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		emit("<?xml version='1.0' encoding='UTF-8'?>");
		nl();
	}

	@Override
	public void endDocument() throws SAXException {
		try {
			nl();
			this.out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		echoText();
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespaceAware
		if (eName.equals("filename")) {
			this.currentFileName = "";
			this.isFileName = true;
			return;
		}
		if (eName.equals("mimetype")) {
			this.isMime = true;
		}
		if (eName.equals("objectpath")) {
			this.isObjPath = true;
		}
		emit("<" + eName);
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name
				if ("".equals(aName))
					aName = attrs.getQName(i);
				emit(" ");
				emit(aName + "=\"" + attrs.getValue(i) + "\"");
			}
		}
		emit(">");
	}

	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName  // qualified name
	) throws SAXException {
		if (this.isFileName) {
			this.currentFileName = this.textBuffer.toString();
			this.textBuffer = null;
			this.isFileName = false;
			return;
		}
		if (this.isMime) {
			this.isMime = false;
			this.extension = MimeExtensionMapper
					.getExtForMime(this.textBuffer.toString().trim());
		}
		if (this.isObjPath) {
			this.isObjPath = false;
			this.objPath = this.textBuffer.toString();
		}
		echoText();
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespaceAware
		if (eName.equals("record")) {
			emit("  <filename>");
			if (!this.currentFileName.isEmpty()) {
				String newFileName = addExtension(this.currentFileName,
						this.extension);
				emit(newFileName);
				File originalFile = new File(
						findObjFileParent(new File("."), this.objPath),
						this.currentFileName);
				File renameTo = new File(originalFile.getParentFile(), newFileName);
				if (originalFile.isFile() && !renameTo.exists()) {
					System.out.println(
							"Found Object File " + originalFile.getAbsolutePath());
					System.out.println(
							"Renaming Object File " + renameTo.getAbsolutePath());
					originalFile.renameTo(renameTo);
				}
			}
			this.currentFileName = "";
			emit("</filename>");
			nl();
		}
		emit("</" + eName + ">");
	}

	@Override
	public void characters(char buf[], int offset, int len) {
		String s = new String(buf, offset, len);
		if (this.textBuffer == null) {
			this.textBuffer = new StringBuffer(s);
		} else {
			this.textBuffer.append(s);
		}
	}

	// ===========================================================
	// Utility Methods ...
	// ===========================================================

	// Display text accumulated in the character buffer
	private void echoText() throws SAXException {
		if (this.textBuffer == null)
			return;
		String s = "" + this.textBuffer;
		emit(s);
		this.textBuffer = null;
	}

	// Wrap I/O exceptions in SAX exceptions, to
	// suit handler signature requirements
	private void emit(String s) throws SAXException {
		try {
			this.out.write(s);
			this.out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	// Start a new line
	private void nl() throws SAXException {
		String lineEnd = System.getProperty("line.separator");
		try {
			this.out.write(lineEnd);
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	public static File findObjFileParent(final File currentDir,
			final String objPath) {
		System.out.println("Checking objPath " + objPath);
		String[] pathParts = objPath.split(":");

		File pathFile = currentDir;
		for (String pathPart : pathParts) {
			System.out.println("Checking pathPart " + pathPart);
			pathFile = findPathDir(pathFile, pathPart);
		}
		return pathFile.isDirectory() ? pathFile : null;
	}

	public static File findPathDir(final File currentDir,
			final String pathPart) {
		if ((currentDir == null) || !currentDir.isDirectory())
			throw new IllegalArgumentException("Current dir wrong.");
		for (File child : currentDir.listFiles()) {
			System.out.println("Checking File " + child.getAbsolutePath());
			if (child.getName().equals(pathPart))
				return child;
		}
		return null;
	}

	private static String addExtension(final String fileName,
			final String ext) {
		if (ext == null || ext.isEmpty()) {
			return fileName;
		}
		int i = fileName.lastIndexOf('.');
		String currentExt = "";
		if (i > 0) {
			currentExt = fileName.substring(i + 1);
		}
		if (!currentExt.isEmpty() && (currentExt.equals(ext)
				|| currentExt.startsWith(ext) || ext.startsWith(currentExt))) {
			return fileName;
		}
		return fileName + "." + ext;
	}
}
