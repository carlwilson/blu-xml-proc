/**
 * 
 */
package org.unhcr.archives.esafe.blubaker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.unhcr.archives.esafe.blubaker.model.Record;

/**
 * @author cfw
 *
 */
public final class RecordProcessor {
	final Map<Integer, Record> records = new HashMap<>();
	final Path exportRoot;
	final Path dataRoot;

	public RecordProcessor(final Path exportRoot) {
		this.exportRoot = exportRoot.toAbsolutePath();
		this.dataRoot = findDataRoot(this.exportRoot);
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

	public static String getRelPath(Path expRoot, File file) {
		String relPath = expRoot.relativize(file.toPath().toAbsolutePath())
				.toString();
		if (relPath.startsWith("./"))
			relPath = relPath.replaceFirst("./", "");
		return "data/" + relPath;
	}

	public void createDublinCore() throws IOException {
		DublinCoreCsv.writeMetadata(this.dataRoot, this.records.values());
	}

	public static File findExportedFile(final Path dataRoot,
			final Record record) {
		Path exportRoot = dataRoot.getParent();
		// Get the object export path path from the file
		Path recExpPath = Paths.get(record.file.exportPath);
		if (recExpPath.toString().isEmpty()) {
			// If there's no export path hack a substitute from the object path
			// First replace any existing "/" chars with _ to undo BluBaker's
			// mangling
			// Then substitute a real path separator for the colons
			recExpPath = Paths.get(
					record.object.path.replace("/", "_").replace(":", "\\"));
		}
		// Now look for the root part of the path, usually the "Enterprise"
		// folder
		String expPathStr = recExpPath.toString();
		boolean rootFound = false;
		Path relRecPath = Paths.get(".");
		// Split the path into parts
		String[] pathParts = (expPathStr.contains("\\"))
				? expPathStr.split("\\\\")
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
							record.file.name.replace("/", "_"))
					.toAbsolutePath().toFile();
		}
		// Return the file whether it exists or not
		return expFile;
	}

	private static Path findDataRoot(Path exportRoot) {
		Path dataRoot = null;
		int dirCount = 0;
		for (File child : exportRoot.toFile().listFiles()) {
			if (child.isDirectory()) {
				dataRoot = child.toPath();
				dirCount++;
			}
		}
		if (dirCount > 1) {
			throw new IllegalArgumentException(
					"More than one directory in export");
		}

		return dataRoot;
	}
}
