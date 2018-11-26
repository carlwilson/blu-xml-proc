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

	File(final String path, final String name, final int size, final String mimeType) {
		super();
		this.exportPath = path;
		this.name = name;
		this.size = size;
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "File [exportPath=" + exportPath + ", name=" + name + ", size=" + size + ", mimeType=" + mimeType + "]";
	}

	static class Builder {
		private String pth = "";
		private String nm = "";
		private int sz = -1;
		private String mme = "";

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
