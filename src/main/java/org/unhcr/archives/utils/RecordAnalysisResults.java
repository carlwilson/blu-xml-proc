/**
 *
 */
package org.unhcr.archives.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.unhcr.archives.esafe.blubaker.BluXmlProcessor;
import org.unhcr.archives.esafe.blubaker.RecordProcessor;
import org.unhcr.archives.esafe.blubaker.model.BadRecordException;
import org.unhcr.archives.esafe.blubaker.model.File;
import org.unhcr.archives.esafe.blubaker.model.Record;

/**
 * @author cfw
 *
 */
public final class RecordAnalysisResults {
	private int recordCount = 0;
	private int fileCount = 0;
	private int totalSize = 0;
	private int missingExportFileCount = 0;
	private final Path xmlAnalysed;
	private final boolean passed;
	public RecordProcessor cleaned;
	private final List<Record> missingFiles = new ArrayList<>();

	public RecordAnalysisResults(final RecordProcessor recProp, final ExportDetails exportDetails) {
		this.xmlAnalysed = exportDetails.bluExportXml.toAbsolutePath();
		this.passed = analyse(recProp, exportDetails);
	}

	public int getRecordCount() {
		return recordCount;
	}

	public int getFileCount() {
		return fileCount;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public int getMissingExportFileCount() {
		return missingExportFileCount;
	}

	public int getMissingFileCount() {
		return this.missingFiles.size();
	}

	public boolean passedAnalysis() {
		return this.passed;
	}

	public void printResults() {
		System.out.println();
		System.out.println("-------------------------------------"); //$NON-NLS-1$
		System.out.println();
		System.out.println("Finished analysis of export: " + xmlAnalysed); //$NON-NLS-1$
		System.out.println("  - Record Count: " + recordCount); //$NON-NLS-1$
		System.out.println("  - File Count:   " + fileCount); //$NON-NLS-1$
		System.out.println("  - Total Size:   " + totalSize + " bytes."); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println();
		if (this.passed) {
			System.out.println("Analysis PASSED."); //$NON-NLS-1$
		} else {
			System.out.print(BluXmlProcessor.COL_ERR); // $NON-NLS-1$
			System.out.println("Analysis FAILED."); //$NON-NLS-1$
			System.out.println("  - Missing Export Paths: " + missingExportFileCount); //$NON-NLS-1$
			System.out.println("  - Missing Files: " + this.missingFiles.size()); //$NON-NLS-1$
			for (Record record : this.missingFiles) {
				System.out.println("    * Record: " + record.details.toString());
				try {
					System.out.println("      - Path: " + record.getExportRelativePath());
				} catch (BadRecordException excep) {
					printBadRecord(excep, record);
				} //$NON-NLS-1$
			}
			System.out.println();
			System.out.println("-------------------------------------"); //$NON-NLS-1$
			System.out.println();
			System.out.println("Analysis FAILED."); //$NON-NLS-1$
			System.out.println("  - Missing Export Paths: " + missingExportFileCount); //$NON-NLS-1$
			System.out.println("  - Missing Files: " + this.missingFiles.size()); //$NON-NLS-1$
			System.out.print(BluXmlProcessor.COL_DEF); // $NON-NLS-1$
		}
		if (File.UNMAPPED.size() > 0) {
			System.out.println();
			System.out.print(BluXmlProcessor.COL_WRN); // $NON-NLS-1$
			System.out.println("Unmapped character count is: " + File.UNMAPPED.size()); //$NON-NLS-1$
			System.out.println("  - No mappings for:"); //$NON-NLS-1$
			for (Character unmapped : File.UNMAPPED) {
				System.out.println("    - char:" + unmapped.toString() + "|hex:"
						+ Integer.toHexString((int) unmapped.charValue()));
			}
			System.out.print(BluXmlProcessor.COL_DEF); // $NON-NLS-1$
		}
		System.out.println();
		System.out.println("-------------------------------------"); //$NON-NLS-1$
		System.out.println();
	}

	private boolean analyse(RecordProcessor recProp, final ExportDetails exportDetails) {
		this.cleaned = new RecordProcessor(recProp.exportDetails);
		for (Record record : recProp.getRecordMap().values()) {
			try {
				recordCount++;
				if (!record.isDirectory()) {
					fileCount++;
					totalSize += record.file.size;
					Path recPath = record.getExportRelativePath();
					if (fileFound(recPath, exportDetails)) {
						this.cleaned.addRecord(record);
					} else {
						this.missingFiles.add(record);
					}
				} else {
					this.cleaned.addRecord(record);
				}
			} catch (BadRecordException excep) {
				missingExportFileCount++;
			}
		}
		return this.missingExportFileCount == 0 && this.missingFiles.size() == 0;
	}

	private boolean fileFound(final Path recPath, final ExportDetails exportDetails) throws BadRecordException {
		Path fullPath = exportDetails.cleanPath
				? Paths.get(exportDetails.exportRoot.toString(), "objects", recPath.toString())
				: Paths.get(exportDetails.exportRoot.toString(), recPath.toString());
		return fullPath.toFile().isFile();
	}

	private void printBadRecord(final BadRecordException excep, final Record record) {
		System.err.println(BluXmlProcessor.COL_ERR + excep.getLocalizedMessage()); // $NON-NLS-1$
		System.err.println("eSafe ID: " + record.details.id);
		System.err.println("  - Object name: " + record.object.name);//$NON-NLS-1$
		System.err.println("  - File name  : " + record.file.name);//$NON-NLS-1$
		System.out.println(BluXmlProcessor.COL_DEF); // $NON-NLS-1$
	}
}
