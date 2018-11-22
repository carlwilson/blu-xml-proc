/**
 * 
 */
package org.unhcr.esafe.blubaker;

import java.util.Date;

/**
 * @author cfw
 *
 */
public final class Record {
	final Details details;
	final Owner owner;
	final AuditInfo auditInfo;
	final Object object;
	final File file;

	private Record(final Details details, final Owner owner, final AuditInfo auditInfo, final Object object,
			final File file) {
		super();
		this.details = details;
		this.owner = owner;
		this.auditInfo = auditInfo;
		this.object = object;
		this.file = file;
	}

	public static class Builder {
		private Details.Builder detBld = new Details.Builder();
		private Owner.Builder ownBld = new Owner.Builder();
		private AuditInfo.Builder audBld = new AuditInfo.Builder();
		private Object.Builder objBld = new Object.Builder();
		private File.Builder flBld = new File.Builder();

		public Builder id(final int id) {
			this.detBld.id(id);
			return this;
		}

		public Builder parentId(final int parentId) {
			this.detBld.parentId(parentId);
			return this;
		}

		public Builder subType(final int subType) {
			this.detBld.subType(subType);
			return this;
		}

		public Builder ownerId(final int ownerId) {
			this.ownBld.id(ownerId);
			return this;
		}

		public Builder ownerName(final String name) {
			this.ownBld.name(name);
			return this;
		}

		public Builder created(final Date created) {
			this.audBld.created(created);
			return this;
		}

		public Builder modified(final Date modified) {
			this.audBld.modified(modified);
			return this;
		}

		public Builder createdBy(final String createdBy) {
			this.audBld.createdBy(createdBy);
			return this;
		}

		public Builder versionId(final int versionId) {
			this.audBld.versionId(versionId);
			return this;
		}

		public Builder version(final int version) {
			this.audBld.version(version);
			return this;
		}

		public Builder maxVersions(final int maxVersions) {
			this.audBld.maxVersions(maxVersions);
			return this;
		}

		public Builder path(final String path) {
			this.objBld.path(path);
			return this;
		}

		public Builder name(final String name) {
			this.objBld.name(name);
			return this;
		}

		public Builder description(final String description) {
			this.objBld.description(description);
			return this;
		}

		public Builder exportPath(final String exportPath) {
			this.flBld.exportPath(exportPath);
			return this;
		}

		public Builder size(final int size) {
			this.flBld.size(size);
			return this;
		}

		public Builder mimeType(final String mimeType) {
			this.flBld.mimeType(mimeType);
			return this;
		}

		public Record build() {
			return new Record(this.detBld.build(), this.ownBld.build(), this.audBld.build(), this.objBld.build(),
					this.flBld.build());
		}
	}
}
