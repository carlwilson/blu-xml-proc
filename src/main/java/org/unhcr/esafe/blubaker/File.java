/**
 * 
 */
package org.unhcr.esafe.blubaker;

/**
 * @author cfw
 *
 */
public final class File {
	final String exportPath;
	final int size;
	final String mimeType;

	File(String path, int size, String mimeType) {
		super();
		this.exportPath = path;
		this.size = size;
		this.mimeType = mimeType;
	}

	static class Builder {
		private String pth = "";
		private int sz = -1;
		private String mime = "";

		public Builder exportPath(final String exportPath) {
			this.pth = exportPath;
			return this;
		}

		public Builder size(final int size) {
			this.sz = size;
			return this;
		}

		public Builder mimeType(final String mime) {
			this.mime = mime;
			return this;
		}
		
		public File build() {
			return new File(this.pth, this.sz, this.mime);
		}
	}
}
