package org.unhcr.archives.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import gov.loc.repository.bagit.creator.BagCreator;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.domain.Manifest;
import gov.loc.repository.bagit.domain.Metadata;
import gov.loc.repository.bagit.domain.Version;
import gov.loc.repository.bagit.exceptions.FileNotInPayloadDirectoryException;
import gov.loc.repository.bagit.exceptions.InvalidBagitFileFormatException;
import gov.loc.repository.bagit.exceptions.MaliciousPathException;
import gov.loc.repository.bagit.exceptions.MissingBagitFileException;
import gov.loc.repository.bagit.exceptions.MissingPayloadDirectoryException;
import gov.loc.repository.bagit.exceptions.MissingPayloadManifestException;
import gov.loc.repository.bagit.exceptions.UnsupportedAlgorithmException;
import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
import gov.loc.repository.bagit.verify.BagVerifier;
import gov.loc.repository.bagit.writer.BagitFileWriter;
import gov.loc.repository.bagit.writer.ManifestWriter;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 26 Nov 2018:01:04:26
 */

public final class BagStructMaker {
	private static final StandardSupportedAlgorithms algorithm = StandardSupportedAlgorithms.SHA256;

	private final Path bagRoot;
	private final int sizeInBytes;

	/**
	 * 
	 */
	private BagStructMaker(final Path bagRoot, final int sizeInBytes) {
		super();
		this.bagRoot = bagRoot;
		this.sizeInBytes = sizeInBytes;
	}

	public Bag createBag() throws NoSuchAlgorithmException, IOException {
		Bag bag = BagCreator.bagInPlace(this.bagRoot, Arrays.asList(algorithm),
				false, defaultMd(sizeInBytes));
		Version version = new Version(0, 97);
		BagitFileWriter.writeBagitFile(version, Charset.forName("UTF-8"),
				this.bagRoot);
		try {
			verfifyBag(bag);
		} catch (InterruptedException | MaliciousPathException
				| UnsupportedAlgorithmException excep) {
			excep.printStackTrace();
			System.exit(-1);
		}
		return bag;
	}

	private static boolean verfifyBag(final Bag toVerify)
			throws IOException, InterruptedException, MaliciousPathException,
			UnsupportedAlgorithmException {
		try (BagVerifier verifier = new BagVerifier();) {
			verifier.isComplete(toVerify, false);
			return true;
		} catch (MissingPayloadManifestException | MissingBagitFileException
				| MissingPayloadDirectoryException
				| FileNotInPayloadDirectoryException
				| InvalidBagitFileFormatException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		}
		return false;
	}

	public static BagStructMaker fromPath(final Path bagRoot,
			final int sizeInBytes) throws FileNotFoundException {
		if (!Files.isDirectory(bagRoot))
			throw new FileNotFoundException(
					String.format("Path %s must be an existing directory",
							bagRoot.toString()));
		return new BagStructMaker(bagRoot, sizeInBytes);
	}

	static Metadata defaultMd(final int sizeInBytes) {
		Metadata md = new Metadata();
		md.add("Bag-Group-Identifier", "esafe_blubaker");
		md.add("Bag-Size",
				Formatters.humanReadableByteCount(sizeInBytes, false));
		return md;
	}
}
