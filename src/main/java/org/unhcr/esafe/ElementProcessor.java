/**
 * 
 */
package org.unhcr.esafe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.unhcr.esafe.blubaker.Record;
import org.xml.sax.Attributes;

/**
 * @author cfw
 *
 */
final class ElementProcessor {
	private final static String empty = "";
	private final static String attId = "dataid";
	private final static String attRecNumber = "number";

	private final static String eleRecord = "record";
	private final static String eleOwnerId = "ownerid";
	private final static String eleParentId = "parentid";
	private final static String eleVersionId = "versionid";
	private final static String eleObjectExportPath = "objectexportpath";
	private final static String eleObectPath = "objectpath";
	private final static String eleFileName = "filename";
	private final static String eleObjectName = "objectname";
	private final static String eleSubType = "subtype";
	private final static String eleCreatedDate = "createdate";
	private final static String eleModifyDate = "modifydate";
	private final static String eleObjectDescription = "objectdescription";
	private final static String eleCreatedBy = "createdby";
	private final static String eleOwner = "owner";
	private final static String eleFileSize = "filesize";
	private final static String eleMimeType = "mimetype";
	private final static String eleMaxVersions = "maxversions";
	private final static String eleVersions = "versions";

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("mm/dd/yyyy");

	private Record.Builder recBuilder;
	private int maxRecNum = 0;
	private int recCount = 0;

	static boolean isRecordEle(final String eleName) {
		return eleRecord.equals(eleName);
	}

	public void recordStart(final Attributes atts) {
		this.recBuilder = new Record.Builder();
		this.recBuilder.id(Integer.parseInt(getId(atts)));
		this.maxRecNum = (Integer.parseInt(getRecNumber(atts)) > this.maxRecNum) ? Integer.parseInt(getRecNumber(atts))
				: this.maxRecNum;
		this.recCount++;
	}

	public int getMaxRecNum() {
		return this.maxRecNum;
	}

	public int getRecordCount() {
		return this.recCount;
	}

	public Record buildRecord() {
		return this.recBuilder.build();
	}

	public void processElement(final String eleName, final String eleValue) {
		switch (eleName) {
		case eleOwnerId:
			this.recBuilder.ownerId(Integer.parseInt(eleValue));
			break;

		case eleParentId:
			this.recBuilder.parentId(Integer.parseInt(eleValue));
			break;

		case eleVersionId:
			this.recBuilder.versionId(Integer.parseInt(eleValue));
			break;

		case eleObjectExportPath:
			this.recBuilder.exportPath(eleValue);
			break;

		case eleObectPath:
			this.recBuilder.path(eleValue);
			break;

		case eleFileName:
			this.recBuilder.fileName(eleValue);
			break;

		case eleObjectName:
			this.recBuilder.name(eleValue);
			break;

		case eleSubType:
			this.recBuilder.subType(Integer.parseInt(eleValue));
			break;

		case eleCreatedDate:
			this.recBuilder.created(parseDate(eleValue));
			break;

		case eleModifyDate:
			this.recBuilder.modified(parseDate(eleValue));
			break;

		case eleObjectDescription:
			this.recBuilder.description(eleValue);
			break;

		case eleCreatedBy:
			this.recBuilder.createdBy(eleValue);
			break;

		case eleOwner:
			this.recBuilder.ownerName(eleValue);
			break;

		case eleMimeType:
			this.recBuilder.mimeType(eleValue);
			break;

		case eleFileSize:
			this.recBuilder.size(Integer.parseInt(eleValue));
			break;

		case eleMaxVersions:
			this.recBuilder.maxVersions(Integer.parseInt(eleValue));
			break;

		case eleVersions:
			this.recBuilder.versions(Integer.parseInt(eleValue));
			break;

		default:
			break;
		}
	}

	private static String getId(final Attributes attrs) {
		return getAttValue(attrs, attId);
	}

	private static String getRecNumber(final Attributes attrs) {
		return getAttValue(attrs, attRecNumber);
	}

	private static String getAttValue(final Attributes attrs, final String attName) {
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name
				if (empty.equals(aName))
					aName = attrs.getQName(i);
				if (attName.equals(aName))
					return attrs.getValue(i);
			}
		}
		return empty;
	}

	private static Date parseDate(final String date) {
		try {
			return dateFormatter.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(String.format("Bad date value %s.", date), e);
		}
	}
}
