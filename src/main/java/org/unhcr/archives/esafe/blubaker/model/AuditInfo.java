/**
 * 
 */
package org.unhcr.archives.esafe.blubaker.model;

import java.util.Date;

/**
 * @author cfw
 *
 */
public final class AuditInfo {
	public final Date created;
	final Date modified;
	final String createdBy;
	final int versionId;
	final int versions;
	final int maxVersions;

	AuditInfo(final Date created, final Date modified, final String createdBy, final int versionId, final int versions,
			final int maxVersions) {
		super();
		this.created = new Date(created.getTime());
		this.modified = new Date(modified.getTime());
		this.createdBy = createdBy;
		this.versionId = versionId;
		this.versions = versions;
		this.maxVersions = maxVersions;
	}
	
	@Override
	public String toString() {
		return "AuditInfo [created=" + created + ", modified=" + modified + ", createdBy=" + createdBy + ", versionId="
				+ versionId + ", versions=" + versions + ", maxVersions=" + maxVersions + "]";
	}

	static class Builder {
		private Date crtd = new Date();
		private Date mdfd = new Date();
		private String crtdBy = "";
		private int vrsnId = -1;
		private int vrsns = -1;
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

		Builder versions(final int versions) {
			this.vrsns = versions;
			return this;
		}

		Builder maxVersions(final int maxVersions) {
			this.mxVrsns = maxVersions;
			return this;
		}
		
		AuditInfo build() {
			return new AuditInfo(this.crtd, this.mdfd, this.crtdBy, this.vrsnId, this.vrsns, this.mxVrsns);
		}
	}
}
