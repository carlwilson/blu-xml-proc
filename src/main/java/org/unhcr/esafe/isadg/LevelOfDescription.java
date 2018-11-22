package org.unhcr.esafe.isadg;

import java.util.Date;

abstract class LevelOfDescription {
	final int dataId;
	final int parentId;
	final Date created;

	protected LevelOfDescription(final int dataId, final int parentId, final Date created) {
		super();
		this.dataId = dataId;
		this.parentId = parentId;
		this.created = new Date(created.getTime());
	}
	
}
