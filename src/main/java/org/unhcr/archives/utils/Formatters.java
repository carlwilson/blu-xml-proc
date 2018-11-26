package org.unhcr.archives.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 26 Nov 2018:01:37:12
 */

public enum Formatters {
	INSTANCE;

	public static SimpleDateFormat bluBakerDateFormatter = new SimpleDateFormat(
			"mm/dd/yyyy");

	public static SimpleDateFormat dcDateFormatter = new SimpleDateFormat(
			"yyyy-mm-dd");

	public static String formatBluBakerDate(final Date date) {
		return bluBakerDateFormatter.format(date);
	}

	public static String formatDcDate(final Date date) {
		return dcDateFormatter.format(date);
	}

	// See https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB",
				Double.valueOf(bytes / Math.pow(unit, exp)), pre);
	}
}
