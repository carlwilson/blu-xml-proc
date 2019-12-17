/**
 *
 */
package org.unhcr.archives.esafe.blubaker.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author cfw
 *
 */
public final class File {
	private final static Map<Character, Character> createCharMap() {
		Map<Character, Character> retVal = new HashMap<Character, Character>();
		// -> !
		retVal.put(Character.valueOf((char) 0x00A1), Character.valueOf((char) 0x0021));
		// -> -
		retVal.put(Character.valueOf((char) 0x00ad), Character.valueOf((char) 0x002D));
		// -> -
		retVal.put(Character.valueOf((char) 0x2010), Character.valueOf((char) 0x002D));
		// -> -
		retVal.put(Character.valueOf((char) 0x2011), Character.valueOf((char) 0x002D));
		// -> -
		retVal.put(Character.valueOf((char) 0x2012), Character.valueOf((char) 0x002D));
		// -> -
		retVal.put(Character.valueOf((char) 0x2013), Character.valueOf((char) 0x002D));
		// -> -
		retVal.put(Character.valueOf((char) 0x2014), Character.valueOf((char) 0x002D));
		// -> -
		retVal.put(Character.valueOf((char) 0x2015), Character.valueOf((char) 0x002D));
		// -> |
		retVal.put(Character.valueOf((char) 0x2016), Character.valueOf((char) 0x007C));
		// -> '
		retVal.put(Character.valueOf((char) 0x2018), Character.valueOf((char) 0x0027));
		// -> '
		retVal.put(Character.valueOf((char) 0x2019), Character.valueOf((char) 0x0027));
		// -> '
		retVal.put(Character.valueOf((char) 0x201B), Character.valueOf((char) 0x0027));
		// -> '
		retVal.put(Character.valueOf((char) 0x201C), Character.valueOf((char) 0x0027));
		// -> '
		retVal.put(Character.valueOf((char) 0x201D), Character.valueOf((char) 0x0027));
		// -> '
		retVal.put(Character.valueOf((char) 0x201F), Character.valueOf((char) 0x0027));
		// -> ,
		retVal.put(Character.valueOf((char) 0x201A), Character.valueOf((char) 0x002C));
		// -> ,
		retVal.put(Character.valueOf((char) 0x201E), Character.valueOf((char) 0x002C));
		// -> 2
		retVal.put(Character.valueOf((char) 0x00B2), Character.valueOf((char) 0x0032));
		// -> A
		retVal.put(Character.valueOf((char) 0x00C0), Character.valueOf((char) 0x0041));
		// -> a
		retVal.put(Character.valueOf((char) 0x00E0), Character.valueOf((char) 0x0061));
		// -> c
		retVal.put(Character.valueOf((char) 0x00A9), Character.valueOf((char) 0x0063));
		// -> c
		retVal.put(Character.valueOf((char) 0x00E7), Character.valueOf((char) 0x0063));
		// -> E
		retVal.put(Character.valueOf((char) 0x00C8), Character.valueOf((char) 0x0045));
		// -> E
		retVal.put(Character.valueOf((char) 0x00C9), Character.valueOf((char) 0x0045));
		// -> e
		retVal.put(Character.valueOf((char) 0x00E8), Character.valueOf((char) 0x0065));
		// -> e
		retVal.put(Character.valueOf((char) 0x00E9), Character.valueOf((char) 0x0065));
		// -> e
		retVal.put(Character.valueOf((char) 0x00EA), Character.valueOf((char) 0x0065));
		// -> o
		retVal.put(Character.valueOf((char) 0x00A4), Character.valueOf((char) 0x006F));
		// -> o
		retVal.put(Character.valueOf((char) 0x00B0), Character.valueOf((char) 0x006F));
		// -> o
		retVal.put(Character.valueOf((char) 0x00F4), Character.valueOf((char) 0x006F));
		return Collections.unmodifiableMap(retVal);
	}

	public final static Map<Character, Character> CHARACTER_MAP = createCharMap();
	public final static Set<Character> UNMAPPED = new HashSet<>();
	public final static Character DEFAULT_CHAR = '_';
	public final static Character COMMA_CHAR = ',';
	private final static String spaceRegex = "\\s";
	private final static String trailingSpaceRegex = spaceRegex + "+$";
	private final static String trailingPathSpaceRegex = spaceRegex + "+/";
	public final String exportPath;
	public final String name;
	public final int size;
	public final String mimeType;

	private File(final String path, final String name, final int size, final String mimeType) {
		super();
		this.exportPath = path;
		this.name = name;
		this.size = size;
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "File [exportPath=" + this.exportPath + ", name=" + this.name //$NON-NLS-1$ //$NON-NLS-2$
				+ ", size=" + this.size + ", mimeType=" + this.mimeType + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.exportPath == null) ? 0 : this.exportPath.hashCode());
		result = prime * result + ((this.mimeType == null) ? 0 : this.mimeType.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + this.size;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof File)) {
			return false;
		}
		File other = (File) obj;
		if (this.exportPath == null) {
			if (other.exportPath != null) {
				return false;
			}
		} else if (!this.exportPath.equals(other.exportPath)) {
			return false;
		}
		if (this.mimeType == null) {
			if (other.mimeType != null) {
				return false;
			}
		} else if (!this.mimeType.equals(other.mimeType)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.size != other.size) {
			return false;
		}
		return true;
	}

	public final static String cleanPathName(final Path toClean) {
		return cleanPathName(toClean.toString());
	}

	public final static String removeTrailingSpaces(final String toClean) {
		return toClean.replaceAll(trailingSpaceRegex, "").replaceAll(trailingPathSpaceRegex, "/");
	}

	public final static String cleanPathName(final String toClean) {
		// Remove trailing space from path elements and replace remaining spaces with
		// underscores
		String spaceScrubbed = removeTrailingSpaces(toClean).replaceAll(spaceRegex, DEFAULT_CHAR.toString())
				.replaceAll(COMMA_CHAR.toString(), DEFAULT_CHAR.toString());
		int len = spaceScrubbed.length();
		StringBuilder cleaned = new StringBuilder(len);
		for (int iLoop = 0; iLoop < len; iLoop++) {
			cleaned.append(mapChar(Character.valueOf(spaceScrubbed.charAt(iLoop))));
		}
		return cleaned.toString();
	}

	private final static Character mapChar(final Character toMap) {
		if (toMap < ' ' || toMap >= 0x7F) {
			if (CHARACTER_MAP.containsKey(toMap)) {
				return CHARACTER_MAP.get(toMap);
			}
		} else {
			return toMap;
		}
		UNMAPPED.add(toMap);
		return '_';
	}

	static class Builder {
		private boolean cleanPaths = false;
		private String pth = ""; //$NON-NLS-1$
		private String nm = ""; //$NON-NLS-1$
		private int sz = -1;
		private String mme = ""; //$NON-NLS-1$

		public Builder() {
			this(false);
		}

		public Builder(final boolean cleanPaths) {
			super();
			this.cleanPaths = cleanPaths;
		}

		public Builder exportPath(final String exportPath) {
			this.pth = this.cleanPaths ? cleanPathName(exportPath) : removeTrailingSpaces(exportPath);
			return this;
		}

		public Builder name(final String name) {
			this.nm = this.cleanPaths ? cleanPathName(name) : removeTrailingSpaces(name);
			return this;
		}

		public Builder size(final int size) {
			this.sz = size;
			return this;
		}

		public Builder mimeType(final String mime) {
			this.mme = mime;
			return this;
		}

		public File build() {
			return new File(this.pth, this.nm, this.sz, this.mme);
		}
	}
}
