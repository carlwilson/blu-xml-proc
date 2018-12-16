package org.unhcr.archives.isadg;

import java.util.Date;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 */

public final class ArchivalHistory {
	final Date exported;
	final String objectPath;

	public ArchivalHistory(final Date exported, final String objectPath) {
		super();
		this.exported = new Date(exported.getTime());
		this.objectPath = objectPath;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.exported == null) ? 0 : this.exported.hashCode());
		result = prime * result
				+ ((this.objectPath == null) ? 0 : this.objectPath.hashCode());
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
		if (!(obj instanceof ArchivalHistory)) {
			return false;
		}
		ArchivalHistory other = (ArchivalHistory) obj;
		if (this.exported == null) {
			if (other.exported != null) {
				return false;
			}
		} else if (!this.exported.equals(other.exported)) {
			return false;
		}
		if (this.objectPath == null) {
			if (other.objectPath != null) {
				return false;
			}
		} else if (!this.objectPath.equals(other.objectPath)) {
			return false;
		}
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArchivalHistory [exported=" + this.exported + ", objectPath="
				+ this.objectPath + "]";
	}

}
