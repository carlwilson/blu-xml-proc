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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.unhcr.archives.esafe.blubaker.model.BadRecordException;
import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.utils.ExportDetails;

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

	private final CSVWriter csvWriter;
	private final ExportDetails exportDetails;

	/**
	 * Created a new CSV file instance.
	 * 
	 * @param exportDetails The details of the BluBaker export to be processed,
	 *                      really used as a base to write files.
	 * @throws IOException When there's a problem writing to the CSV file.
	 */
	public AtomIsadMetadataCsv(final ExportDetails exportDetails) throws IOException {
		this.exportDetails = exportDetails;
		this.csvWriter = setupWriter(exportDetails);
	}

	private static CSVWriter setupWriter(final ExportDetails exportDetails) throws IOException {
		// Create metadata folder in the export file tree.
		Path mdDir = exportDetails.exportRoot.resolve("metadata"); //$NON-NLS-1$
		Files.createDirectories(mdDir);

		// Create a new file Writer instance to a metadata.csv file
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(Paths.get(mdDir.toString(), "metadata.csv").toFile()), StandardCharsets.UTF_8)); //$NON-NLS-1$
				// Wrap the writer in a CSV writer
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR,
						CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
						CSVWriter.DEFAULT_LINE_END);) {
//			writer.write('\ufeff');
			// Write out the CSV header natively to the writer, this avoids adding quotes
			// around the header items, ATOM doesn't like them
			int headerCount = 0;
			for (String headerEle : csvHeader) {
				headerCount++;
				writer.write(headerEle);
				if (headerCount < csvHeader.length) {
					writer.write(",");
				}
			}
			writer.write("\n");
			// Return the CSV Writer to caller
			return csvWriter;
		}
	}

	/**
	 * Writes all the records in the passed collection to the CSV file.
	 * 
	 * @param records a Collection of records to write to the CSV file.
	 */
	public void writeMetadata(final Collection<Record> records) {
		for (Record record : records) {
			this.writeRecord(record);
		}
	}

	/**
	 * Write an individual record to the CSV file
	 * @param record
	 */
	public void writeRecord(final Record record) {
		try {
			Path relPath = record.getExportRelativePath(exportDetails);
			String relPathString = (relPath == null) ? "" : relPath.toString();
			if (!record.isDirectory()) {
				this.writeFileRecord(record, relPathString);
			} else {
				this.writeDirRecord(record, relPathString);
			}
		} catch (BadRecordException excep) {
			System.err.println(BluXmlProcessor.COL_ERR + excep.getLocalizedMessage()); //$NON-NLS-1$
			System.out.println(BluXmlProcessor.COL_DEF); //$NON-NLS-1$
		}
	}

	private void writeFileRecord(final Record record, final String relPath) {
		String[] recordMd = new String[] { relPath, Integer.valueOf(record.details.id).toString(),
				Integer.valueOf(record.details.parentId).toString(), "", Integer.valueOf(record.details.id).toString(),
				"ACESSIONNO", record.object.name, // $NON-NLS-1$
				"Item", "UNHCR", record.object.description, "", "", "", "" };
		csvWriter.writeNext(recordMd);
	}

	private void writeDirRecord(final Record record, final String relPath) {
		String[] recordMd = new String[] { relPath, Integer.valueOf(record.details.id).toString(),
				Integer.valueOf(record.details.parentId).toString(), "", Integer.valueOf(record.details.id).toString(),
				"ACESSIONNO", record.object.name, // $NON-NLS-1$
				"File", "UNHCR", record.object.description, "", "", "", "" };
		csvWriter.writeNext(recordMd);
	}
}
