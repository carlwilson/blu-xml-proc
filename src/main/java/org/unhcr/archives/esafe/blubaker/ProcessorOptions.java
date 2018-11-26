package org.unhcr.archives.esafe.blubaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 12 Jul 2018:00:08:51
 */

public final class ProcessorOptions {

	final boolean isEnhanced;
	final boolean isToFile;
	final boolean isUsage;
	final List<File> toProcess;
	/**
	 * 
	 */
	private ProcessorOptions(final boolean isEnhanced, final boolean isToFile, final boolean isUsage, List<File> toProcess) {
		this.isEnhanced = isEnhanced;
		this.isToFile = isToFile;
		this.isUsage = isUsage;
		this.toProcess = Collections.unmodifiableList(toProcess);
	}

	final static ProcessorOptions fromArgs(final String[] args) throws FileNotFoundException {
		List<File> toProcess = new ArrayList<>();
		boolean isToFile = false;
		boolean isEnhanced = false;
		boolean isUsage = false;
		for (String arg : args) {
			if (arg.equals("-h")) {
				isUsage = true;
			}
			if (arg.equals("-o")) {
				isEnhanced = true;
				continue;
			} else if (arg.equals("-f")) {
				isEnhanced = isToFile = true;
				continue;
			}
			File toTest = new File(arg);
			if (toTest.isDirectory()) {
				toProcess.add(toTest);
			} else {
				throw new FileNotFoundException(String.format("Could not find directory: %s",
						toTest.getAbsolutePath()));
			}
		}
		return new ProcessorOptions(isEnhanced, isToFile, isUsage, toProcess);
	}
}
