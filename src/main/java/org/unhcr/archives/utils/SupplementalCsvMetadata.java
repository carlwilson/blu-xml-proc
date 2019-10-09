package org.unhcr.archives.utils;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
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
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd");
	public final Path csvSource;
	public final Map<String, CsvMetadataRecord> metadataMap;

	/**
	 * 
	 */
	private SupplementalCsvMetadata(final Path csvSource,
			final Map<String, CsvMetadataRecord> metadataMap) {
		super();
		this.csvSource = csvSource.toAbsolutePath();
		this.metadataMap = Collections.unmodifiableMap(metadataMap);
	}

	public CsvMetadataRecord recordByName(final String name) {
		return this.metadataMap.get(name);
	}
	
	public int recordCount() {
		return this.metadataMap.size();
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
				Integer esafeId = Integer.valueOf(record[0]);
				String objectName = record[1];
				Integer parentId = (record[2].isEmpty()) ? null
						: Integer.valueOf(record[2]);
				String structId = record[3];
				String description = record[4];
				String isadLeveDescription = record[5];
				String createdBy = record[6];
				Date dateCreated = parseDate(record[7]);
				Date dateModified = parseDate(record[8]);
				CsvMetadataRecord mdRecord = CsvMetadataRecord.instance(esafeId,
						objectName, parentId, structId, description,
						isadLeveDescription, createdBy, dateCreated,
						dateModified);
				metadataMap.put(mdRecord.objectName, mdRecord);
			}
		}
		return new SupplementalCsvMetadata(csvSource, metadataMap);
	}

	private static Date parseDate(String date) {
		if (date == null || date.isEmpty()) return null;
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			System.err.println("WARNING - Illegal date found: " + date);
			return null;
		}
	}

	public final static class CsvMetadataRecord {
		public final Integer esafeId;
		public final String objectName;
		public final Integer parentId;
		public final String structId;
		public final String description;
		public final String isadLeveDescription;
		public final String createdBy;
		public final Date dateCreated;
		public final Date dateModified;

		private CsvMetadataRecord() {
			this(null, null, null, null, null, null, null, null, null);
		}

		public boolean isRoot() {
			return this.parentId == null;
		}

		private CsvMetadataRecord(final Integer esafeId,
				final String objectName, final Integer parentId,
				final String structId, final String description,
				final String isadLeveDescription, final String createdBy,
				final Date dateCreated, final Date dateModified) {
			super();
			this.esafeId = esafeId;
			this.objectName = objectName;
			this.parentId = parentId;
			this.structId = structId;
			this.description = description;
			this.isadLeveDescription = isadLeveDescription;
			this.createdBy = createdBy;
			this.dateCreated = dateCreated;
			this.dateModified = dateModified;
		}

		public static CsvMetadataRecord instance(final Integer esafeId,
				final String objectName, final Integer parentId,
				final String structId, final String description,
				final String isadLeveDescription, final String createdBy,
				final Date dateCreated, final Date dateModified) {
			checkNull(esafeId, "id"); //$NON-NLS-1$
			checkNull(objectName, "name"); //$NON-NLS-1$
			return new CsvMetadataRecord(esafeId, objectName, parentId,
					structId, description, isadLeveDescription, createdBy,
					dateCreated, dateModified);
		}
	}

	static void checkNull(Object param, String name) {
		if (param == null) {
			throw new IllegalArgumentException(MessageFormat
					.format("Parameter {0} can not be null", name)); //$NON-NLS-1$
		}
	}
}
