package org.unhcr.archives.isadg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UnitOfDescription {

	public final UnitOfDescription parent;
	public final Identifiers identifiers;
	public final AuditInfo auditInfo;
	public final ExtentAndMedium extent;
	public final RecordDetails details;
	public final ArchivalHistory history;

	final Set<UnitOfDescription> children = new HashSet<>();

	public UnitOfDescription(final UnitOfDescription parent,
			final Identifiers identifiers, final AuditInfo auditInfo,
			final ExtentAndMedium extent, final RecordDetails details,
			final ArchivalHistory history) {
		super();
		this.parent = parent;
		this.identifiers = identifiers;
		this.auditInfo = auditInfo;
		this.extent = extent;
		this.details = details;
		this.history = history;
	}

	public Set<UnitOfDescription> getChildren() {
		return Collections.unmodifiableSet(this.children);
	}

	public void setChildren(Set<UnitOfDescription> children) {
		this.children.clear();
		this.children.addAll(children);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UnitOfDescription [parent=" //$NON-NLS-1$
				+ ((this.parent == null) ? "null" //$NON-NLS-1$
						: this.parent.identifiers.referenceCode)
				+ ", identifiers=" + this.identifiers + ", auditInfo=" //$NON-NLS-1$ //$NON-NLS-2$
				+ this.auditInfo + ", extent=" + this.extent + ", details=" //$NON-NLS-1$ //$NON-NLS-2$
				+ this.details + ", history=" + this.history + ", children=" //$NON-NLS-1$ //$NON-NLS-2$
				+ this.children + "]"; //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.auditInfo == null) ? 0 : this.auditInfo.hashCode());
		result = prime * result
				+ ((this.children == null) ? 0 : this.children.hashCode());
		result = prime * result
				+ ((this.details == null) ? 0 : this.details.hashCode());
		result = prime * result
				+ ((this.extent == null) ? 0 : this.extent.hashCode());
		result = prime * result
				+ ((this.history == null) ? 0 : this.history.hashCode());
		result = prime * result + ((this.identifiers == null) ? 0
				: this.identifiers.hashCode());
		result = prime * result
				+ ((this.parent.identifiers.referenceCode == null) ? 0
						: this.parent.identifiers.referenceCode.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UnitOfDescription)) {
			return false;
		}
		UnitOfDescription other = (UnitOfDescription) obj;
		if (this.auditInfo == null) {
			if (other.auditInfo != null) {
				return false;
			}
		} else if (!this.auditInfo.equals(other.auditInfo)) {
			return false;
		}
		if (this.children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!this.children.equals(other.children)) {
			return false;
		}
		if (this.details == null) {
			if (other.details != null) {
				return false;
			}
		} else if (!this.details.equals(other.details)) {
			return false;
		}
		if (this.extent == null) {
			if (other.extent != null) {
				return false;
			}
		} else if (!this.extent.equals(other.extent)) {
			return false;
		}
		if (this.history == null) {
			if (other.history != null) {
				return false;
			}
		} else if (!this.history.equals(other.history)) {
			return false;
		}
		if (this.identifiers == null) {
			if (other.identifiers != null) {
				return false;
			}
		} else if (!this.identifiers.equals(other.identifiers)) {
			return false;
		}
		if (this.parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!this.parent.equals(other.parent)) {
			return false;
		}
		return true;
	}

}
