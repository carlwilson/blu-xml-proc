/**
 * 
 */
package org.unhcr.archives.esafe.blubaker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.unhcr.archives.esafe.blubaker.model.Record;

/**
 * @author cfw
 *
 */
public final class RecordProcessor {
	final List<Record> records = new ArrayList<>();
	final Path exportRoot;
	final Path dataRoot;

	public RecordProcessor(final Path exportRoot) {
		this.exportRoot = exportRoot.toAbsolutePath();
		this.dataRoot = findDataRoot(this.exportRoot);
	}

	void addRecord(final Record record) {
		this.records.add(record);
	}

	public void generateManifest() throws IOException {
		File manifest = new File(this.exportRoot.toString(), "manifest-sha256.txt");
		try (FileOutputStream fos = new FileOutputStream(manifest);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
			for (Record record : this.records) {
				if (record.isFile()) {
					File exportedFile = findExportedFile(this.dataRoot, record);
					String relPath = getRelPath(this.exportRoot, exportedFile);
					writer.write(RecordProcessor.getShaOfFile(exportedFile) + " " + relPath);
					writer.newLine();
				}
			}
		}
	}

	public static String getRelPath(Path expRoot, File file) {
		String relPath = expRoot.relativize(file.toPath().toAbsolutePath()).toString();
		if (relPath.startsWith("./"))
			relPath = relPath.replaceFirst("./", "");
		return "data/" + relPath;
	}
	public void createDublinCore() throws IOException {
		DublinCoreCsv.writeMetadata(this.dataRoot, this.records);
	}

	private static String getShaOfFile(final File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return DigestUtils.sha256Hex(is);
		}
	}

	public static File findExportedFile(final Path dataRoot, final Record record) {
		Path exportRoot = dataRoot.getParent();
		// Get the object export path path from the file
		Path recExpPath = Paths.get(record.file.exportPath);
		if (recExpPath.toString().isEmpty()) {
			// If there's no export path hack a substitute from the object path
			// First replace any existing "/" chars with _ to undo BluBaker's
			// mangling
			// Then substitute a real path separator for the colons
			recExpPath = Paths.get(record.object.path.replace("/", "_").replace(":", "\\"));
		}
		// Now look for the root part of the path, usually the "Enterprise"
		// folder
		String expPathStr = recExpPath.toString();
		boolean rootFound = false;
		Path relRecPath = Paths.get(".");
		// Split the path into parts
		String[] pathParts = (expPathStr.contains("\\")) ? expPathStr.split("\\\\") : new String[] { expPathStr };
		for (String pathPart : pathParts) {
			if (pathPart.equals(dataRoot.getFileName().toString()) || rootFound) {
				// Once we've found the data root keep concatenating the parts
				// to build a relative path
				rootFound = true;
				relRecPath = Paths.get(relRecPath.toString(), pathPart.toString());
			}
		}
		// Make a file from the path
		File expFile = Paths.get(exportRoot.toString(), relRecPath.toString(), record.object.name).toAbsolutePath()
				.toFile();
		if (!expFile.exists()) {
			// If that doesn't exist we pull a similar stunt to see if we can
			// un-mangle BluBakers file path
			expFile = Paths.get(exportRoot.toString(), relRecPath.toString(), record.file.name.replace("/", "_"))
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
			throw new IllegalArgumentException("More than one directory in export");
		}

		return dataRoot;
	}
}
