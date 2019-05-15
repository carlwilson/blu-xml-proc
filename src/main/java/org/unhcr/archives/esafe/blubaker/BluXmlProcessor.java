package org.unhcr.archives.esafe.blubaker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.unhcr.archives.esafe.blubaker.model.BadRecordException;
import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.isadg.IsadG;
import org.unhcr.archives.isadg.UnitOfDescription;
import org.unhcr.archives.utils.ExportDetails;
import org.unhcr.archives.utils.SupplementalCsvMetadata;
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
			throws IOException, TransformerFactoryConfigurationError {
		try {
			// Get an options instance from the CLI args
			ProcessorOptions opts = parseOptions(args);
			// Parse the CSV metadata file
			SupplementalCsvMetadata metadata = SupplementalCsvMetadata
					.instance(opts.metadataCsv);
			// Now process the export paths
			for (Path toProcess : opts.toProcess) {
				processExport(ExportDetails.instance(toProcess),
						opts.isAnalyse);
			}
		} catch (IllegalArgumentException | IllegalStateException excep) {
			usage(excep);
		}
	}

	private static ProcessorOptions parseOptions(final String[] args) {
		try {
			// Parse the command line options
			ProcessorOptions opts = ProcessorOptions.fromArgs(args);
			// If we need to show the user help, do so and terminate
			if (opts.isUsage) {
				usage();
				System.exit(0);
			}
			// Return the parsed opts
			return opts;
		} catch (IllegalArgumentException | FileNotFoundException excep) {
			// Something went awry, report the error and terminate
			usage(excep);
		}
		// This really should never happen....
		throw new IllegalArgumentException(
				"Unknown issue parsing CLI options, terminating."); //$NON-NLS-1$
	}

	private static void processExport(final ExportDetails exportDetails,
			final boolean isAnalyse) {
		RecordProcessor recordProcessor = parseExportXml(exportDetails);
		analyse(exportDetails, recordProcessor);
		if (!isAnalyse) {
			createExportSip(exportDetails, recordProcessor);
		}
	}

	private static RecordProcessor parseExportXml(
			final ExportDetails exportDetails) {
		try {
			BluBakerXmlHandler handler = new BluBakerXmlHandler(exportDetails);
			return handler.processExports();
		} catch (IOException | SAXException excep) {
			throw new IllegalStateException(
					"Exception raised when processing BluBaker export file: " //$NON-NLS-1$
							+ exportDetails.bluExportXml,
					excep);
		}
	}

	private static boolean analyse(final ExportDetails exportDetails,
			final RecordProcessor recordProcessor) {
		int recordCount = 0;
		int fileCount = 0;
		int totalSize = 0;
		int missingExportFileCount = 0;
		boolean passedAnalysis = true;
		int longestFailedPath = 0;
		int shortestFailedPath = Integer.MAX_VALUE;
		for (Record record : recordProcessor.getRecordMap().values()) {
			try {
				recordCount++;
				if (!record.isDirectory()) {
					fileCount++;
					totalSize+=record.file.size;
					Path relPath = record.getExportRelativePath(exportDetails);
				}
			} catch (BadRecordException excep) {
				passedAnalysis = false;
				missingExportFileCount++;
				int failedPathLength = (record.object.path.length() + record.object.name.length());
				longestFailedPath = (longestFailedPath > failedPathLength) ? longestFailedPath : failedPathLength;
				shortestFailedPath = (shortestFailedPath < failedPathLength) ? shortestFailedPath : failedPathLength;
//				System.err.println(
//						(char) 27 + "[31m" + excep.getLocalizedMessage()); //$NON-NLS-1$
				System.err.println("eSafe ID: " + record.details.id);
				System.err.println("  - Object name: " + record.object.name);//$NON-NLS-1$
				System.err.println("  - File name  : " + record.file.name);//$NON-NLS-1$
				System.err.println("  - Path length: " + failedPathLength); //$NON-NLS-1$
//				System.out.print((char) 27 + "[39m"); //$NON-NLS-1$
			}
		}
		System.out.println();
		System.out.println("-------------------------------------"); //$NON-NLS-1$
		System.out.println();
		System.out.println("Finished analysis of export: " + exportDetails.bluExportXml); //$NON-NLS-1$
		System.out.println("  - Record Count: " + recordCount); //$NON-NLS-1$
		System.out.println("  - File Count:   " + fileCount); //$NON-NLS-1$
		System.out.println("  - Total Size:   " + totalSize + " bytes."); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println();
		if (passedAnalysis) {
			System.out.println("Analysis PASSED."); //$NON-NLS-1$
		} else {
//			System.out.print((char) 27 + "[31m"); //$NON-NLS-1$
			System.out.println("Analysis FAILED."); //$NON-NLS-1$
			System.out.println("  - Missing Export Paths: " + missingExportFileCount); //$NON-NLS-1$
			System.out.println("  - Longest Failed Path:  " + longestFailedPath); //$NON-NLS-1$
			System.out.println("  - Shortest Failed Path: " + shortestFailedPath); //$NON-NLS-1$
//			System.out.print((char) 27 + "[39m"); //$NON-NLS-1$
		}
		System.out.println();
		System.out.println("-------------------------------------"); //$NON-NLS-1$
		System.out.println();
		return passedAnalysis;

	}

	private static void createExportSip(final ExportDetails exportDetails,
			final RecordProcessor recordProcessor) {
		try {
			DublinCoreCsv.writeMetadata(exportDetails,
					recordProcessor.getRecordMap().values());
		} catch (IOException excep) {
			excep.printStackTrace();
			throw new IllegalStateException(
					"I/O Exception raised when writing ATOM metadata file.", //$NON-NLS-1$
					excep);
		}
		UnitOfDescription uod = IsadGTransformer
				.parseIsadGTree(recordProcessor);
		try {
			IsadG.toEadXmlDocument(exportDetails.exportRoot, uod);
		} catch (TransformerFactoryConfigurationError | TransformerException
				| ParserConfigurationException excep) {
			excep.printStackTrace();
			throw new IllegalStateException(
					"Exception raised when writing EAD metadata file.", excep); //$NON-NLS-1$
		}
		// BagStructMaker bagMaker =
		// BagStructMaker.fromPath(toProcess.toPath(),
		// recProc.getSize());
		// bagMaker.createBag();
	}

	private static void usage(final Exception excep) {
		System.err.println((char) 27 + "[31m" + excep.getLocalizedMessage()); //$NON-NLS-1$
		excep.printStackTrace();
		if (excep.getCause() != null) {
			System.err.println(excep.getCause().getLocalizedMessage());
			System.err.println(excep.getCause().getStackTrace());
		}
		System.out.println((char) 27 + "[39m"); //$NON-NLS-1$
		usage();
		System.exit(1);
	}

	private static void usage() {
		System.out.println(
				"Processes a BluBaker eSafe export structure for SIP ingest."); //$NON-NLS-1$
		System.out.println(""); //$NON-NLS-1$
		System.out.println("usage: blu-xml-proc [flags] [DIRECTORY]"); //$NON-NLS-1$
		System.out.println("  -h prints this message."); //$NON-NLS-1$
		System.out.println(
				"  -a analysis dry run that only checks export integrity but doesn' process the export."); //$NON-NLS-1$
		System.out.println("  -m [FILE] use this CSV metadata file"); //$NON-NLS-1$
		System.out.println(
				"  [DIRECTORY] the root directory of the BluBaker export for processing"); //$NON-NLS-1$
	}
}
