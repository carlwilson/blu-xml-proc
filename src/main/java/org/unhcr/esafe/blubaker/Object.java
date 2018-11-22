package org.unhcr.esafe.blubaker;

public class Object {
	final String path;
	final String name;
	final String description;
	
	Object (final String path, final String name, final String description) {
		this.path = path;
		this.name = name;
		this.description = description;
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
