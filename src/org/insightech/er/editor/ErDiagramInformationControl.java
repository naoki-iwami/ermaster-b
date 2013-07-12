package org.insightech.er.editor;

import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.view.outline.ERDiagramOutlinePage;


public class ErDiagramInformationControl extends AbstractInformationControl {

	private ERDiagramOutlinePage outline;
	private Text search;
	private ERDiagram diagram;

	public ErDiagramInformationControl(ERDiagram diagram, Shell shell, Control composite) {
		super(shell, true);
		this.diagram = diagram;

		create();

		int width  = 300;
		int height = 300;

		Point loc  = composite.toDisplay(0, 0);
		Point size = composite.getSize();

		int x = (size.x - width)  / 2 + loc.x;
		int y = (size.y - height) / 2 + loc.y;

		setSize(width, height);
		setLocation(new Point(x, y));
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				dispose();
			}
		});
	}

	@Override
	protected void createContent(Composite parent) {
		Color foreground = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
		Color background = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setForeground(foreground);
		composite.setBackground(background);


		search = new Text(composite, SWT.NONE);
		search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		search.setForeground(foreground);
		search.setBackground(background);

		new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL)
			.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Dialog.applyDialogFont(search);

		search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		search.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String filterText = search.getText();
				outline.setFilterText(filterText);
			}
		});
		search.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.CR){
					selectAndDispose();
				}
				if(e.keyCode == SWT.ARROW_UP){
					outline.getControl().setFocus();
				}
				if(e.keyCode == SWT.ARROW_DOWN){
					outline.getControl().setFocus();
				}
			}
		});

		Composite treeArea = new Composite(composite, SWT.NULL);
		treeArea.setLayout(new FillLayout());
		treeArea.setLayoutData(new GridData(GridData.FILL_BOTH));

		outline = new ERDiagramOutlinePage(diagram);
		outline.setQuickMode(true);

		IEditorPart activeEditor = ((ERDiagramMultiPageEditor) ERModelUtil.getActiveEditor()).getActiveEditor();
		if (activeEditor instanceof EROneDiagramEditor) {
			EROneDiagramEditor editor = (EROneDiagramEditor) activeEditor;
			outline.setCategory(editor.getDefaultEditDomain(), editor.getGraphicalViewer(), null, editor.getDefaultActionRegistry());
		} else {
			ERDiagramEditor editor = (ERDiagramEditor) activeEditor;
			outline.setCategory(editor.getDefaultEditDomain(), editor.getGraphicalViewer(), null, editor.getDefaultActionRegistry());
		}

		outline.createControl(treeArea);
		outline.update();


//		treeViewer.getControl()

//		outline.getViewer().expandAll();
//		outline.setSelect(false);
		outline.getViewer().getControl().setForeground(foreground);
		outline.getViewer().getControl().setBackground(background);
		outline.getViewer().getControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.CR){
					selectAndDispose();
				}
			}
		});

		TreeViewer treeViewer = (TreeViewer) outline.getViewer();
		Tree tree = (Tree) treeViewer.getControl();
		expand(tree.getItems());

//		outline.getViewer().addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				selectAndDispose();
//			}
//		});
	}

	private void selectAndDispose(){
		outline.selectSelection();
		dispose();
	}

	private void expand(TreeItem[] items) {
		for (int i = 0; i < items.length; i++) {
			expand(items[i].getItems());
			items[i].setExpanded(true);
		}
	}

	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		search.setFocus();
	}

	public boolean hasContents() {
		return true;
	}

}
