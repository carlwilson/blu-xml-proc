package org.unhcr.archives.esafe.blubaker.model;

public class Object {
	public final String path;
	public final String name;
	public final String description;
	
	Object (final String path, final String name, final String description) {
		this.path = path;
		this.name = name;
		this.description = description;
	}

	@Override
	public String toString() {
		return "Object [path=" + path + ", name=" + name + ", description=" + description + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.description == null) ? 0
				: this.description.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ ((this.path == null) ? 0 : this.path.hashCode());
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
		if (!(obj instanceof Object)) {
			return false;
		}
		Object other = (Object) obj;
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!this.path.equals(other.path)) {
			return false;
		}
		return true;
	}

	static class Builder {
		private String pth ="";
		private String nm = "";
		private String dsc = "";
		
		Builder path(final String path) {
			this.pth = path;
			return this;
		}
		
		Builder name(final String name) {
			this.nm = name;
			return this;
		}
		
		Builder description(final String description) {
			this.dsc = description;
			return this;
		}
		
		Object build() {
			return new Object(this.pth, this.nm, this.dsc);
		}
	}
}
