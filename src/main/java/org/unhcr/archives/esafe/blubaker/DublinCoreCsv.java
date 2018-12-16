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

import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.utils.Formatters;

import com.opencsv.CSVWriter;

/**
 * @author cfw
 *
 */
public final class DublinCoreCsv {
	public static final String[] csvHeader = { "filename", "dc.identifier",
			"dc.title", "dc.description", "dc.date", "dc.format" };

	public static void writeMetadata(final Path dataRoot,
			final Collection<Record> records) throws IOException {
		File mdDir = new File(dataRoot.getParent().toString(), "metadata");
		mdDir.mkdir();
		try (Writer writer = Files
				.newBufferedWriter(Paths.get(mdDir.toString(), "metadata.csv"));
				CSVWriter csvWriter = new CSVWriter(writer,
						CSVWriter.DEFAULT_SEPARATOR,
						CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER,
						CSVWriter.DEFAULT_LINE_END);) {
			csvWriter.writeNext(csvHeader);
			for (Record record : records) {
				if (record.isFile()) {
					String relPath = RecordProcessor.getRelPath(
							dataRoot.getParent(),
							RecordProcessor.findExportedFile(dataRoot, record));
					String[] recordMd = new String[] { relPath,
							"" + record.details.id, record.object.name,
							record.object.description,
							Formatters.formatDcDate(record.auditInfo.created),
							record.file.mimeType };
					csvWriter.writeNext(recordMd);
				}
			}
		}
	}
}
