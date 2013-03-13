package org.insightech.er.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.util.Format;

public class VGroup extends NodeElement implements IResizable, Comparable<VGroup> {

	private static final long serialVersionUID = 8251435120903384808L;

	public static final String PROPERTY_CHANGE_VGROUP = "vgroup";

	private List<NodeElement> nodeElementList;

	private String name;

	public VGroup() {
		this.nodeElementList = new ArrayList<NodeElement>();
	}

	public void setContents(List<NodeElement> contetns) {
		this.nodeElementList = contetns;

		if (this.getWidth() == 0) {

			int categoryX = 0;
			int categoryY = 0;

			int categoryWidth = 300;
			int categoryHeight = 400;

			if (!nodeElementList.isEmpty()) {
				categoryX = nodeElementList.get(0).getX();
				categoryY = nodeElementList.get(0).getY();
				categoryWidth = nodeElementList.get(0).getWidth();
				categoryHeight = nodeElementList.get(0).getHeight();

				for (NodeElement nodeElement : nodeElementList) {
					int x = nodeElement.getX();
					int y = nodeElement.getY();
					int width = nodeElement.getWidth();
					int height = nodeElement.getHeight();

					if (categoryX > x) {
						width += categoryX - x;
						categoryX = x;
					}
					if (categoryY > y) {
						height += categoryY - y;
						categoryY = y;
					}

					if (x - categoryX + width > categoryWidth) {
						categoryWidth = x - categoryX + width;
					}

					if (y - categoryY + height > categoryHeight) {
						categoryHeight = y - categoryY + height;
					}

				}
			}

			this.setLocation(new Location(categoryX, categoryY, categoryWidth,
					categoryHeight));
		}
	}

	public boolean contains(NodeElement nodeElement) {
		return this.nodeElementList.contains(nodeElement);
	}

	public boolean isVisible(NodeElement nodeElement, ERDiagram diagram) {
		return true;
//		boolean isVisible = false;
//
//		if (this.contains(nodeElement)) {
//			isVisible = true;
//
//		} else {
//			VGroupSetting groupSettings = diagram.getDiagramContents()
//					.getSettings().getGroupSetting();
//
//			if (groupSettings.isShowReferredTables()) {
//				for (NodeElement referringElement : nodeElement.getReferringElementList()) {
//					if (this.contains(referringElement)) {
//						isVisible = true;
//						break;
//					}
//				}
//			}
//		}
//
//		return isVisible;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.firePropertyChange(PROPERTY_CHANGE_VGROUP, null, null);
	}

	public List<NodeElement> getContents() {
		return nodeElementList;
	}

	public List<ERTable> getTableContents() {
		List<ERTable> tableList = new ArrayList<ERTable>();

		for (NodeElement nodeElement : this.nodeElementList) {
			if (nodeElement instanceof ERTable) {
				tableList.add((ERTable) nodeElement);
			}
		}

		return tableList;
	}

	public List<View> getViewContents() {
		List<View> viewList = new ArrayList<View>();

		for (NodeElement nodeElement : this.nodeElementList) {
			if (nodeElement instanceof View) {
				viewList.add((View) nodeElement);
			}
		}

		return viewList;
	}

	public List<TableView> getTableViewContents() {
		List<TableView> tableList = new ArrayList<TableView>();

		for (NodeElement nodeElement : this.nodeElementList) {
			if (nodeElement instanceof TableView) {
				tableList.add((TableView) nodeElement);
			}
		}

		return tableList;
	}

	public int compareTo(VGroup other) {
		int compareTo = 0;

		compareTo = Format.null2blank(this.name).compareTo(
				Format.null2blank(other.name));

		return compareTo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VGroup clone() {
		VGroup clone = (VGroup) super.clone();
		return clone;
	}

	public String getDescription() {
		return "";
	}

	public String getObjectType() {
		return "vgroup";
	}

	@Override
	public boolean needsUpdateOtherModel() {
		return false;
	}
}
