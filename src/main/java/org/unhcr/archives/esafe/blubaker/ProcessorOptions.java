package org.unhcr.archives.esafe.blubaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 12 Jul 2018:00:08:51
 */

public final class ProcessorOptions {

	final boolean isAnalyse;
	final boolean isUsage;
	final Path metadataCsv;
	final List<Path> toProcess;

	/**
	 * 
	 */
	// private ProcessorOptions(final boolean isEnhanced, final boolean
	// isToFile, final boolean isUsage, List<File> toProcess) {
	private ProcessorOptions(final boolean isAnalyse, final boolean isUsage,
			final Path metadataCsv, List<Path> toProcess) {
		this.isAnalyse = isAnalyse;
		this.isUsage = isUsage;
		this.metadataCsv = metadataCsv;
		this.toProcess = Collections.unmodifiableList(toProcess);
	}

	final static ProcessorOptions fromArgs(final String[] args)
			throws FileNotFoundException {
		List<Path> toProcess = new ArrayList<>();
		boolean isUsage = false;
		boolean isAnalyse = false;
		boolean inMetadata = false;
		Path metadataCsv = null;
		for (String arg : args) {
			if (inMetadata) {
				metadataCsv = metadataPath(arg);
				inMetadata = false;
			} else if (arg.equals("-h") || arg.equals("--help")) { //$NON-NLS-1$ //$NON-NLS-2$
				isUsage = true;
			} else if (arg.equals("-a") || arg.equals("--analyse")) { //$NON-NLS-1$ //$NON-NLS-2$
				isAnalyse = true;
			} else if (arg.equals("-m") || arg.equals("--metadata")) { //$NON-NLS-1$ //$NON-NLS-2$
				inMetadata = true;
			} else {
					toProcess.add(toProcessPath(arg));
			}
		}
		if (!isUsage) {
			if (toProcess.isEmpty()) {
				throw new IllegalArgumentException(
						"No export directories to process, terminating."); //$NON-NLS-1$
			}
			if (metadataCsv == null) {
				throw new IllegalArgumentException(
						"No supplementary CSV metadata provided, terminating."); //$NON-NLS-1$
			}
		}
		return new ProcessorOptions(isAnalyse, isUsage, metadataCsv, toProcess);
	}
	
	private static final Path metadataPath(final String arg) throws FileNotFoundException {
		File metadataCsv = new File(arg);
		if (!metadataCsv.isFile()) {
			throw new FileNotFoundException(String.format(
					"Could not find metadata CSV file: %s", //$NON-NLS-1$
					metadataCsv.getAbsolutePath()));
		}
		return metadataCsv.toPath();
	}

	private static final Path toProcessPath(final String arg) throws FileNotFoundException {
		File toTest = new File(arg);
		if (!toTest.isDirectory()) {
			String message = (toTest.exists())
					? "%s is a file, only directories can be processed." //$NON-NLS-1$
					: "Could not find directory: %s";  //$NON-NLS-1$
			throw new FileNotFoundException(
					String.format(message, toTest.getAbsolutePath()));
		}
		return toTest.toPath();
	}
}
