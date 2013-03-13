package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;

public class ChangeFontCommand extends AbstractCommand {

	private ViewableModel viewableModel;

	private String oldFontName;

	private String newFontName;

	private int oldFontSize;

	private int newFontSize;

	public ChangeFontCommand(ViewableModel viewableModel, String fontName,
			int fontSize) {
		this.viewableModel = viewableModel;

		this.oldFontName = viewableModel.getFontName();
		this.oldFontSize = viewableModel.getFontSize();

		this.newFontName = fontName;
		this.newFontSize = fontSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.viewableModel.setFontName(this.newFontName);
		this.viewableModel.setFontSize(this.newFontSize);
		
//		if (viewableModel instanceof ERVirtualTable) {
//			ERTable table = ((ERVirtualTable)viewableModel).getRawTable();
//			for (ERModel model : ((ERVirtualTable) viewableModel).getDiagram().getDiagramContents().getModelSet()) {
//				ERVirtualTable vtable = model.findVirtualTable(table);
//				if (!vtable.equals(viewableModel)) {
//					vtable.setFontName(fontName)
////					vtable.firePropertyChange(vtable.PROPERTY_CHANGE_FONT, null, null);
//				}
//			}
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.viewableModel.setFontName(this.oldFontName);
		this.viewableModel.setFontSize(this.oldFontSize);
	}
}
