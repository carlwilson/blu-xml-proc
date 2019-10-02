/**
 *
 */
package org.unhcr.archives.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author cfw
 *
 */
public final class ExportFileTreeCreator {

	private ExportFileTreeCreator() {
		// Not to be instantiated
	}

	public static ExportDetails moveAndCleanTree(final ExportDetails exportDetails) throws IOException {
		// Create objects directory
		Path objectsDir = exportDetails.exportRoot.resolve("objects"); //$NON-NLS-1$
		Files.createDirectories(objectsDir);
		moveFiles(exportDetails.dataRoot, objectsDir);
		return ExportDetails.fromValues(exportDetails.exportRoot, objectsDir, exportDetails.bluExportXml);
	}

	private static void moveFiles(final Path sourceDir, final Path destDir) throws IOException {
		// Protect against badly formed directories
		String destName = org.unhcr.archives.esafe.blubaker.model.File.cleanPathName(sourceDir.getFileName());
		Path dest = destDir.resolve(destName);
		Files.createDirectories(dest);
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(sourceDir)) {
			for (Path source : dirStream) {
				destName = org.unhcr.archives.esafe.blubaker.model.File.cleanPathName(source.getFileName());
				if (Files.isRegularFile(source)) {
					Path destFile = dest.resolve(destName);
					if (Files.exists(destFile)) {
						throw new IOException("Destination file " + destFile.toString() + " already exists");
					}
					Files.move(source, destFile);
				} else {
					moveFiles(source, dest);
				}
			}
		}
	}
}
