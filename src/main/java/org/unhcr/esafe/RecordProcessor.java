/**
 * 
 */
package org.unhcr.esafe;

import java.util.ArrayList;
import java.util.List;

import org.unhcr.esafe.blubaker.Record;

/**
 * @author cfw
 *
 */
final class RecordProcessor {
	final List<Record> records = new ArrayList<>();
	
	void addRecord(final Record record) {
		this.records.add(record);
	}
}
