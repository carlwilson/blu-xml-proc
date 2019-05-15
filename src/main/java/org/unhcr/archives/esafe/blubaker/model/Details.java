/**
 * 
 */
package org.unhcr.archives.esafe.blubaker.model;

/**
 * @author cfw
 *
 */
public final class Details {
	public final int id;
	public final int parentId;
	public final int subType;
	
	Details (final int id, final int parentId, final int subType) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.subType = subType;
	}
	
	@Override
	public String toString() {
		return "Details [id=" + this.id + ", parentId=" + this.parentId + ", subType=" + this.subType + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		result = prime * result + this.parentId;
		result = prime * result + this.subType;
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
		if (!(obj instanceof Details)) {
			return false;
		}
		Details other = (Details) obj;
		if (this.id != other.id) {
			return false;
		}
		if (this.parentId != other.parentId) {
			return false;
		}
		if (this.subType != other.subType) {
			return false;
		}
		return true;
	}


	static class Builder {
		private int ident = -1;
		private int prntId = -1;
		private int sbTyp = -1;
		
		Builder id(final int id) {
			this.ident = id;
			return this;
		}
		
		Builder parentId(final int parentId) {
			this.prntId = parentId;
			return this;
		}
		
		Builder subType(final int subType) {
			this.sbTyp = subType;
			return this;
		}
		
		Details build() {
			return new Details(this.ident, this.prntId, this.sbTyp);
		}
	}
}
