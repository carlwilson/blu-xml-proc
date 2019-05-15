/**
 * 
 */
package org.unhcr.archives.esafe.blubaker;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.unhcr.archives.esafe.blubaker.model.BadRecordException;
import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.utils.ExportDetails;
import org.unhcr.archives.utils.Formatters;

import com.opencsv.CSVWriter;

/**
 * @author cfw
 *
 */
public final class DublinCoreCsv {
	public static final String[] csvHeader = { "filename", "dc.identifier", //$NON-NLS-1$ //$NON-NLS-2$
			"dc.title", "dc.description", "dc.date", "dc.format" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	public static void writeMetadata(final ExportDetails exportDetails,
			final Collection<Record> records) throws IOException {
		File mdDir = new File(exportDetails.exportRoot.toString(), "metadata"); //$NON-NLS-1$
		mdDir.mkdir();
		try (Writer writer = Files
				.newBufferedWriter(Paths.get(mdDir.toString(), "metadata.csv")); //$NON-NLS-1$
				CSVWriter csvWriter = new CSVWriter(writer,
						CSVWriter.DEFAULT_SEPARATOR,
						CSVWriter.DEFAULT_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER,
						CSVWriter.DEFAULT_LINE_END);) {
			csvWriter.writeNext(csvHeader);
			for (Record record : records) {
				if (!record.isDirectory()) {
					Path relPath = record.getExportRelativePath(exportDetails);
					String[] recordMd = new String[] { relPath.toString(),
							"" + record.details.id, record.object.name, //$NON-NLS-1$
							record.object.description,
							Formatters.formatDcDate(record.auditInfo.created),
							record.file.mimeType };
					csvWriter.writeNext(recordMd);
				}
			}
		} catch (BadRecordException excep) {
			System.err.println((char) 27 + "[31m" + excep.getLocalizedMessage()); //$NON-NLS-1$
			System.out.println((char) 27 + "[39m"); //$NON-NLS-1$
		}
	}
}
