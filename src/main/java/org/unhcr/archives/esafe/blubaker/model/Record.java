/**
 *
 */
package org.unhcr.archives.esafe.blubaker.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.unhcr.archives.utils.ExportDetails;

/**
 * @author cfw
 *
 */
public final class Record {
	public final Details details;
	public final Owner owner;
	public final AuditInfo auditInfo;
	public final Object object;
	public final File file;
	private static final Integer[] containerValues = new Integer[] {0, 201, 202, 298, 412, 751, 10136};
	public static final Set<Integer> CONTAINER_SUB_TYPES = new HashSet<>(Arrays.asList(containerValues));

	Record(final Details details, final Owner owner, final AuditInfo auditInfo,
			final Object object, final File file) {
		super();
		this.details = details;
		this.owner = owner;
		this.auditInfo = auditInfo;
		this.object = object;
		this.file = file;
	}

	public boolean isDirectory() {
		return CONTAINER_SUB_TYPES.contains(this.details.subType);
	}

	public boolean isFile() {
		return !this.isDirectory();
	}

	@Override
	public String toString() {
		return "Record [details=" + this.details + ", owner=" + this.owner //$NON-NLS-1$ //$NON-NLS-2$
				+ ", auditInfo=" + this.auditInfo + ", object=" + this.object  //$NON-NLS-1$ //$NON-NLS-2$
				+ ", file=" + this.file + "]"; //$NON-NLS-1$ //$NON-NLS-2$
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
				+ ((this.details == null) ? 0 : this.details.hashCode());
		result = prime * result
				+ ((this.file == null) ? 0 : this.file.hashCode());
		result = prime * result
				+ ((this.object == null) ? 0 : this.object.hashCode());
		result = prime * result
				+ ((this.owner == null) ? 0 : this.owner.hashCode());
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
		if (!(obj instanceof Record)) {
			return false;
		}
		Record other = (Record) obj;
		if (this.auditInfo == null) {
			if (other.auditInfo != null) {
				return false;
			}
		} else if (!this.auditInfo.equals(other.auditInfo)) {
			return false;
		}
		if (this.details == null) {
			if (other.details != null) {
				return false;
			}
		} else if (!this.details.equals(other.details)) {
			return false;
		}
		if (this.file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!this.file.equals(other.file)) {
			return false;
		}
		if (this.object == null) {
			if (other.object != null) {
				return false;
			}
		} else if (!this.object.equals(other.object)) {
			return false;
		}
		if (this.owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!this.owner.equals(other.owner)) {
			return false;
		}
		return true;
	}

	public Path getExportRelativePath(final ExportDetails exportDetails) throws BadRecordException {
		if (!this.isFile()) return null;
		// Get the object export path path from the file
		Path recExpPath = (this.isDirectory()) ? Paths.get(this.object.path.replace(":", "/")) : Paths.get(this.file.exportPath, this.file.name);
		if (recExpPath.toString().isEmpty()) {
			throw new BadRecordException(this.details.id, MessageFormat.format(
					"File record id: {0, number, #}, name: {1} has no file export path.", //$NON-NLS-1$
					Integer.valueOf(this.details.id), this.object.name));
		}
		return recExpPath;
	}

	public static String cleanExportPath(final String exportPath) {
		String[] pathParts = exportPath.split("\\\\");
		List<String> cleanParts = new ArrayList<>();
		// cleanParts.add("objects");
		boolean entFound = false;
		for (String part : pathParts) {
			if (!entFound && !part.equals("Enterprise")) {
				continue;
			}
			entFound = true;
			cleanParts.add(part);
		}
		String [] retVal = new String[cleanParts.size()];
		retVal = cleanParts.toArray(retVal);
		return String.join("/", retVal);
	}

	public static class Builder {
		private Details.Builder detBld = new Details.Builder();
		private Owner.Builder ownBld = new Owner.Builder();
		private AuditInfo.Builder audBld = new AuditInfo.Builder();
		private Object.Builder objBld = new Object.Builder();
		private File.Builder flBld = new File.Builder();

		public Builder() {
			super();
		}

		public Builder(Record record) {
			super();
			this.id(record.details.id);
			this.parentId(record.details.parentId);
			this.subType(record.details.subType);
			this.ownerId(record.owner.id);
			this.ownerName(record.owner.name);
			this.created(record.auditInfo.created);
			this.modified(record.auditInfo.modified);
			this.createdBy(record.auditInfo.createdBy);
			this.versionId(record.auditInfo.versionId);
			this.versions(record.auditInfo.versions);
			this.maxVersions(record.auditInfo.maxVersions);
			this.path(record.object.path);
			this.name(record.object.name);
			this.description(record.object.description);
			this.exportPath(record.file.exportPath);
			this.fileName(record.file.name);
			this.size(record.file.size);
			this.mimeType(record.file.mimeType);
		}
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

		public Builder versions(final int version) {
			this.audBld.versions(version);
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

		public Builder fileName(final String fileName) {
			this.flBld.name(fileName);
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
			return new Record(this.detBld.build(), this.ownBld.build(),
					this.audBld.build(), this.objBld.build(),
					this.flBld.build());
		}
	}
}
