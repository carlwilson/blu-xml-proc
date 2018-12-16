/**
 * 
 */
package org.unhcr.archives.esafe.blubaker.model;

/**
 * @author cfw
 *
 */
public final class File {
	public final String exportPath;
	public final String name;
	public final int size;
	public final String mimeType;

	File(final String path, final String name, final int size,
			final String mimeType) {
		super();
		this.exportPath = path;
		this.name = name;
		this.size = size;
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "File [exportPath=" + this.exportPath + ", name=" + this.name //$NON-NLS-1$ //$NON-NLS-2$
				+ ", size=" + this.size + ", mimeType=" + this.mimeType + "]";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.exportPath == null) ? 0 : this.exportPath.hashCode());
		result = prime * result
				+ ((this.mimeType == null) ? 0 : this.mimeType.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
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

	static class Builder {
		private String pth = ""; //$NON-NLS-1$
		private String nm = ""; //$NON-NLS-1$
		private int sz = -1;
		private String mme = ""; //$NON-NLS-1$

		public Builder exportPath(final String exportPath) {
			this.pth = exportPath;
			return this;
		}

		public Builder name(final String name) {
			this.nm = name;
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
