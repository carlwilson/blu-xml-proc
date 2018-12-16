package org.unhcr.archives.esafe.blubaker;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.isadg.ArchivalHistory;
import org.unhcr.archives.isadg.AuditInfo;
import org.unhcr.archives.isadg.ExtentAndMedium;
import org.unhcr.archives.isadg.Identifiers;
import org.unhcr.archives.isadg.RecordDetails;
import org.unhcr.archives.isadg.UnitOfDescription;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 6 Dec 2018:01:09:42
 */

public final class IsadGTransformer {

	private IsadGTransformer() {
		throw new AssertionError("Should not happen"); //$NON-NLS-1$
	}

	public static UnitOfDescription parseIsadGTree(
			final RecordProcessor records) {
		UnitOfDescription root = findRoot(records.records);
		return populateChildren(root, records.records);
	}

	static UnitOfDescription findRoot(final Map<Integer, Record> records) {
		boolean rootFound = false;
		UnitOfDescription root = null;
		for (Record record : records.values()) {
			if (!records
					.containsKey(Integer.valueOf(record.details.parentId))) {
				if (rootFound) {
					throw new IllegalArgumentException("Multiple roots found"); //$NON-NLS-1$
				}
				rootFound = true;
				root = unitOfDescriptionFromRecord(record);
			}
		}
		return root;
	}

	static UnitOfDescription populateChildren(
			final UnitOfDescription parentToPopulate,
			final Map<Integer, Record> records) {
		Map<Integer, Set<Record>> parentRecords = makeParentMap(records);
		return isadChildProcessor(parentToPopulate, parentRecords);
	}

	static UnitOfDescription isadChildProcessor(final UnitOfDescription parent,
			final Map<Integer, Set<Record>> records) {
		Integer parentId = Integer.valueOf(parent.identifiers.referenceCode);
		Set<UnitOfDescription> children = new HashSet<>();
		Set<Record> recordsSet = records.get(parentId);
		if (recordsSet == null)
			return parent;
		for (Record record : recordsSet) {
			UnitOfDescription child = unitOfDescriptionFromRecord(record,
					record.isFile() ? "item" : "file", parent); //$NON-NLS-1$ //$NON-NLS-2$
			if (!record.isFile()) {
				child = isadChildProcessor(child, records);
			}
			children.add(child);
		}
		parent.setChildren(children);
		return parent;
	}

	static Map<Integer, Set<Record>> makeParentMap(
			final Map<Integer, Record> records) {
		Map<Integer, Set<Record>> parentRecords = new HashMap<>();
		for (Record record : records.values()) {
			Integer parentId = Integer.valueOf(record.details.parentId);
			if (!parentRecords.containsKey(parentId)) {
				Set<Record> children = new HashSet<>();
				children.add(record);
				parentRecords.put(parentId, children);
			} else {
				parentRecords.get(parentId).add(record);
			}
		}
		return parentRecords;
	}

	public static final UnitOfDescription unitOfDescriptionFromRecord(
			final Record record) {
		return unitOfDescriptionFromRecord(record, "series"); //$NON-NLS-1$
	}

	public static final UnitOfDescription unitOfDescriptionFromRecord(
			final Record record, final String levelOfDescription) {
		return unitOfDescriptionFromRecord(record, levelOfDescription, null);
	}

	public static final UnitOfDescription unitOfDescriptionFromRecord(
			final Record record, final String levelOfDescription,
			final UnitOfDescription parent) {
		return new UnitOfDescription(parent, idsFromRecord(record),
				auditInfoFromRecord(record), extentFromRecord(record),
				detailsFromRecord(record, levelOfDescription),
				historyFromRecord(record));
	}

	static ArchivalHistory historyFromRecord(final Record record) {
		return new ArchivalHistory(new Date(), record.object.path);
	}

	static AuditInfo auditInfoFromRecord(final Record record) {
		return new AuditInfo(record.auditInfo.createdBy,
				record.auditInfo.created, record.auditInfo.modified);
	}

	static ExtentAndMedium extentFromRecord(final Record record) {
		return new ExtentAndMedium(record.file.size, record.auditInfo.versions);
	}

	static Identifiers idsFromRecord(final Record record) {
		return new Identifiers(String.valueOf(record.details.id),
				new HashMap<>());
	}

	static RecordDetails detailsFromRecord(final Record record,
			final String levelOfDescription) {
		return new RecordDetails(record.object.name, record.object.description,
				levelOfDescription);
	}
}
