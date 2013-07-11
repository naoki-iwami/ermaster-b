package org.insightech.er.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.settings.Settings;

public class TableSetOutlineEditPart extends AbstractOutlineEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(TableSet.PROPERTY_CHANGE_TABLE_SET)) {
			refresh();
		}
	}

	public static List<EditPart> tableEditParts;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		TableSet tableSet = (TableSet) this.getModel();

		List<ERTable> list = new ArrayList<ERTable>();

		Category category = this.getCurrentCategory();
		String filterText = getFilterText();
		for (ERTable table : tableSet) {
			if (category == null || category.contains(table)) {
				if (filterText != null) {
					if (table.getPhysicalName().toLowerCase().indexOf(filterText.toLowerCase()) < 0) {
						continue;
					}
				}
				list.add(table);
			}
		}

		if (this.getDiagram().getDiagramContents().getSettings()
				.getViewOrderBy() == Settings.VIEW_MODE_LOGICAL) {
			Collections.sort(list, TableView.LOGICAL_NAME_COMPARATOR);
		} else {
			Collections.sort(list, TableView.PHYSICAL_NAME_COMPARATOR);
		}

		if  (filterText != null) {

			Iterator<ERTable> iterator = list.iterator();
			while (iterator.hasNext()) {
				ERTable table = iterator.next();
				if (table.getPhysicalName().equalsIgnoreCase(filterText)) {
					iterator.remove();
					list.add(0, table);
					break;
				}
			}
		}

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString.getResourceString("label.table")
				+ " (" + this.getModelChildren().size() + ")");
		this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshChildren() {
		super.refreshChildren();

		tableEditParts = new ArrayList<EditPart>();
		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			tableEditParts.add(part);
			part.refresh();
		}
	}

}
