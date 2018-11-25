package org.unhcr.esafe;

import java.io.File;
import java.io.IOException;

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
	public static void main(String[] args) throws SAXException, IOException {
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

	private static void processExport(final File toProcess) throws IOException, SAXException  {
		EsafeXmlHandler handler = new EsafeXmlHandler(toProcess.toPath());
		RecordProcessor recProc = handler.processExports();
		recProc.generateManifest();
		recProc.createDublinCore();
	}

	private static void usage() {
		System.out.println("usage: blu-xml-proc [flags] [DIRECTORY]");
		System.out.println("");
		System.out.println(
				"Analyses XML eSafe export file and report details or enhance the XML export.");
		System.out.println("");
		System.out.println("  -h prints this message.");
		System.out.println("  -o output enanced XML to STDOUT.");
		System.out.println(
				"  -f send enanced XML output to .fix file rather than STDOUT.");
	}
}
