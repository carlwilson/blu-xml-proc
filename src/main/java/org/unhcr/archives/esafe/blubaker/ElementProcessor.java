/**
 *
 */
package org.unhcr.archives.esafe.blubaker;

import java.text.ParseException;
import java.util.Date;

import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.utils.Formatters;
import org.xml.sax.Attributes;

/**
 * @author cfw
 *
 */
public final class ElementProcessor {
	private final static String empty = ""; //$NON-NLS-1$
	private final static String attId = "dataid"; //$NON-NLS-1$
	private final static String attRecNumber = "number"; //$NON-NLS-1$

	private final static String eleRecord = "record"; //$NON-NLS-1$
	private final static String eleOwnerId = "ownerid"; //$NON-NLS-1$
	private final static String eleParentId = "parentid"; //$NON-NLS-1$
	private final static String eleVersionId = "versionid"; //$NON-NLS-1$
	private final static String eleObjectExportPath = "objectexportpath"; //$NON-NLS-1$
	private final static String eleObectPath = "objectpath"; //$NON-NLS-1$
	private final static String eleFileName = "filename"; //$NON-NLS-1$
	private final static String eleObjectName = "objectname"; //$NON-NLS-1$
	private final static String eleSubType = "subtype"; //$NON-NLS-1$
	private final static String eleCreatedDate = "createdate"; //$NON-NLS-1$
	private final static String eleModifyDate = "modifydate"; //$NON-NLS-1$
	private final static String eleObjectDescription = "objectdescription"; //$NON-NLS-1$
	private final static String eleCreatedBy = "createdby"; //$NON-NLS-1$
	private final static String eleOwner = "owner"; //$NON-NLS-1$
	private final static String eleFileSize = "filesize"; //$NON-NLS-1$
	private final static String eleMimeType = "mimetype"; //$NON-NLS-1$
	private final static String eleMaxVersions = "maxversions"; //$NON-NLS-1$
	private final static String eleVersions = "versions"; //$NON-NLS-1$

	private Record.Builder recBuilder;
	private int maxRecNum = 0;
	private int recCount = 0;
	
	private final boolean cleanPaths;

	static boolean isRecordEle(final String eleName) {
		return eleRecord.equals(eleName);
	}
	
	public ElementProcessor(final boolean cleanPaths) {
		this.cleanPaths = cleanPaths;
	}

	public void recordStart(final Attributes atts) {
		this.recBuilder = new Record.Builder(this.cleanPaths);
		this.recBuilder.id(Integer.parseInt(getId(atts)));
		this.maxRecNum = (Integer.parseInt(getRecNumber(atts)) > this.maxRecNum)
				? Integer.parseInt(getRecNumber(atts))
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
			this.recBuilder.exportPath(Record.cleanExportPath(eleValue));
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

	private static String getAttValue(final Attributes attrs,
			final String attName) {
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
			return Formatters.bluBakerDateFormatter.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					String.format("Bad date value %s.", date), e); //$NON-NLS-1$
		}
	}
}
