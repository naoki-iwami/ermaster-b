package org.insightech.er.editor.model.tracking;

import java.io.Serializable;
import java.util.Date;

import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.edit.CopyManager;

public class ChangeTracking implements Serializable {

	private static final long serialVersionUID = 4766921781666293191L;

	private DiagramContents diagramContents;

	private Date updatedDate;

	private String comment;

	public ChangeTracking(DiagramContents diagramContents) {
		CopyManager copyManager = new CopyManager();

		this.diagramContents = copyManager.copy(diagramContents);
		this.updatedDate = new Date();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public DiagramContents getDiagramContents() {
		return this.diagramContents;
	}

}
