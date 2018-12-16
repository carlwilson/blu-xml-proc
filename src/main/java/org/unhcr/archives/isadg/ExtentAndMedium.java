package org.unhcr.archives.isadg;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 */

public final class ExtentAndMedium {

	public final int size;
	public final int versions;

	public ExtentAndMedium(final int size, final int versions) {
		this.size = size;
		this.versions = versions;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtentAndMedium [size=" + this.size + ", versions=" //$NON-NLS-1$ //$NON-NLS-2$
				+ this.versions + "]"; //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.size;
		result = prime * result + this.versions;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ExtentAndMedium)) {
			return false;
		}
		ExtentAndMedium other = (ExtentAndMedium) obj;
		if (this.size != other.size) {
			return false;
		}
		if (this.versions != other.versions) {
			return false;
		}
		return true;
	}

}
