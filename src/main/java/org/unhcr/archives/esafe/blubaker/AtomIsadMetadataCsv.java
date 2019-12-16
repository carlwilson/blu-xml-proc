/**
 *
 */
package org.unhcr.archives.esafe.blubaker;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.unhcr.archives.esafe.blubaker.model.BadRecordException;
import org.unhcr.archives.esafe.blubaker.model.File;
import org.unhcr.archives.esafe.blubaker.model.Record;

import com.opencsv.CSVWriter;

/**
 * Class responsible for writing out a Comma Separated Values (CSV) file of
 * metadata for ATOM ingest. This file contains metadata in ISAD(G) form and
 * hierarchical relationships between ISAD(G) types.
 * 
 * @author cfw
 *
 */
public final class AtomIsadMetadataCsv {
	/**
	 * The CSV file header line fields, the first line of a CSV file can contain a
	 * list of column headings.
	 */
	public static final String[] csvHeader = { "parts", "atom.legacyId", //$NON-NLS-1$ //$NON-NLS-2$
			"atom.parentId", "atom.qubitParentSlug", "isadg.identifier", "isadg.accessionNumber", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"isadg.title", "isadg.levelOfDescription", "isadg.repository", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"isadg.scopeAndContent", "isadg.subjectAccessPoints", //$NON-NLS-1$ //$NON-NLS-2$
			"isadg.eventDates", "isadg.eventTypes", "isadg.eventActors", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"isadg.eventActorHistories" }; //$NON-NLS-1$

	// private final ExportDetails exportDetails;

	/**
	 * Created a new CSV file instance.
	 * 
	 */
	private AtomIsadMetadataCsv() {
		super();
	}

	/**
	 * Writes all the records in the passed collection to the CSV file.
	 *
	 * @param records a Collection of records to write to the CSV file.
	 * @throws IOException
	 */
	public static void writeMetadata(final Path mdDir, final Collection<Record> records, final int rootId)
			throws IOException {
		// Create a new file Writer instance to a metadata.csv file
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(mdDir.resolve("metadata.csv").toFile()), StandardCharsets.UTF_8)); //$NON-NLS-1$
				// Wrap the writer in a CSV writer
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR,
						CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
						CSVWriter.DEFAULT_LINE_END);) {
			// Write out the CSV header natively to the writer, this avoids adding quotes
			// around the header items, ATOM doesn't like them
			int headerCount = 0;
			for (String headerEle : csvHeader) {
				headerCount++;
				writer.write(headerEle);
				if (headerCount < csvHeader.length) {
					// Skip last element separator
					writer.write(",");
				}
			}
			writer.write("\n");

			// Now cycle through records and write them out
			for (Record record : records) {
				writeRecord(csvWriter, record, rootId);
			}
		}
	}

	private static void writeRecord(final CSVWriter csvWriter, final Record record, final int rootId) {
		try {
			// Get the relative export path and set the string value
			Path relPath = Paths.get("objects", File.cleanPathName(record.getExportRelativePath().toString()));
			String relPathString = (relPath == null) ? "" : relPath.toString();
			if (!record.isDirectory()) {
				// Write out a file record
				writeFileRecord(csvWriter, record, relPathString);
			} else {
				// Write out a directory record
				writeDirRecord(csvWriter, record, relPathString, record.details.id == rootId);
			}
		} catch (BadRecordException excep) {
			// Log any bad record exceptions
			System.err.println(BluXmlProcessor.COL_ERR + excep.getLocalizedMessage()); // $NON-NLS-1$
			System.out.println(BluXmlProcessor.COL_DEF); // $NON-NLS-1$
		}
	}

	private static void writeFileRecord(final CSVWriter csvWriter, final Record record, final String relPath) {
		String[] recordMd = new String[] { relPath, Integer.valueOf(record.details.id).toString(),
				Integer.valueOf(record.details.parentId).toString(), "", Integer.valueOf(record.details.id).toString(),
				"ACESSIONNOFILE", record.object.name, // $NON-NLS-1$
				"Item", "UNHCR", record.object.description, "", "", "", "", "", "" };
		csvWriter.writeNext(recordMd);
	}

	private static void writeDirRecord(final CSVWriter csvWriter, final Record record, final String relPath,
			boolean isRoot) {
		String dirPath = Paths.get(relPath, File.cleanPathName(record.object.name)).toString();
		String[] recordMd = new String[] { dirPath, Integer.valueOf(record.details.id).toString(),
				isRoot ? "" : Integer.valueOf(record.details.parentId).toString(),
				isRoot ? Integer.valueOf(record.details.parentId).toString() : "",
				Integer.valueOf(record.details.id).toString(), "ACESSIONNODIR", record.object.name, // $NON-NLS-1$
				"File", "UNHCR", record.object.description, "", "", "", "", "", "" };
		csvWriter.writeNext(recordMd);
	}
}
