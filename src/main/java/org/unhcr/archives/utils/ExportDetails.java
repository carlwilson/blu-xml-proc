package org.unhcr.archives.utils;

import java.io.File;
import java.nio.file.Path;
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

	public final Path exportRoot;
	public final Path dataRoot;
	public final Path bluExportXml;

	private ExportDetails(final Path exportRoot, final Path dataRoot,
			final Path bluExportXml) {
		super();
		this.exportRoot = exportRoot;
		this.dataRoot = dataRoot;
		this.bluExportXml = bluExportXml;
	}

	public static final ExportDetails instance(final Path exportRoot) {
		if (exportRoot == null) {
			throw new IllegalArgumentException("Null exportRoot passed."); //$NON-NLS-1$
		}
		if (!exportRoot.toFile().isDirectory()) {
			throw new IllegalArgumentException(MessageFormat.format("ExportRoot must be a directory, {0} isn't.", exportRoot.toString())); //$NON-NLS-1$
		}
		return new ExportDetails(exportRoot, dataRootPath(exportRoot), bluExportXmlPath(exportRoot));
	}

	private static Path dataRootPath(final Path exportRoot) {
		Path dataRoot = null;
		int dirCount = 0;
		for (File child : exportRoot.toFile().listFiles()) {
			if (child.isDirectory()) {
				dataRoot = child.toPath();
				dirCount++;
			}
		}
		if (dirCount > 1) {
			throw new IllegalStateException(MessageFormat.format(
					"More than one sub-directory in export directory {0}", exportRoot.toString())); //$NON-NLS-1$
		}

		return dataRoot;
	}

	private static Path bluExportXmlPath(final Path exportRoot) {
		for (File child : exportRoot.toFile().listFiles()) {
			if (child.isFile()
					&& child.getName().toLowerCase().endsWith(".xml")) { //$NON-NLS-1$
				return child.toPath();
			}
		}
		throw new IllegalStateException(MessageFormat.format(
				"Could not find BluBaker XML file in export directory {0}", //$NON-NLS-1$
				exportRoot.toString()));
	}
}
