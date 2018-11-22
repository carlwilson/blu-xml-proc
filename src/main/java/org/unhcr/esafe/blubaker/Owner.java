/**
 * 
 */
package org.unhcr.esafe.blubaker;

/**
 * @author cfw
 *
 */
public final class Owner {
	final int id;
	final String name;

	Owner(final int id, final String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Owner [id=" + id + ", name=" + name + "]";
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
