package org.insightech.er.editor.model.diagram_contents.element.node.note;

import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.util.Format;

/**
 * ノートのモデル
 * 
 * @author nakajima
 * 
 */
public class Note extends NodeElement implements Comparable<Note> {

	private static final long serialVersionUID = -8810455349879962852L;

	public static final String PROPERTY_CHANGE_NOTE = "note";

	/** 親モデル */
	private ERModel model;

	private String text;

	public Note() {
		System.out.println("Note");
	}
	
	/**
	 * 親モデルを取得します。
	 * @return 親モデル
	 */
	public ERModel getModel() {
	    return model;
	}

	/**
	 * 親モデルを設定します。
	 * @param model 親モデル
	 */
	public void setModel(ERModel model) {
	    this.model = model;
	}

	/**
	 * ノートの本文を返却します。
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * ノートの本文を設定します。
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;

		this.firePropertyChange(PROPERTY_CHANGE_NOTE, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NodeElement> getReferringElementList() {
		List<NodeElement> referringElementList = super.getReferringElementList();

		for (ConnectionElement connectionElement : this.getIncomings()) {
			NodeElement sourceElement = connectionElement.getSource();
			referringElementList.add(sourceElement);
		}

		return referringElementList;
	}

	public String getDescription() {
		return "";
	}

	public int compareTo(Note other) {
		int compareTo = 0;

		compareTo = Format.null2blank(this.text).compareTo(
				Format.null2blank(other.text));

		return compareTo;
	}

	public String getName() {
		String name = text;
		if (name == null) {
			name = "";

		} else if (name.length() > 20) {
			name = name.substring(0, 20);
		}

		return name;
	}

	public String getObjectType() {
		return "note";
	}

	@Override
	public boolean needsUpdateOtherModel() {
		return false;
	}
	
}
