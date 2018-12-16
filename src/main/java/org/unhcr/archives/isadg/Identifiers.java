package org.unhcr.archives.isadg;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 */

public final class Identifiers {

	public final String referenceCode;
	final Map<String, String> alternativeIdentifiers;

	public Identifiers(final String referenceCode,
			final Map<String, String> alternativeIds) {
		this.referenceCode = referenceCode;
		this.alternativeIdentifiers = Collections
				.unmodifiableMap(alternativeIds);
	}
	
	public Map<String, String> alternativeIdentifiers() {
		return Collections.unmodifiableMap(this.alternativeIdentifiers);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Identifiers [referenceCode=" + this.referenceCode //$NON-NLS-1$
				+ ", alternativeIdentifiers=" + this.alternativeIdentifiers //$NON-NLS-1$
				+ "]"; //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.alternativeIdentifiers == null) ? 0
				: this.alternativeIdentifiers.hashCode());
		result = prime * result + ((this.referenceCode == null) ? 0
				: this.referenceCode.hashCode());
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
		if (!(obj instanceof Identifiers)) {
			return false;
		}
		Identifiers other = (Identifiers) obj;
		if (this.alternativeIdentifiers == null) {
			if (other.alternativeIdentifiers != null) {
				return false;
			}
		} else if (!this.alternativeIdentifiers
				.equals(other.alternativeIdentifiers)) {
			return false;
		}
		if (this.referenceCode == null) {
			if (other.referenceCode != null) {
				return false;
			}
		} else if (!this.referenceCode.equals(other.referenceCode)) {
			return false;
		}
		return true;
	}

	
}
