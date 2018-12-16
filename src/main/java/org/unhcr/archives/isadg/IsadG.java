package org.unhcr.archives.isadg;

import java.io.File;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.unhcr.archives.utils.Formatters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 */

public final class IsadG {
	static DocumentBuilderFactory icFactory = DocumentBuilderFactory
			.newInstance();
	static final String eadNS = "urn:isbn:1-931666-22-9";

	/**
	 * 
	 */
	private IsadG() {
		// TODO Auto-generated constructor stub
	}

	public static Document toEadXmlDocument(final Path projRoot, final UnitOfDescription uod)
			throws TransformerFactoryConfigurationError, TransformerException,
			ParserConfigurationException {
		DocumentBuilder icBuilder = icFactory.newDocumentBuilder();
		Document doc = icBuilder.newDocument();

		Element root = doc.createElementNS(eadNS, "ead:ead");
		root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", // namespace
				"xsi:schemaLocation", // node name including prefix
				eadNS + "  http://www.loc.gov/ead/ead.xsd" // value
		);
		root.appendChild(createHeader(doc, uod));
		root.appendChild(createArchDesc(doc, uod));
		doc.appendChild(root);
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		File mdDir = new File(projRoot.toString(), "metadata");
		if (!mdDir.exists()) {
			mdDir.mkdir();
		}
		StreamResult streamResult = new StreamResult(
				new File(mdDir, "series-ead.xml"));
		transformer.transform(source, streamResult);
		return doc;
	}

	static Element createHeader(final Document doc,
			final UnitOfDescription uod) {
		Element header = createHeaderEle(doc);
		header.appendChild(createEadIdEle(doc, uod));
		header.appendChild(createFileDescEle(doc, uod));
		return header;
	}

	static Element createHeaderEle(final Document doc) {
		Element header = doc.createElementNS(eadNS, "ead:eadheader");
		header.setAttribute("langencoding", "iso639-2b");
		header.setAttribute("countryencoding", "iso3166-1");
		header.setAttribute("dateencoding", "iso8601");
		header.setAttribute("repositoryencoding", "iso15511");
		header.setAttribute("scriptencoding", "iso15924");
		header.setAttribute("relatedencoding", "DC");
		return header;
	}

	static Element createEadIdEle(final Document doc,
			final UnitOfDescription uod) {
		Element eadid = doc.createElementNS(eadNS, "ead:eadid");
		eadid.setAttribute("identifier", uod.details.title);
		eadid.setAttribute("encodinganalog", "identifier");
		return eadid;
	}

	static Element createFileDescEle(final Document doc,
			final UnitOfDescription uod) {
		Element titleproper = doc.createElementNS(eadNS, "ead:titleproper");
		titleproper.setTextContent(uod.details.title);
		Element titlestmt = doc.createElementNS(eadNS, "ead:titlestmt");
		titlestmt.appendChild(titleproper);
		Element filedesc = doc.createElementNS(eadNS, "ead:filedesc");
		filedesc.appendChild(titlestmt);
		return filedesc;
	}

	static Element createArchDesc(final Document doc,
			final UnitOfDescription uod) {
		Element archdesc = createArchDescEle(doc, uod);
		archdesc.appendChild(createDid(doc, uod));
		addChildren(doc, archdesc, uod);
		return archdesc;
	}
	
	static void addChildren(final Document doc, final Element parent, final UnitOfDescription uod) {
		if (uod.children.isEmpty()) return;
		for (UnitOfDescription child : uod.children) {
			Element c = doc.createElementNS(eadNS, "ead:c");
			c.setAttribute("level", child.details.levelOfDescription);
			c.appendChild(createDid(doc, child));
			addChildren(doc, c, child);
			Element dsc = doc.createElementNS(eadNS, "ead:dsc");
			dsc.setAttribute("type", "combined");
			dsc.appendChild(c);
			parent.appendChild(dsc);
		}
	}

	static Element createArchDescEle(final Document doc,
			final UnitOfDescription uod) {
		Element archdesc = doc.createElementNS(eadNS, "ead:archdesc");
		archdesc.setAttribute("level", uod.details.levelOfDescription);
		archdesc.setAttribute("relatedencoding", "ISAD(G)v2");
		return archdesc;
	}

	static Element createDid(final Document doc, final UnitOfDescription uod) {
		Element did = doc.createElementNS(eadNS, "ead:did");
		did.appendChild(createUnitId(doc, uod));
		did.appendChild(createTitle(doc, uod));
		did.appendChild(createDate(doc, uod));
		did.appendChild(createPhysDesc(doc, uod));
		did.appendChild(createOrig(doc));
		return did;
	}

	static Element createUnitId(final Document doc,
			final UnitOfDescription uod) {
		Element unitid = doc.createElementNS(eadNS, "ead:unitid");
		unitid.setAttribute("encodinganalog", "3.1.1");
		unitid.setTextContent(uod.identifiers.referenceCode);
		return unitid;
	}

	static Element createAltUnitId(final Document doc, final String label,
			final String value) {
		Element unitid = doc.createElementNS(eadNS, "ead:unitid");
		unitid.setAttribute("type", "alternative");
		unitid.setAttribute("label", label);
		unitid.setTextContent(value);
		return unitid;
	}

	static Element createTitle(final Document doc,
			final UnitOfDescription uod) {
		Element unittitle = doc.createElementNS(eadNS, "ead:unittitle");
		unittitle.setAttribute("encodinganalog", "3.1.2");
		unittitle.setTextContent(uod.details.title);
		return unittitle;
	}

	static Element createDate(final Document doc,
			final UnitOfDescription uod) {
		Element unittitle = doc.createElementNS(eadNS, "ead:unitdate");
		unittitle.setAttribute("encodinganalog", "3.1.3");
		unittitle.setAttribute("datechar", "Creation");
		unittitle.setTextContent(Formatters.formatDcDate(uod.auditInfo.created()));
		return unittitle;
	}

	static Element createPhysDesc(final Document doc,
			final UnitOfDescription uod) {
		Element extent = doc.createElementNS(eadNS, "ead:extent");
		extent.setTextContent(String.valueOf(uod.extent.versions));
		Element dimensions = doc.createElementNS(eadNS, "ead:dimensions");
		dimensions.setTextContent(String.valueOf(uod.extent.size));
		Element physdesc = doc.createElementNS(eadNS, "ead:physdesc");
		physdesc.setAttribute("encodinganalog", "3.1.5");
		physdesc.appendChild(extent);
		physdesc.appendChild(dimensions);
		return physdesc;
	}

	static Element createOrig(final Document doc) {
		Element origination = doc.createElementNS(eadNS, "ead:origination");
		origination.setAttribute("encodinganalog", "3.2.1");
		Element name = doc.createElementNS(eadNS, "ead:name");
		name.setTextContent("UNHCR");
		origination.appendChild(name);
		return origination;
	}
}
