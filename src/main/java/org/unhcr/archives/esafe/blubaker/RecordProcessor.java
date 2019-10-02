/**
 *
 */
package org.unhcr.archives.esafe.blubaker;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.unhcr.archives.esafe.blubaker.model.BadRecordException;
import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.utils.ExportDetails;

/**
 * @author cfw
 *
 */
public final class RecordProcessor {
	public final ExportDetails exportDetails;
	final Map<Integer, Record> records = new HashMap<>();

	public RecordProcessor(final ExportDetails exportDetails) {
		super();
		this.exportDetails = exportDetails;
	}

	void addRecord(final Record record) {
		this.records.put(Integer.valueOf(record.details.id), record);
	}

	public int getSize() {
		int sizeInBytes = 0;
		for (Record record : this.records.values()) {
			if (record.isFile()) {
				sizeInBytes += record.file.size;
			}
		}
		return sizeInBytes;
	}

	public Map<Integer, Record> getRecordMap() {
		return Collections.unmodifiableMap(this.records);
	}

	public Record findRoot() throws BadRecordException {
		return findRoot(this.records);
	}

	private static Record findRoot(final Map<Integer, Record> records) throws BadRecordException {
		int rootCount = 0;
		Record root = null;
		for (Record record : records.values()) {
			if (records.containsKey(record.details.parentId)) {
				continue;
			}
			rootCount++;
			root = record;
		}
		if (rootCount != 1) {
			throw new BadRecordException(MessageFormat
					.format("Record set contains {0,number,integer} root records, must be only one.", rootCount));
		}
		return root;
	}
}
