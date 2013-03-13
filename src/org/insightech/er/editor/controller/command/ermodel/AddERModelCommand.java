package org.insightech.er.editor.controller.command.ermodel;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;

public class AddERModelCommand extends AbstractCommand {
	
	private String name;
	private ERDiagram diagram;

	public AddERModelCommand(ERDiagram diagram, String name) {
		super();
		this.diagram = diagram;
		this.name = name;
	}

	@Override
	protected void doExecute() {
		ERModel ermodel = new ERModel(diagram);
		ermodel.setName(name);
//		diagram.addContent(ermodel);
		diagram.addErmodel(ermodel);
	}

	@Override
	protected void doUndo() {
		// TODO Auto-generated method stub

	}

}
