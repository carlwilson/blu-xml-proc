package org.unhcr.archives.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.text.MessageFormat;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 *
 *          Created 25 Apr 2019:00:19:59
 */

public final class ExportDetails {
	static final PathMatcher xmlMatcher = FileSystems.getDefault().getPathMatcher("glob:*.xml");
	public final Path exportRoot;
	public final Path dataRoot;
	public final Path bluExportXml;
	public final boolean cleanPath;

	private ExportDetails(final Path exportRoot, final Path dataRoot,
			final Path bluExportXml, final boolean cleanPath) {
		super();
		this.exportRoot = exportRoot;
		this.dataRoot = dataRoot;
		this.bluExportXml = bluExportXml;
		this.cleanPath = cleanPath;
	}

	public static final ExportDetails instance(final Path exportRoot, final boolean cleanPath) throws IOException {
		if (exportRoot == null) {
			throw new IllegalArgumentException("Null exportRoot passed."); //$NON-NLS-1$
		}
		if (!exportRoot.toFile().isDirectory()) {
			throw new IllegalArgumentException(MessageFormat.format("ExportRoot must be a directory, {0} isn't.", exportRoot.toString())); //$NON-NLS-1$
		}
		return new ExportDetails(exportRoot, dataRootPath(exportRoot), bluExportXmlPath(exportRoot), cleanPath);
	}
	
	public static final ExportDetails fromValues(final Path exportRoot, final Path dataRoot, final Path bluExportXml, final boolean cleanPath) {
		return new ExportDetails(exportRoot, dataRoot, bluExportXml, cleanPath);
	}

	private static Path dataRootPath(final Path exportRoot) throws IOException {
		Path dataRoot = null;
		int dirCount = 0;
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(exportRoot)) {
			for (Path child : dirStream) {
				if (Files.isDirectory(child)) {
					dataRoot = child;
					dirCount++;
				}
			}
		}
		if (dirCount > 1) {
			throw new IllegalStateException(MessageFormat.format(
					"More than one sub-directory in export directory {0}", exportRoot.toString())); //$NON-NLS-1$
		}

		return dataRoot;
	}

	public static Path bluExportXmlPath(final Path exportRoot) throws IOException {
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(exportRoot)) {
			for (Path child : dirStream) {
				if (Files.isRegularFile(child) && xmlMatcher.matches(child.getFileName())) {
					return child;
				}
			}
		}
		throw new IllegalStateException(MessageFormat.format(
				"Could not find BluBaker XML file in export directory {0}", //$NON-NLS-1$
				exportRoot.toString()));
	}
}
