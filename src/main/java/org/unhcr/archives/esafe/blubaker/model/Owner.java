/**
 * 
 */
package org.unhcr.archives.esafe.blubaker.model;

/**
 * @author cfw
 *
 */
public final class Owner {
	public final int id;
	public final String name;

	Owner(final int id, final String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Owner [id=" + id + ", name=" + name + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
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
		if (!(obj instanceof Owner)) {
			return false;
		}
		Owner other = (Owner) obj;
		if (this.id != other.id) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	static class Builder {
		private int ident = -1;
		private String nm = "";
		
		Builder id(final int id) {
			this.ident = id;
			return this;
		}
		
		Builder name(final String name) {
			this.nm = name;
			return this;
		}
		
		Owner build() {
			return new Owner(this.ident, this.nm);
		}
	}
}
