/**
 * 
 */
package org.unhcr.archives.esafe.blubaker;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

	public static String getRelPath(Path expRoot, Path exportPath) {
		Path expRootNorm = expRoot.normalize().toAbsolutePath();
		Path other = exportPath.normalize().toAbsolutePath();
		String relPath = expRootNorm.relativize(other).toString();
		System.out.println("RelPath: " + relPath + "|"); //$NON-NLS-1$ //$NON-NLS-2$
		if (relPath.startsWith("./")) //$NON-NLS-1$
			relPath = relPath.replaceFirst("./", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return "data/" + relPath; //$NON-NLS-1$
	}

	public static File findExportedFile(final Path dataRoot,
			final Record record) {
		if (record.isDirectory()) return null;
		// Get the object export path path from the file
		Path recExpPath = Paths.get(record.file.exportPath);
		if (recExpPath.toString().isEmpty()) {
			throw new IllegalStateException(MessageFormat.format(
					"File record id: {0, number}, name: {1} has no file export path.", //$NON-NLS-1$
					Integer.valueOf(record.details.id), record.object.name));
		}
		Path exportRoot = dataRoot.getParent();
		// Now look for the root part of the path, usually the "Enterprise"
		// folder
		String expPathStr = recExpPath.toString();
		boolean rootFound = false;
		Path relRecPath = Paths.get("."); //$NON-NLS-1$
		// Split the path into parts
		String[] pathParts = (expPathStr.contains("\\")) //$NON-NLS-1$
				? expPathStr.split("\\\\") //$NON-NLS-1$
				: new String[] { expPathStr };
		for (String pathPart : pathParts) {
			if (pathPart.equals(dataRoot.getFileName().toString())
					|| rootFound) {
				// Once we've found the data root keep concatenating the parts
				// to build a relative path
				rootFound = true;
				relRecPath = Paths.get(relRecPath.toString(),
						pathPart.toString());
			}
		}
		// Make a file from the path
		File expFile = Paths.get(exportRoot.toString(), relRecPath.toString(),
				record.object.name).toAbsolutePath().toFile();
		if (!expFile.exists()) {
			// If that doesn't exist we pull a similar stunt to see if we can
			// un-mangle BluBakers file path
			expFile = Paths
					.get(exportRoot.toString(), relRecPath.toString(),
							record.file.name.replace("/", "_")) //$NON-NLS-1$ //$NON-NLS-2$
					.toAbsolutePath().toFile();
		}
		// Return the file whether it exists or not
		return expFile;
	}

}
