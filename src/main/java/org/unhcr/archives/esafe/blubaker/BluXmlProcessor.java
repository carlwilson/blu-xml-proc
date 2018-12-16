package org.unhcr.archives.esafe.blubaker;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.unhcr.archives.isadg.IsadG;
import org.unhcr.archives.isadg.UnitOfDescription;
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
	public static void main(String[] args) throws SAXException, IOException,
			NoSuchAlgorithmException, TransformerFactoryConfigurationError,
			TransformerException, ParserConfigurationException {
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
			throws IOException, SAXException, NoSuchAlgorithmException,
			TransformerFactoryConfigurationError, TransformerException,
			ParserConfigurationException {
		BluBakerXmlHandler handler = new BluBakerXmlHandler(toProcess.toPath());
		RecordProcessor recProc = handler.processExports();
		recProc.createDublinCore();
		UnitOfDescription uod = IsadGTransformer.parseIsadGTree(recProc);
		IsadG.toEadXmlDocument(toProcess.toPath(), uod);
		BagStructMaker bagMaker = BagStructMaker.fromPath(toProcess.toPath(),
				recProc.getSize());
		bagMaker.createBag();
	}

	private static void usage() {
		System.out.println("usage: blu-xml-proc [flags] [DIRECTORY]"); //$NON-NLS-1$
		System.out.println(""); //$NON-NLS-1$
		System.out.println(
				"Analyses BluBaker XML eSafe export file and report details or enhance the XML export."); //$NON-NLS-1$
		System.out.println(""); //$NON-NLS-1$
		System.out.println("  -h prints this message."); //$NON-NLS-1$
		System.out.println("  -o output enanced XML to STDOUT."); //$NON-NLS-1$
		System.out.println(
				"  -f send enanced XML output to .fix file rather than STDOUT."); //$NON-NLS-1$
	}
}
