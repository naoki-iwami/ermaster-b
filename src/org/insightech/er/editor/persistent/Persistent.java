package org.insightech.er.editor.persistent;

import java.io.IOException;
import java.io.InputStream;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.impl.PersistentXmlImpl;

public abstract class Persistent {

	private static Persistent persistent = new PersistentXmlImpl();

	public static Persistent getInstance() {
		return persistent;
	}

	abstract public InputStream createInputStream(ERDiagram diagram)
			throws IOException;

	abstract public ERDiagram load(InputStream in) throws Exception;

}