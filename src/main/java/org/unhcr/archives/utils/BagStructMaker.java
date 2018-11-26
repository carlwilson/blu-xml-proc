package org.unhcr.archives.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import gov.loc.repository.bagit.creator.BagCreator;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.domain.Metadata;
import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;

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

	public void createBag() throws NoSuchAlgorithmException, IOException {
		Bag bag = BagCreator.bagInPlace(this.bagRoot, Arrays.asList(algorithm),
				false, defaultMd(sizeInBytes));
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
