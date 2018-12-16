package org.unhcr.archives.isadg;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 */

public final class AuditInfo {
	final static String createdKey = "System creation date"; //$NON-NLS-1$
	final static String modifedKey = "System modification date"; //$NON-NLS-1$

	public final String creator;
	final Map<String, Date> dates = new HashMap<>();
	final Map<String, Range> ranges = new HashMap<>();

	public AuditInfo(final String creator, final Date created, final Date modified) {
		this.creator = creator;
		this.dates.put(createdKey, new Date(created.getTime()));
		this.dates.put(modifedKey,
				new Date(modified.getTime()));
	}
	
	public Date created() {
		return this.dates.get(createdKey);
	}

	public Map<String, Date> dates() {
		return Collections.unmodifiableMap(this.dates);
	}

	public Map<String, Range> ranges() {
		return Collections.unmodifiableMap(this.ranges);
	}

	Range addRange(final String label, final Range range) {
		return this.ranges.put(label, range);
	}

	Range addRange(final String label, final Date start,
			final Date finish) {
		return this.addRange(label, new Range(start, finish));
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.creator == null) ? 0 : this.creator.hashCode());
		result = prime * result
				+ ((this.dates == null) ? 0 : this.dates.hashCode());
		result = prime * result
				+ ((this.ranges == null) ? 0 : this.ranges.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuditInfo [creator=" + this.creator + ", dates=" + this.dates //$NON-NLS-1$ //$NON-NLS-2$
				+ ", ranges=" + this.ranges + "]"; //$NON-NLS-1$ //$NON-NLS-2$
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
		if (!(obj instanceof AuditInfo)) {
			return false;
		}
		AuditInfo other = (AuditInfo) obj;
		if (this.creator == null) {
			if (other.creator != null) {
				return false;
			}
		} else if (!this.creator.equals(other.creator)) {
			return false;
		}
		if (this.dates == null) {
			if (other.dates != null) {
				return false;
			}
		} else if (!this.dates.equals(other.dates)) {
			return false;
		}
		if (this.ranges == null) {
			if (other.ranges != null) {
				return false;
			}
		} else if (!this.ranges.equals(other.ranges)) {
			return false;
		}
		return true;
	}

	public static final class Range {
		public final Date start;
		public final Date finish;

		public Range(final Date start, final Date finish) {
			this.start = start.before(finish) ? new Date(start.getTime())
					: new Date(finish.getTime());
			this.finish = start.before(finish) ? new Date(finish.getTime())
					: new Date(start.getTime());
		}
	}
}
