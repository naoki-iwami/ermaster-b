package org.insightech.er.db;

public abstract class EclipseDBManagerBase implements EclipseDBManager {

	public EclipseDBManagerBase() {
		EclipseDBManagerFactory.addDB(this);
	}

}
