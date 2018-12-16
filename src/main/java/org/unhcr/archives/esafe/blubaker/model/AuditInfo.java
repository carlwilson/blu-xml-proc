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
	public final Date modified;
	public final String createdBy;
	public final int versionId;
	public final int versions;
	public final int maxVersions;

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

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.created == null) ? 0 : this.created.hashCode());
		result = prime * result
				+ ((this.createdBy == null) ? 0 : this.createdBy.hashCode());
		result = prime * result + this.maxVersions;
		result = prime * result
				+ ((this.modified == null) ? 0 : this.modified.hashCode());
		result = prime * result + this.versionId;
		result = prime * result + this.versions;
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
		if (!(obj instanceof AuditInfo)) {
			return false;
		}
		AuditInfo other = (AuditInfo) obj;
		if (this.created == null) {
			if (other.created != null) {
				return false;
			}
		} else if (!this.created.equals(other.created)) {
			return false;
		}
		if (this.createdBy == null) {
			if (other.createdBy != null) {
				return false;
			}
		} else if (!this.createdBy.equals(other.createdBy)) {
			return false;
		}
		if (this.maxVersions != other.maxVersions) {
			return false;
		}
		if (this.modified == null) {
			if (other.modified != null) {
				return false;
			}
		} else if (!this.modified.equals(other.modified)) {
			return false;
		}
		if (this.versionId != other.versionId) {
			return false;
		}
		if (this.versions != other.versions) {
			return false;
		}
		return true;
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
