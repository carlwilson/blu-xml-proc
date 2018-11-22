/**
 * 
 */
package org.unhcr.esafe.blubaker;

/**
 * @author cfw
 *
 */
public final class Details {
	final int id;
	final int parentId;
	final int subType;
	
	Details (final int id, final int parentId, final int subType) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.subType = subType;
	}
	
	@Override
	public String toString() {
		return "Details [id=" + id + ", parentId=" + parentId + ", subType=" + subType + "]";
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
