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
	static final String eadNS = "urn:isbn:1-931666-22-9"; //$NON-NLS-1$
	static final String eadunitid = "ead:unitid"; //$NON-NLS-1$
	static final String encodinganalog = "encodinganalog"; //$NON-NLS-1$

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

		Element root = doc.createElementNS(eadNS, "ead:ead"); //$NON-NLS-1$
		root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", // namespace //$NON-NLS-1$
				"xsi:schemaLocation", // node name including prefix //$NON-NLS-1$
				eadNS + "  http://www.loc.gov/ead/ead.xsd" // value //$NON-NLS-1$
		);
		root.appendChild(createHeader(doc, uod));
		root.appendChild(createArchDesc(doc, uod));
		doc.appendChild(root);
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		DOMSource source = new DOMSource(doc);
		File mdDir = new File(projRoot.toString(), "metadata"); //$NON-NLS-1$
		if (!mdDir.exists()) {
			mdDir.mkdir();
		}
		StreamResult streamResult = new StreamResult(
				new File(mdDir, "series-ead.xml")); //$NON-NLS-1$
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
		Element header = doc.createElementNS(eadNS, "ead:eadheader"); //$NON-NLS-1$
		header.setAttribute("langencoding", "iso639-2b"); //$NON-NLS-1$ //$NON-NLS-2$
		header.setAttribute("countryencoding", "iso3166-1"); //$NON-NLS-1$ //$NON-NLS-2$
		header.setAttribute("dateencoding", "iso8601"); //$NON-NLS-1$ //$NON-NLS-2$
		header.setAttribute("repositoryencoding", "iso15511"); //$NON-NLS-1$ //$NON-NLS-2$
		header.setAttribute("scriptencoding", "iso15924"); //$NON-NLS-1$ //$NON-NLS-2$
		header.setAttribute("relatedencoding", "DC"); //$NON-NLS-1$ //$NON-NLS-2$
		return header;
	}

	static Element createEadIdEle(final Document doc,
			final UnitOfDescription uod) {
		Element eadid = doc.createElementNS(eadNS, "ead:eadid"); //$NON-NLS-1$
		eadid.setAttribute("identifier", uod.details.title); //$NON-NLS-1$
		eadid.setAttribute(encodinganalog, "identifier"); //$NON-NLS-1$
		return eadid;
	}

	static Element createFileDescEle(final Document doc,
			final UnitOfDescription uod) {
		Element titleproper = doc.createElementNS(eadNS, "ead:titleproper"); //$NON-NLS-1$
		titleproper.setTextContent(uod.details.title);
		Element titlestmt = doc.createElementNS(eadNS, "ead:titlestmt"); //$NON-NLS-1$
		titlestmt.appendChild(titleproper);
		Element filedesc = doc.createElementNS(eadNS, "ead:filedesc"); //$NON-NLS-1$
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
			Element c = doc.createElementNS(eadNS, "ead:c"); //$NON-NLS-1$
			c.setAttribute("level", child.details.levelOfDescription); //$NON-NLS-1$
			c.appendChild(createDid(doc, child));
			addChildren(doc, c, child);
			Element dsc = doc.createElementNS(eadNS, "ead:dsc"); //$NON-NLS-1$
			dsc.setAttribute("type", "combined"); //$NON-NLS-1$ //$NON-NLS-2$
			dsc.appendChild(c);
			parent.appendChild(dsc);
		}
	}

	static Element createArchDescEle(final Document doc,
			final UnitOfDescription uod) {
		Element archdesc = doc.createElementNS(eadNS, "ead:archdesc"); //$NON-NLS-1$
		archdesc.setAttribute("level", uod.details.levelOfDescription); //$NON-NLS-1$
		archdesc.setAttribute("relatedencoding", "ISAD(G)v2"); //$NON-NLS-1$ //$NON-NLS-2$
		return archdesc;
	}

	static Element createDid(final Document doc, final UnitOfDescription uod) {
		Element did = doc.createElementNS(eadNS, "ead:did"); //$NON-NLS-1$
		did.appendChild(createUnitId(doc, uod));
		did.appendChild(createTitle(doc, uod));
		did.appendChild(createDate(doc, uod));
		did.appendChild(createPhysDesc(doc, uod));
		did.appendChild(createOrig(doc));
		return did;
	}

	static Element createUnitId(final Document doc,
			final UnitOfDescription uod) {
		Element unitid = doc.createElementNS(eadNS, eadunitid);
		unitid.setAttribute(encodinganalog, "3.1.1"); //$NON-NLS-1$
		unitid.setTextContent(uod.identifiers.referenceCode);
		return unitid;
	}

	static Element createAltUnitId(final Document doc, final String label,
			final String value) {
		Element unitid = doc.createElementNS(eadNS, eadunitid);
		unitid.setAttribute("type", "alternative"); //$NON-NLS-1$ //$NON-NLS-2$
		unitid.setAttribute("label", label); //$NON-NLS-1$
		unitid.setTextContent(value);
		return unitid;
	}

	static Element createTitle(final Document doc,
			final UnitOfDescription uod) {
		Element unittitle = doc.createElementNS(eadNS, "ead:unittitle"); //$NON-NLS-1$
		unittitle.setAttribute(encodinganalog, "3.1.2"); //$NON-NLS-1$
		unittitle.setTextContent(uod.details.title);
		return unittitle;
	}

	static Element createDate(final Document doc,
			final UnitOfDescription uod) {
		Element unittitle = doc.createElementNS(eadNS, "ead:unitdate"); //$NON-NLS-1$
		unittitle.setAttribute(encodinganalog, "3.1.3"); //$NON-NLS-1$
		unittitle.setAttribute("datechar", "Creation"); //$NON-NLS-1$ //$NON-NLS-2$
		unittitle.setTextContent(Formatters.formatDcDate(uod.auditInfo.created()));
		return unittitle;
	}

	static Element createPhysDesc(final Document doc,
			final UnitOfDescription uod) {
		Element extent = doc.createElementNS(eadNS, "ead:extent"); //$NON-NLS-1$
		extent.setTextContent(String.valueOf(uod.extent.versions));
		Element dimensions = doc.createElementNS(eadNS, "ead:dimensions"); //$NON-NLS-1$
		dimensions.setTextContent(String.valueOf(uod.extent.size));
		Element physdesc = doc.createElementNS(eadNS, "ead:physdesc"); //$NON-NLS-1$
		physdesc.setAttribute(encodinganalog, "3.1.5"); //$NON-NLS-1$
		physdesc.appendChild(extent);
		physdesc.appendChild(dimensions);
		return physdesc;
	}

	static Element createOrig(final Document doc) {
		Element origination = doc.createElementNS(eadNS, "ead:origination"); //$NON-NLS-1$
		origination.setAttribute(encodinganalog, "3.2.1"); //$NON-NLS-1$
		Element name = doc.createElementNS(eadNS, "ead:name"); //$NON-NLS-1$
		name.setTextContent("UNHCR"); //$NON-NLS-1$
		origination.appendChild(name);
		return origination;
	}
}
