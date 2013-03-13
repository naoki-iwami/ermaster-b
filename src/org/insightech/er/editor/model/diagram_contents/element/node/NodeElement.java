package org.insightech.er.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public abstract class NodeElement extends ViewableModel implements ObjectModel {

	private static final long serialVersionUID = -5143984125818569247L;

	public static final String PROPERTY_CHANGE_RECTANGLE = "rectangle";

	public static final String PROPERTY_CHANGE_INCOMING = "incoming";

	public static final String PROPERTY_CHANGE_OUTGOING = "outgoing";

	private Location location;

	private List<ConnectionElement> incomings = new ArrayList<ConnectionElement>();

	private List<ConnectionElement> outgoings = new ArrayList<ConnectionElement>();

	private ERDiagram diagram;
	
	/**
	 * この要素を変更したとき、他モデル上の要素も変更する必要があるか？
	 * @return
	 */
	abstract public boolean needsUpdateOtherModel();

	public NodeElement() {
		this.location = new Location(0, 0, 0, 0);
	}

	public void setDiagram(ERDiagram diagram) {
		this.diagram = diagram;
	}

	public ERDiagram getDiagram() {
		return diagram;
	}

	public int getX() {
		return this.location.x;
	}

	public int getY() {
		return this.location.y;
	}

	public int getWidth() {
		return this.location.width;
	}

	public int getHeight() {
		return this.location.height;
	}

	public void setLocation(Location location) {
		this.location = location;

		this.firePropertyChange(PROPERTY_CHANGE_RECTANGLE, null, null);
	}

	public List<ConnectionElement> getIncomings() {
		return incomings;
	}

	public List<ConnectionElement> getOutgoings() {
		return outgoings;
	}

	public void setIncoming(List<ConnectionElement> relations) {
		this.incomings = relations;
		this.firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
	}

	public void setOutgoing(List<ConnectionElement> relations) {
		this.outgoings = relations;
		this.firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
	}

	public void addIncoming(ConnectionElement relation) {
		this.incomings.add(relation);
		this.firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
	}

	public void removeIncoming(ConnectionElement relation) {
		this.incomings.remove(relation);
		this.firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
	}

	public void addOutgoing(ConnectionElement relation) {
		this.outgoings.add(relation);
		this.firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
	}

	public void removeOutgoing(ConnectionElement relation) {
		this.outgoings.remove(relation);
		this.firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
	}

	public List<NodeElement> getReferringElementList() {
		List<NodeElement> referringElementList = new ArrayList<NodeElement>();

		for (ConnectionElement connectionElement : this.getOutgoings()) {
			NodeElement targetElement = connectionElement.getTarget();

			referringElementList.add(targetElement);
		}

		return referringElementList;
	}

	public List<NodeElement> getReferedElementList() {
		List<NodeElement> referedElementList = new ArrayList<NodeElement>();

		for (ConnectionElement connectionElement : this.getIncomings()) {
			NodeElement sourceElement = connectionElement.getSource();

			referedElementList.add(sourceElement);
		}

		return referedElementList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeElement clone() {
		NodeElement clone = (NodeElement) super.clone();

		clone.location = this.location.clone();
		clone.setIncoming(new ArrayList<ConnectionElement>());
		clone.setOutgoing(new ArrayList<ConnectionElement>());

		return clone;
	}

}
