package org.unhcr.archives.esafe.blubaker;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.unhcr.archives.utils.BagStructMaker;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 *
 *          Created 22 Jun 2018:00:27:22
 */

public final class BluXmlProcessor {

	/**
	 *
	 */
	public BluXmlProcessor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
			throws SAXException, IOException, NoSuchAlgorithmException {
		ProcessorOptions opts = ProcessorOptions.fromArgs(args);
		if (opts.toProcess.isEmpty()) {
			usage();
			System.exit(1);
		}
		if (opts.isUsage) {
			usage();
			System.exit(0);
		}
		for (File toProcess : opts.toProcess) {
			processExport(toProcess);
		}

	}

	private static void processExport(final File toProcess)
			throws IOException, SAXException, NoSuchAlgorithmException {
		BluBakerXmlHandler handler = new BluBakerXmlHandler(toProcess.toPath());
		RecordProcessor recProc = handler.processExports();
		recProc.createDublinCore();
		BagStructMaker bagMaker = BagStructMaker.fromPath(toProcess.toPath(), recProc.getSize());
		bagMaker.createBag();
	}

	private static void usage() {
		System.out.println("usage: blu-xml-proc [flags] [DIRECTORY]");
		System.out.println("");
		System.out.println(
				"Analyses BluBaker XML eSafe export file and report details or enhance the XML export.");
		System.out.println("");
		System.out.println("  -h prints this message.");
		System.out.println("  -o output enanced XML to STDOUT.");
		System.out.println(
				"  -f send enanced XML output to .fix file rather than STDOUT.");
	}
}
