package org.unhcr.esafe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
			// TODO Auto-generated catch block
			throw new IllegalStateException(
					"Couldn't initialise SAX XML Parser.", excep);
		}
	}
	private OutputHandler outHandler;
	private final List<RecordDetails> details = new ArrayList<>();
	private RecordBuilder recBuilder;
	private String currEleName;
	private final ProcessorOptions opts;
	private File currentDir = new File(".");

	public BluBakerXmlHandler(final ProcessorOptions opts) {
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
					this.currentDir = child.getParentFile().getCanonicalFile();
					this.details.clear();
					if (this.opts.isToFile) {
						this.outHandler = new OutputHandler(child);
					} else {
						this.outHandler = new OutputHandler();
					}
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
		this.outHandler.emit("<?xml version='1.0' encoding='UTF-8'?>");
		this.outHandler.nl();
	}

	@Override
	public void endDocument() throws SAXException {
		if (!this.opts.isEnhanced)
			return;
		if (this.opts.isEnhanced) {
			this.outHandler.nl();
			this.outHandler.nl();
		}
		this.report();
	}

	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		// Throw the text to output
		if (this.opts.isEnhanced)
			this.outHandler.echoText();
		else
			this.outHandler.voidBuffer();
		// Get the current ele name
		this.currEleName = deriveEleName(sName, qName);
		if (this.isRecordEle()) {
			this.recBuilder = new RecordBuilder();
		}
		if (this.opts.isEnhanced)
			outputEleStart(this.outHandler, this.currEleName, attrs);
	}

	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName  // qualified name
	) throws SAXException {
		this.currEleName = deriveEleName(sName, qName);
		if (this.isRecordEle()) {
			this.processRecord();
		} else if (!"dataextract".equals(this.currEleName)) {
			this.recBuilder.processEle(this.currEleName,
					this.outHandler.getBufferValue());
		}
		if (this.opts.isEnhanced) {
			this.outHandler.echoText();
			this.outHandler.emit("</" + this.currEleName + ">");
		} else
			this.outHandler.voidBuffer();

		this.currEleName = null;
	}

	private static String deriveEleName(final String sName,
			final String qName) {
		return ("".equals(sName)) ? qName : sName; // element name
	}

	private static void outputEleStart(final OutputHandler handler,
			final String eleName, final Attributes attrs) throws SAXException {
		handler.emit("<" + eleName);
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name
				if ("".equals(aName))
					aName = attrs.getQName(i);
				handler.emit(" ");
				handler.emit(aName + "=\"" + attrs.getValue(i) + "\"");
			}
		}
		handler.emit(">");
	}

	@Override
	public void characters(char buf[], int offset, int len) {
		String toAdd = new String(buf, offset, len);
		this.outHandler.addToBuffer(toAdd);
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

	private void report() {
		RecordRenamer renamer = new RecordRenamer(this.currentDir);
		for (RecordDetails record : this.details) {
			renamer.checkRecord(record.objPath, record.objName);
			System.out.println("");
		}
		System.out.println(String.format("Total files in export: %d",
				Integer.valueOf(this.details.size())));
	}

	static String extFromFileName(final String name) {
		int i = name.lastIndexOf('.');
		return (i > 0) ? name.substring(i + 1) : null;
	}

	private void processRecord() throws SAXException {

		if (!this.recBuilder.fileName.trim().isEmpty()) {
			RecordDetails recDetails = this.recBuilder.build();
			if (this.opts.isEnhanced) {
				StringBuffer ele = new StringBuffer("\n    <fixedFilename>");
				ele.append(recDetails.objName.newName());
				ele.append("</fixedFilename>");
				this.outHandler.emit(ele.toString());
			}
			this.details.add(recDetails);
		}
		this.recBuilder = null;
	}

	static void renameObjectFile(final File originalFile,
			final File renameTo) {
		if (originalFile.isFile()) {
			// Original still exists, so not renamed
			if (!renameTo.exists()) {
				System.out
						.println(String.format("File %s WILL be renamed to %s",
								originalFile, renameTo));
				// originalFile.renameTo(renameTo);
			} else {
				System.err.println("RENAME TARGET EXISTS");
				System.err.println("====================");
				System.err.println(String.format(
						"File %s CANNOT be renamed to %s as target file exists already",
						originalFile, renameTo));
			}
		} else if (renameTo.exists()) {
			// Original gone and new file in place means rename already
			// performed
			System.out.println(
					String.format("File %s has already been renamed to %s",
							originalFile, renameTo));
		} else {
			// Can't find original or rename target == lost file
			System.err.println("LOST FILE");
			System.err.println("=========");
			System.err.println(
					String.format("Orignal file %s is missing", originalFile));
			System.err.println(String
					.format("Rename target file %s is also missing", renameTo));
			System.exit(2);
		}
	}

	private boolean isRecordEle() {
		return "record".equals(this.currEleName);
	}

	static class RecordDetails {
		final ObjectName objName;
		final ObjectPath objPath;
		final int fileSize;

		RecordDetails(final ObjectName objName, final ObjectPath objPath,
				final int fileSize) {
			this.objName = objName;
			this.objPath = objPath;
			this.fileSize = fileSize;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "RecordDetails [objName=" + this.objName + ", objPath="
					+ this.objPath + ", fileSize=" + this.fileSize + "]";
		}

	}

	static class RecordBuilder {
		String exportPath = null;
		String mimeType = null;
		String fileName = null;
		int fileSize = -1;

		public void processEle(final String name, final String value) {
			if (isFileNameEle(name)) {
				this.fileName = value;
			} else if (isFileSizeEle(name)) {
				if (!value.trim().isEmpty()) {
					this.fileSize = Integer.parseInt(value);
				}
			} else if (isMimeEle(name)) {
				this.mimeType = value;
			} else if (isExpPathEle(name)) {
				this.exportPath = value;
			}
		}

		public RecordDetails build() {
			ObjectName objName = new ObjectName(this.fileName, this.mimeType);
			String relativePath = "RelativePath";
			ObjectPath objPath = new ObjectPath(this.exportPath, relativePath);
			return new RecordDetails(objName, objPath, this.fileSize);
		}

		public static boolean isFileNameEle(final String eleName) {
			return "filename".equals(eleName);
		}

		public static boolean isFileSizeEle(final String eleName) {
			return "filesize".equals(eleName);
		}

		public static boolean isMimeEle(final String eleName) {
			return "mimetype".equals(eleName);
		}

		public static boolean isExpPathEle(final String eleName) {
			return "objectexportpath".equals(eleName);
		}
	}

	static class ObjectName {
		final String fileName;
		final String fileExt;
		final String mimeType;
		final String mimeExt;

		ObjectName(final String fileName, final String mimeType) {
			super();
			this.fileName = fileName;
			this.fileExt = extFromFileName(fileName);
			this.mimeType = mimeType;
			this.mimeExt = MimeExtensionMapper.getExtForMime(mimeType);
		}

		public boolean isToRename() {
			if (this.mimeExt == null || this.mimeExt.isEmpty()) {
				return false;
			}
			return (this.fileExt == null || this.fileExt.isEmpty());
		}

		public String newName() {
			return (this.isToRename()) ? fileName + "." + this.mimeExt
					: this.fileName;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ObjectName [fileName=" + this.fileName + ", fileExt="
					+ this.fileExt + ", mimeType=" + this.mimeType
					+ ", mimeExt=" + this.mimeExt + "]";
		}
	}

	static class ObjectPath {
		final String exportPath;
		final String relativePath;

		ObjectPath(String exportPath, String relativePath) {
			super();
			this.exportPath = exportPath;
			this.relativePath = relativePath;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ObjectPath [exportPath=" + this.exportPath
					+ ", relativePath=" + this.relativePath + "]";
		}
	}

	static class RecordRenamer {
		private final File root;

		RecordRenamer(final File root) {
			this.root = root;
		}

		void checkRecord(final ObjectPath objPath, final ObjectName objName) {
			File originalFile = new File(
					findObjParentDir(this.root, objPath.exportPath),
					objName.fileName);
			File renameTo = new File(originalFile.getParentFile(),
					objName.newName());
			if (!objName.isToRename()) {
				System.out.println(String.format("Not renaming object: %s/%s",
						objPath.exportPath, objName.fileName));
				if (objName.mimeExt == null || objName.mimeExt.isEmpty()) {
					System.out.println("No MIME type extension suggested.");
				}
				if (objName.fileExt != null && !objName.fileExt.isEmpty()) {
					System.out.println(String.format(
							"Object file already has extension: %s",
							objName.fileExt));
				}
				if (!originalFile.exists()) {
					System.out.println(String.format("MISSING Export File: %s/%s",
						objPath.exportPath, objName.fileName));
					return;
				}
			}

			if (!objName.fileName.equals(objName.newName())) {
				// File needs renaming
				renameObjectFile(originalFile, renameTo);
			}
		}
	}
}
