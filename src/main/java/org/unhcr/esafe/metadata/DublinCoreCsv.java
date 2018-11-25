/**
 * 
 */
package org.unhcr.esafe.metadata;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.unhcr.esafe.ElementProcessor;
import org.unhcr.esafe.RecordProcessor;
import org.unhcr.esafe.blubaker.Record;

import com.opencsv.CSVWriter;

/**
 * @author cfw
 *
 */
public final class DublinCoreCsv {
	public static final String[] csvHeader = { "filename", "dc.title", "dc.description", "dc.date", "dc.format" };

	public static void writeMetadata(final Path dataRoot, final List<Record> records) throws IOException {
		File mdDir = new File(dataRoot.getParent().toString(), "metadata");
		mdDir.mkdir();
		try (Writer writer = Files.newBufferedWriter(Paths.get(mdDir.toString(), "metadata.csv"));
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);) {
			csvWriter.writeNext(csvHeader);
			for (Record record : records) {
				if (record.isFile()) {
					String relPath = RecordProcessor.getRelPath(dataRoot.getParent(),
							RecordProcessor.findExportedFile(dataRoot, record));
					String[] recordMd = new String[] { relPath, record.object.name, record.object.description,
							ElementProcessor.formatDate(record.auditInfo.created), record.file.mimeType };
					csvWriter.writeNext(recordMd);
				}
			}
		}
	}
}
