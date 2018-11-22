/**
 * 
 */
package org.unhcr.esafe.blubaker;

import java.util.Date;

/**
 * @author cfw
 *
 */
public final class AuditInfo {
	final Date created;
	final Date modified;
	final String createdBy;
	final int versionId;
	final int version;
	final int maxVersions;

	AuditInfo(final Date created, final Date modified, final String createdBy, final int versionId, final int version,
			final int maxVersions) {
		super();
		this.created = new Date(created.getTime());
		this.modified = new Date(modified.getTime());
		this.createdBy = createdBy;
		this.versionId = versionId;
		this.version = version;
		this.maxVersions = maxVersions;
	}
	
	static class Builder {
		private Date crtd = new Date();
		private Date mdfd = new Date();
		private String crtdBy = "";
		private int vrsnId = -1;
		private int vrsn = -1;
		private int mxVrsns = -1;

		Builder created(final Date created) {
			this.crtd = new Date(created.getTime());
			return this;
		}

		Builder modified(final Date modified) {
			this.mdfd = new Date(modified.getTime());
			return this;
		}

		Builder createdBy(final String createdBy) {
			this.crtdBy = createdBy;
			return this;
		}

		Builder versionId(final int versionId) {
			this.vrsnId = versionId;
			return this;
		}

		Builder version(final int version) {
			this.vrsn = version;
			return this;
		}

		Builder maxVersions(final int maxVersions) {
			this.mxVrsns = maxVersions;
			return this;
		}
		
		AuditInfo build() {
			return new AuditInfo(this.crtd, this.mdfd, this.crtdBy, this.vrsnId, this.vrsn, this.mxVrsns);
		}
	}
}
