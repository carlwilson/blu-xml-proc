package org.unhcr.archives.esafe.blubaker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import javax.print.attribute.PrintRequestAttribute;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.unhcr.archives.esafe.blubaker.model.BadRecordException;
import org.unhcr.archives.esafe.blubaker.model.Record;
import org.unhcr.archives.isadg.IsadG;
import org.unhcr.archives.isadg.UnitOfDescription;
import org.unhcr.archives.utils.BagStructMaker;
import org.unhcr.archives.utils.ExportDetails;
import org.unhcr.archives.utils.ExportFileTreeCreator;
import org.unhcr.archives.utils.RecordAnalysisResults;
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
	private static boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
	public static String COL_ERR = isWin ? "" : (char) 27 + "[31m";
	public static String COL_WRN = isWin ? "" : (char) 27 + "[33m";
	public static String COL_DEF = isWin ? "" : (char) 27 + "[39m";

	/**
	 *
	 */
	public BluXmlProcessor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws BadRecordException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, TransformerFactoryConfigurationError,
			NoSuchAlgorithmException, BadRecordException, InterruptedException {
		try {
			// Get an options instance from the CLI args
			ProcessorOptions opts = parseOptions(args);
			// Now process the export paths
			for (Path toProcess : opts.toProcess) {
				processExport(toProcess, opts);
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
		} catch (IllegalArgumentException | IOException excep) {
			// Something went awry, report the error and terminate
			usage(excep);
		}
		// This really should never happen....
		throw new IllegalArgumentException("Unknown issue parsing CLI options, terminating."); //$NON-NLS-1$
	}

	private static void processExport(final Path toProcess,
			final ProcessorOptions opts)
			throws NoSuchAlgorithmException, IOException, BadRecordException, InterruptedException {
		ExportDetails exportDetails = ExportDetails.instance(toProcess, false);
		// Grab the individual records into a record processor
		RecordProcessor recordProcessor = parseExportXml(exportDetails);
		// Analyse the export and detect validity
		RecordAnalysisResults results = new RecordAnalysisResults(recordProcessor, exportDetails);
		// If this isn't an analysis run AND the export is valid OR the force processing
		// option is enabled
		if (!opts.isAnalyse && (results.passedAnalysis() || opts.isForce)) {
			exportDetails = ExportFileTreeCreator.moveAndCleanTree(ExportDetails.instance(toProcess, true));
			recordProcessor = parseExportXml(exportDetails);
			results = new RecordAnalysisResults(recordProcessor, exportDetails);
			results.printResults();
			createExportSip(exportDetails, results.cleaned);
			printRootRecord(recordProcessor.findRoot());
		} else {
			results.printResults();
			printRootRecord(recordProcessor.findRoot());
		}
	}
	
	private static void printRootRecord(final Record root) {
		System.out.println("Hierarchy parent is:");
		System.out.println("  - id: " + root.details.parentId);
		System.out.println("  - path: " + root.object.path);
	}

	private static RecordProcessor parseExportXml(final ExportDetails exportDetails) {
		try {
			BluBakerXmlHandler handler = new BluBakerXmlHandler(exportDetails);
			return handler.processExports();
		} catch (IOException | SAXException excep) {
			throw new IllegalStateException("Exception raised when processing BluBaker export file: " //$NON-NLS-1$
					+ exportDetails.bluExportXml, excep);
		}
	}

	private static void createExportSip(final ExportDetails exportDetails, final RecordProcessor recordProcessor)
			throws IOException, NoSuchAlgorithmException {
		Path mdDir = getMetadataPath(exportDetails);
		Files.createDirectories(mdDir);
		moveBluExportMd(exportDetails);
		createMetadata(mdDir, recordProcessor);
		// Bag creation is currently disabled.
		// createBag(exportDetails, recordProcessor);
	}

	private static void createMetadata(final Path mdDir, final RecordProcessor recordProcessor) throws IOException {
		// Create the metadata directory
		createAtomCsv(mdDir, recordProcessor);
		createEadXml(mdDir, recordProcessor);
	}

	private static Path getMetadataPath(final ExportDetails exportDetails) {
		return exportDetails.exportRoot.resolve("metadata");
	}

	private static void moveBluExportMd(final ExportDetails exportDetails) throws IOException {
		Path mdDir = getMetadataPath(exportDetails);
		Files.move(exportDetails.bluExportXml, mdDir.resolve(exportDetails.bluExportXml.getFileName()));
	}

	private static void createAtomCsv(final Path mdDir, final RecordProcessor recordProcessor) {
		try {
			AtomIsadMetadataCsv.writeMetadata(mdDir, recordProcessor.getRecordMap().values(), recordProcessor.findRoot().details.id);
		} catch (IOException | BadRecordException excep) {
			excep.printStackTrace();
			throw new IllegalStateException("Exception raised when writing ATOM metadata file.", //$NON-NLS-1$
					excep);
		}
	}

	private static void createEadXml(final Path mdDir, final RecordProcessor recordProcessor)
			throws IOException {
		try {
			UnitOfDescription uod = IsadGTransformer.parseIsadGTree(recordProcessor);
			IsadG.toEadXmlDocument(mdDir, uod);
		} catch (TransformerFactoryConfigurationError | TransformerException | ParserConfigurationException excep) {
			excep.printStackTrace();
			throw new IllegalStateException("Exception raised when writing EAD metadata file.", excep); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("unused")
	private static void createBag(final ExportDetails exportDetails, final RecordProcessor recordProcessor) throws NoSuchAlgorithmException, IOException {
		BagStructMaker bagMaker = BagStructMaker.fromPath(exportDetails.exportRoot, recordProcessor.getSize());
		bagMaker.createBag();
	}

	private static void usage(final Exception excep) {
		usage();
		System.err.println(BluXmlProcessor.COL_ERR + excep.getLocalizedMessage()); // $NON-NLS-1$
		excep.printStackTrace();
		if (excep.getCause() != null) {
			System.err.println(excep.getCause().getLocalizedMessage());
			System.err.println(excep.getCause().getStackTrace());
		}
		System.out.println(BluXmlProcessor.COL_DEF); // $NON-NLS-1$
		System.exit(1);
	}

	private static void usage() {
		System.out.println("Processes a BluBaker eSafe export structure for SIP ingest."); //$NON-NLS-1$
		System.out.println(""); //$NON-NLS-1$
		System.out.println("usage: blu-xml-proc [flags] [DIRECTORY]"); //$NON-NLS-1$
		System.out.println("  -h | --help : Prints this message."); //$NON-NLS-1$
		System.out.println(
				"  -a | --analysis        : Analysis dry run only checks export integrity without processing."); //$NON-NLS-1$
		System.out.println("  -f | --force           : Force processing of export even if analysis fails."); //$NON-NLS-1$
		System.out.println("  [DIRECTORY]            : The root directory of the BluBaker export for processing."); //$NON-NLS-1$
	}
}
