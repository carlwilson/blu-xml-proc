package org.unhcr.archives.utils;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 2 Apr 2019:10:26:12
 */

public final class SupplementalCsvMetadata {
	private final Map<String, CsvMetadataRecord> metadataMap;

	/**
	 * 
	 */
	private SupplementalCsvMetadata(
			final Map<String, CsvMetadataRecord> metadataMap) {
		super();
		this.metadataMap = Collections.unmodifiableMap(metadataMap);
	}

	public CsvMetadataRecord recordByName(final String name) {
		return this.metadataMap.get(name);
	}

	public static SupplementalCsvMetadata instance(final Path csvSource)
			throws IOException {
		Map<String, CsvMetadataRecord> metadataMap = new HashMap<>();
		try (Reader reader = Files.newBufferedReader(csvSource);
				CSVReader csvReader = new CSVReader(reader)) {
			String[] record;
			boolean skipLine = true;
			while ((record = csvReader.readNext()) != null) {
				if (skipLine) {
					skipLine = false;
					continue;
				}
				Integer id = Integer.valueOf(record[0]);
				String name = record[1];
				Integer parentId = (record[2].isEmpty()) ? null
						: Integer.valueOf(record[2]);
				CsvMetadataRecord mdRecord = CsvMetadataRecord.instance(id,
						name, parentId);
				metadataMap.put(mdRecord.name, mdRecord);
			}
		}
		return new SupplementalCsvMetadata(metadataMap);
	}

	public final static class CsvMetadataRecord {
		public final Integer id;
		public final String name;
		public final Integer parent;

		private CsvMetadataRecord() {
			this(null, null, null);
		}

		public boolean isRoot() {
			return this.parent == null;
		}

		private CsvMetadataRecord(final Integer id, final String name,
				final Integer parent) {
			super();
			this.id = id;
			this.name = name;
			this.parent = parent;
		}

		public static CsvMetadataRecord instance(final Integer id,
				final String name, final Integer parent) {
			checkNull(id, "id"); //$NON-NLS-1$
			checkNull(name, "name"); //$NON-NLS-1$
			return new CsvMetadataRecord(id, name, parent);
		}
	}

	static void checkNull(Object param, String name) {
		if (param == null) {
			throw new IllegalArgumentException(MessageFormat
					.format("Parameter {0} can not be null", name)); //$NON-NLS-1$
		}
	}
}
