package org.unhcr.archives.isadg;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 */

public final class RecordDetails {
	public final String title;
	public final String scopeAndContent;
	public final String levelOfDescription;

	/**
	 * 
	 */
	public RecordDetails(final String title, final String scopeAndContent,
			final String levelOfDescription) {
		this.title = title;
		this.scopeAndContent = scopeAndContent;
		this.levelOfDescription = levelOfDescription;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RecordDetails [title=" + this.title + ", scopeAndContent=" //$NON-NLS-1$ //$NON-NLS-2$
				+ this.scopeAndContent + ", levelOfDescription=" //$NON-NLS-1$
				+ this.levelOfDescription + "]"; //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.levelOfDescription == null) ? 0
				: this.levelOfDescription.hashCode());
		result = prime * result + ((this.scopeAndContent == null) ? 0
				: this.scopeAndContent.hashCode());
		result = prime * result
				+ ((this.title == null) ? 0 : this.title.hashCode());
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
		if (!(obj instanceof RecordDetails)) {
			return false;
		}
		RecordDetails other = (RecordDetails) obj;
		if (this.levelOfDescription == null) {
			if (other.levelOfDescription != null) {
				return false;
			}
		} else if (!this.levelOfDescription.equals(other.levelOfDescription)) {
			return false;
		}
		if (this.scopeAndContent == null) {
			if (other.scopeAndContent != null) {
				return false;
			}
		} else if (!this.scopeAndContent.equals(other.scopeAndContent)) {
			return false;
		}
		if (this.title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!this.title.equals(other.title)) {
			return false;
		}
		return true;
	}

	
}
