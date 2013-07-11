package org.insightech.er.editor.view.outline;

import java.util.List;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.ermodel.OpenERModelCommand;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.outline.ERDiagramOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.ERDiagramOutlineEditPartFactory;
import org.insightech.er.editor.controller.editpart.outline.ermodel.ERModelOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.ermodel.ERModelSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.table.TableOutlineEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.action.outline.ChangeNameAction;
import org.insightech.er.editor.view.action.outline.index.CreateIndexAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToBothAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToLogicalAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToPhysicalAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByLogicalNameAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByPhysicalNameAction;
import org.insightech.er.editor.view.action.outline.sequence.CreateSequenceAction;
import org.insightech.er.editor.view.action.outline.tablespace.CreateTablespaceAction;
import org.insightech.er.editor.view.action.outline.trigger.CreateTriggerAction;
import org.insightech.er.editor.view.drag_drop.ERDiagramOutlineTransferDropTargetListener;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class ERDiagramOutlinePage extends ContentOutlinePage {

	// ページをアウトラインとサムネイルに分離するコンポジット
	private SashForm sash;

	private TreeViewer viewer;

	private ERDiagram diagram;

	private LightweightSystem lws;

	private ScrollableThumbnail thumbnail;

	private GraphicalViewer graphicalViewer;

	private ActionRegistry outlineActionRegistory;

	private ActionRegistry registry;

	private boolean quickMode;

	private ERDiagramOutlineEditPartFactory editPartFactory;

	public ERDiagramOutlinePage(ERDiagram diagram) {
		// GEFツリービューワを使用する
		super(new TreeViewer());

		this.viewer = (TreeViewer) this.getViewer();
		this.diagram = diagram;

		this.outlineActionRegistory = new ActionRegistry();
		this.registerAction(this.viewer, outlineActionRegistory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		this.sash = new SashForm(parent, SWT.VERTICAL);

		// コンストラクタで指定したビューワの作成
		this.viewer.createControl(this.sash);

		editPartFactory = new ERDiagramOutlineEditPartFactory();
		editPartFactory.setQuickMode(quickMode);

		this.viewer.setEditPartFactory(editPartFactory);

		// グラフィカル・エディタのルート・モデルをツリー・ビューワにも設定
		this.viewer.setContents(this.diagram);

		if (!quickMode) {
			Canvas canvas = new Canvas(this.sash, SWT.BORDER);
			// サムネイル・フィギュアを配置する為の LightweightSystem
			this.lws = new LightweightSystem(canvas);
		}

		this.resetView(this.registry);

		AbstractTransferDragSourceListener dragSourceListener = new ERDiagramTransferDragSourceListener(
				this.viewer, TemplateTransfer.getInstance());
		this.viewer.addDragSourceListener(dragSourceListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control getControl() {
		// アウトライン・ビューをアクティブにした時にフォーカスが設定されるコントロールを返す
		return sash;
	}

	private void showThumbnail() {
		if (quickMode) {
			return;
		}
		// RootEditPartのビューをソースとしてサムネイルを作成
		ScalableFreeformRootEditPart editPart = (ScalableFreeformRootEditPart) this.graphicalViewer
				.getRootEditPart();

		if (this.thumbnail != null) {
			this.thumbnail.deactivate();
		}

		this.thumbnail = new ScrollableThumbnail((Viewport) editPart
				.getFigure());
		this.thumbnail.setSource(editPart
				.getLayer(LayerConstants.PRINTABLE_LAYERS));

		this.lws.setContents(this.thumbnail);

	}

	private void initDropTarget() {
		AbstractTransferDropTargetListener dropTargetListener = new ERDiagramOutlineTransferDropTargetListener(
				this.graphicalViewer, TemplateTransfer.getInstance());

		this.graphicalViewer.addDropTargetListener(dropTargetListener);
	}

	public void setCategory(EditDomain editDomain,
			GraphicalViewer graphicalViewer, MenuManager outlineMenuMgr,
			ActionRegistry registry) {
		this.graphicalViewer = graphicalViewer;
		this.viewer.setContextMenu(outlineMenuMgr);

		// エディット・ドメインの設定
		this.viewer.setEditDomain(editDomain);
		this.registry = registry;

		if (this.getSite() != null) {
			this.resetView(registry);
		}
	}

	private void resetAction(ActionRegistry registry) {
		// アウトライン・ページで有効にするアクション
		if (getSite() == null) {
			return;
		}
		IActionBars bars = this.getSite().getActionBars();

		String id = ActionFactory.UNDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		id = ActionFactory.REDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		id = ActionFactory.DELETE.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		bars.updateActionBars();
	}

	private void resetView(ActionRegistry registry) {
		this.showThumbnail();
		this.initDropTarget();
		this.resetAction(registry);
	}

	private void registerAction(TreeViewer treeViewer,
			ActionRegistry actionRegistry) {
		IAction[] actions = { new CreateIndexAction(treeViewer),
				new CreateSequenceAction(treeViewer),
				new CreateTriggerAction(treeViewer),
				new CreateTablespaceAction(treeViewer),
				new ChangeOutlineViewToPhysicalAction(treeViewer),
				new ChangeOutlineViewToLogicalAction(treeViewer),
				new ChangeOutlineViewToBothAction(treeViewer),
				new ChangeOutlineViewOrderByPhysicalNameAction(treeViewer),
				new ChangeOutlineViewOrderByLogicalNameAction(treeViewer),
				new ChangeNameAction(treeViewer),
		};

		for (IAction action : actions) {
			actionRegistry.registerAction(action);
		}
	}

	public ActionRegistry getOutlineActionRegistory() {
		return outlineActionRegistory;
	}

	@Override
	public EditPartViewer getViewer() {
		return super.getViewer();
	}

	public void update() {
		viewer.flush();
//		gettr
//		if (model != null) {
//			try {
//				model.update(editor.getDocumentProvider()
//						.getDocument(editor.getEditorInput()).get());
//			} catch (Throwable t) {
//				t.printStackTrace();
//			}
//		}
	}

	public void setFilterText(String filterText) {
		editPartFactory.setFilterText(filterText);
		viewer.setContents(diagram);
		Tree tree = (Tree)viewer.getControl();
		TreeItem[] items = tree.getItems();
		expand(items);
		TreeItem[] tableItems = items[0].getItems();
		if (tableItems.length >= 1) {
			tree.setSelection(tableItems[0]);
		}
//		viewer.getContents().getChildren();



//		viewer.flush();
//		viewer.getEditPartFactory()
//		if (filterText == null) {
//			filterText = "";
//		}
//		this.filterText = filterText;
//		getTreeViewer().refresh();
//		getTreeViewer().expandAll();
//		JavaScriptElement element = getFirstElement(model, filterText);
//		if(element != null){
//			getViewer().setSelection(new StructuredSelection(element), true);
//		}
	}

	private void expand(TreeItem[] items) {
		for (int i = 0; i < items.length; i++) {
			expand(items[i].getItems());
			items[i].setExpanded(true);
		}
	}

	/**
	 * quickModeを設定します。
	 * @param quickMode quickMode
	 */
	public void setQuickMode(boolean quickMode) {
	    this.quickMode = quickMode;
	}

	public void selectSelection() {
		IStructuredSelection sel = (IStructuredSelection) getViewer().getSelection();
		Object firstElement = sel.getFirstElement();
		if (firstElement instanceof ERDiagramOutlineEditPart) {
			Tree tree = (Tree)viewer.getControl();
			TreeItem[] items = tree.getItems();
			expand(items);
			TreeItem[] tableItems = items[0].getItems();
			if (tableItems.length >= 1) {
				Object data = tableItems[0].getData();
				firstElement = data;
			}
		}
		if (firstElement instanceof TableOutlineEditPart) {
			Object model = ((TableOutlineEditPart)firstElement).getModel();
			ERTable table = (ERTable) model;
			ERModel erModel = table.getDiagram().findModelByTable(table);
			if (erModel != null) {

				OpenERModelCommand command = new OpenERModelCommand(diagram, erModel);
				command.setTable(table);
				this.getViewer().getEditDomain().getCommandStack().execute(command);

				ERDiagramOutlineEditPart contents = (ERDiagramOutlineEditPart) diagram.getEditor().getOutlinePage().getViewer().getContents();
				if (contents != null) {
					List<ERModelOutlineEditPart> parts = ((ERModelSetOutlineEditPart) contents.getChildren().get(0)).getChildren();
					for (ERModelOutlineEditPart part : parts) {
						if (part.getModel().equals(erModel)) {
							ISelection selection = new StructuredSelection(part);
							diagram.getEditor().getOutlinePage().setSelection(selection);
						}
					}
				}

			} else {
				Activator.showMessageDialog(table.getPhysicalName() + " テーブルはそのダイアグラムにも配置されていません。");
			}

		}
	}

}
