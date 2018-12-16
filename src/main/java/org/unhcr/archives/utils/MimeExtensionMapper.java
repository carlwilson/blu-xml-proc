package org.unhcr.archives.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 22 Jun 2018:00:32:54
 */

public enum MimeExtensionMapper {
	INSTANCE;
	private static final String mappingResourcePath = "org/unhcr/esafe/mime.types"; //$NON-NLS-1$
	private static Map<String, String> mimeExtMap;

	private MimeExtensionMapper() {
		try (InputStream rawMappings = MimeExtensionMapper.class
				.getClassLoader().getResourceAsStream(mappingResourcePath);) {
			processMappings(rawMappings);
		} catch (IOException excep) {
			excep.printStackTrace();
			throw new IllegalStateException(
					"Problem reading internal mapping file", excep); //$NON-NLS-1$
		}
	}

	public static String getExtForMime(final String mime) {
		return mimeExtMap.get(mime);
	}

	private static void processMappings(final InputStream rawMappings)
			throws IOException {
		InputStreamReader reader = new InputStreamReader(rawMappings);
		if (mimeExtMap == null) {
			mimeExtMap = new HashMap<>();
		}
		try (BufferedReader buffReader = new BufferedReader(reader)) {
			String line = null;
			while ((line = buffReader.readLine()) != null) {
				processMapping(line);
			}
		}
	}

	private static void processMapping(final String line) {
		if (line.trim().startsWith("#") || line.trim().isEmpty()) { //$NON-NLS-1$
			// Ignore comments and empty lines
			return;
		}
		String[] parts = line.trim().split("\\s+"); //$NON-NLS-1$
		if (mimeExtMap == null)
			System.out.println("NULL MAP"); //$NON-NLS-1$
		if (parts.length >= 2) {
			mimeExtMap.put(parts[0], parts[1]);
		}
	}
}
