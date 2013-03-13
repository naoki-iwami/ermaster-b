package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class MoveElementCommand extends AbstractCommand {

	protected int x;

	protected int oldX;

	protected int y;

	protected int oldY;

	protected int width;

	protected int oldWidth;

	protected int height;

	protected int oldHeight;

	private NodeElement element;

	private Map<Category, Rectangle> oldCategoryRectangleMap;

	private Map<Category, Rectangle> newCategoryRectangleMap;

	private List<Category> removedCategories;

	private List<Category> addCategories;

	private ERDiagram diagram;

	private Rectangle bounds;

	public MoveElementCommand(ERDiagram diagram, Rectangle bounds, int x,
			int y, int width, int height, NodeElement element) {
		this.element = element;
		this.setNewRectangle(x, y, width, height);

		this.oldX = element.getX();
		this.oldY = element.getY();
		this.oldWidth = element.getWidth();
		this.oldHeight = element.getHeight();

		this.oldCategoryRectangleMap = new HashMap<Category, Rectangle>();
		this.newCategoryRectangleMap = new HashMap<Category, Rectangle>();

		this.removedCategories = new ArrayList<Category>();
		this.addCategories = new ArrayList<Category>();

		this.bounds = bounds;
		this.diagram = diagram;
	}

	protected void setNewRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private void initCategory(ERDiagram diagram, Rectangle bounds) {

		for (Category category : diagram.getDiagramContents().getSettings()
				.getCategorySetting().getSelectedCategories()) {
			if (category.contains(element)) {
				int categoryX = category.getX();
				int categoryY = category.getY();
				int categoryWidth = category.getWidth();
				int categoryHeight = category.getHeight();

				Rectangle oldRectangle = new Rectangle(categoryX, categoryY,
						categoryWidth, categoryHeight);

				boolean isDirty = false;

				if (diagram.getCurrentCategory() == null) {
					if (bounds.x + bounds.width < category.getX()
							|| bounds.x > category.getX() + category.getWidth()
							|| bounds.y + bounds.height < category.getY()
							|| bounds.y > category.getY()
									+ category.getHeight()) {

						this.removedCategories.add(category);

						continue;
					}
				}

				if (bounds.x < category.getX()) {
					categoryX = bounds.x;
					isDirty = true;
				}
				if (bounds.y < category.getY()) {
					categoryY = bounds.y;
					isDirty = true;
				}
				if (bounds.x + bounds.width > categoryX + categoryWidth) {
					categoryWidth = bounds.x + bounds.width - categoryX;
					isDirty = true;
				}
				if (bounds.y + bounds.height > categoryY + categoryHeight) {
					categoryHeight = bounds.y + bounds.height - categoryY;
					isDirty = true;
				}

				if (isDirty) {
					this.newCategoryRectangleMap.put(category,
							new Rectangle(categoryX, categoryY, categoryWidth,
									categoryHeight));
					this.oldCategoryRectangleMap.put(category, oldRectangle);
				}

			} else {
				if (diagram.getCurrentCategory() == null) {
					if (bounds.x >= category.getX()
							&& bounds.x + bounds.width <= category.getX()
									+ category.getWidth()
							&& bounds.y >= category.getY()
							&& bounds.y + bounds.height <= category.getY()
									+ category.getHeight()) {
						this.addCategories.add(category);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (this.bounds != null) {
			Rectangle rectangle = new Rectangle(bounds);

			if (rectangle.x != x) {
				rectangle.x = x;
			}
			if (rectangle.y != y) {
				rectangle.y = y;
			}
			if (rectangle.width < width) {
				rectangle.width = width;
			}
			if (rectangle.height < height) {
				rectangle.height = height;
			}

			this.initCategory(diagram, rectangle);
		}

		for (Category category : this.newCategoryRectangleMap.keySet()) {
			Rectangle rectangle = this.newCategoryRectangleMap.get(category);
			category.setLocation(new Location(rectangle.x, rectangle.y,
					rectangle.width, rectangle.height));
		}

		for (Category category : removedCategories) {
			category.getContents().remove(this.element);
		}

		for (Category category : addCategories) {
			category.getContents().add(this.element);
		}

		this.element.setLocation(new Location(x, y, width, height));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.element.setLocation(new Location(oldX, oldY, oldWidth, oldHeight));

		for (Category category : this.oldCategoryRectangleMap.keySet()) {
			Rectangle rectangle = this.oldCategoryRectangleMap.get(category);
			category.setLocation(new Location(rectangle.x, rectangle.y,
					rectangle.width, rectangle.height));
		}

		for (Category category : removedCategories) {
			category.getContents().add(this.element);
		}

		for (Category category : addCategories) {
			category.getContents().remove(this.element);
		}
	}
}
