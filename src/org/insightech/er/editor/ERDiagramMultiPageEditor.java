package org.insightech.er.editor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.controller.command.category.ChangeCategoryNameCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.editor.view.dialog.category.CategoryNameChangeDialog;
import org.insightech.er.editor.view.outline.ERDiagramOutlinePage;
import org.insightech.er.util.Format;

/**
 * <pre>
 * エディタークラス
 * カテゴリー毎にタブ（ページ）を作成する
 * 各タブ（ページ）には、{@link ERDiagramEditor} を割り当てる
 * </pre>
 */
public class ERDiagramMultiPageEditor extends MultiPageEditorPart {

	private ERDiagram diagram;

	private ERDiagramEditPartFactory editPartFactory;

	private ZoomComboContributionItem zoomComboContributionItem;

	private ERDiagramOutlinePage outlinePage;

	private ERDiagramElementStateListener fElementStateListener;

	private boolean dirty;

	@Override
	public boolean isDirty() {
		if (this.dirty) {
			return true;
		}
		return super.isDirty();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createPages() {
		try {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			this.setPartName(file.getName());

			Persistent persistent = Persistent.getInstance();

			if (!file.isSynchronized(IResource.DEPTH_ONE)) {
				file.refreshLocal(IResource.DEPTH_ONE,
						new NullProgressMonitor());
			}

			InputStream in = file.getContents();

			System.out.println(new Date() + " : load start");
			this.diagram = persistent.load(in);
			System.out.println(new Date() + " : load end");

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

		System.out.println(new Date() + " : A");
		if (this.diagram == null) {
			this.diagram = new ERDiagram(DBManagerFactory.getAllDBList().get(0));
			this.diagram.init();
		}
		System.out.println(new Date() + " : B");

		this.diagram.getDiagramContents().getSettings().getTranslationSetting()
				.load();
		System.out.println(new Date() + " : C");

		this.diagram.setEditor(this);

		this.editPartFactory = new ERDiagramEditPartFactory();
		this.outlinePage = new ERDiagramOutlinePage(this.diagram);
		System.out.println(new Date() + " : D");

		try {
			this.zoomComboContributionItem = new ZoomComboContributionItem(this
					.getSite().getPage());

			ERDiagramEditor editor = new ERDiagramEditor(diagram,
					this.editPartFactory, zoomComboContributionItem,
					this.outlinePage);

			int index = this.addPage(editor, this.getEditorInput());
			this.setPageText(index,
					ResourceString.getResourceString("label.all"));
			
		} catch (PartInitException e) {
			Activator.showExceptionDialog(e);
		}

		System.out.println(new Date() + " : E");
		this.initCategoryPages(); // これが5秒掛かる

		System.out.println(new Date() + " : F");
		this.initStartPage(); // これが9秒掛かる

		System.out.println(new Date() + " : G");
		this.addMouseListenerToTabFolder();

		System.out.println(new Date() + " : H");
		this.validate();
		
		if (diagram.getCurrentErmodel() == null) {
			ERDiagramEditor diagramEditor = (ERDiagramEditor) this.getActiveEditor();
			diagramEditor.getGraphicalViewer().setContents(diagram);
		}
	}

	private void initStartPage() {
//		System.out.println(new Date() + " : F1");
//		int categoryIndex = this.diagram.getCurrentCategoryIndex();
//		System.out.println(new Date() + " : F2");
//		this.setActivePage(categoryIndex);
//
//		System.out.println(new Date() + " : F3");
//		if (categoryIndex > 0) {
//			this.pageChange(categoryIndex);
//		}
		
		ERModel model = diagram.getCurrentErmodel();
		if (model != null) {
			setActivePage(1);
		} else {
			setActivePage(0);
//			ERDiagramEditor diagramEditor = (ERDiagramEditor) this.getActiveEditor();
//			diagramEditor.getGraphicalViewer().setContents(diagram);
		}

		ERDiagramEditor activeEditor = (ERDiagramEditor) this.getActiveEditor();
		ZoomManager zoomManager = (ZoomManager) activeEditor
				.getAdapter(ZoomManager.class);
		zoomManager.setZoom(this.diagram.getZoom());

		activeEditor.setLocation(this.diagram.getX(), this.diagram.getY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createPageContainer(Composite parent) {
		try {
			IWorkbenchPage page = this.getSite().getWorkbenchWindow()
					.getActivePage();

			if (page != null) {
				page.showView(IPageLayout.ID_OUTLINE);
			}

		} catch (PartInitException e) {
			Activator.showExceptionDialog(e);
		}

		return super.createPageContainer(parent);
	}

	public void initCategoryPages() {
		
		String modelName = diagram.getDefaultModelName();
		if (modelName != null) {
			try {
				ERModel model = diagram.getDiagramContents().getModelSet().getModel(modelName);
				diagram.setCurrentErmodel(model, model.getName());
				EROneDiagramEditor modelEditor = new EROneDiagramEditor(
						this.diagram, model, this.editPartFactory,
						this.zoomComboContributionItem, this.outlinePage);

				int pageNo = this.addPage(modelEditor, this.getEditorInput());
				this.setPageText(pageNo, Format.null2blank(model.getName()));
				
//				IServiceLocator pageSite = getPageSite(pageNo);
//				System.out.println(pageSite);
//				MultiPageEditorSite site = ((IMultiPageEditorSiteHolder)pageSite.getService(IMultiPageEditorSiteHolder.class)).getSite();
//				MenuManager menuManager = new MenuManager();
				

			} catch (PartInitException e) {
				Activator.showExceptionDialog(e);
			}
			
		}
		
		// カテゴリ表示は無くす
//		CategorySetting categorySettings = this.diagram.getDiagramContents()
//				.getSettings().getCategorySetting();
//		System.out.println(new Date() + " : E1");
//
//		List<Category> selectedCategories = categorySettings
//				.getSelectedCategories();
//		System.out.println(new Date() + " : E2");
//
//		if (this.getPageCount() > selectedCategories.size() + 1) {
//			while (this.getPageCount() > selectedCategories.size() + 1) {
//				IEditorPart editorPart = this.getEditor(selectedCategories
//						.size() + 1);
//				editorPart.dispose();
//				this.removePage(selectedCategories.size() + 1);
//			}
//		}
//		System.out.println(new Date() + " : E3");
//
//		try {
//			for (int i = 1; i < this.getPageCount(); i++) {
//				Category category = selectedCategories.get(i - 1);
//				this.setPageText(i, Format.null2blank(category.getName()));
//			}
//			System.out.println(new Date() + " : E4");
//
//			for (int i = this.getPageCount(); i < selectedCategories.size() + 1; i++) {
//				Category category = selectedCategories.get(i - 1);
//
//				ERDiagramEditor diagramEditor = new ERDiagramEditor(
//						this.diagram, this.editPartFactory,
//						this.zoomComboContributionItem, this.outlinePage);
//
//				this.addPage(diagramEditor, this.getEditorInput());
//				this.setPageText(i, Format.null2blank(category.getName()));
//			}
//
//		} catch (PartInitException e) {
//			Activator.showExceptionDialog(e);
//		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		monitor.setTaskName("save initialize...");
		ZoomManager zoomManager = (ZoomManager) this.getActiveEditor()
				.getAdapter(ZoomManager.class);
		double zoom = zoomManager.getZoom();
		this.diagram.setZoom(zoom);

		ERDiagramEditor activeEditor = (ERDiagramEditor) this.getActiveEditor();
		Point location = activeEditor.getLocation();
		this.diagram.setLocation(location.x, location.y);

		Persistent persistent = Persistent.getInstance();

		IFile file = ((IFileEditorInput) this.getEditorInput()).getFile();

		try {
			monitor.setTaskName("create stream...");
			diagram.getDiagramContents().getSettings().getModelProperties()
					.setUpdatedDate(new Date());

			InputStream source = persistent.createInputStream(this.diagram);

			if (!file.exists()) {
				file.create(source, true, monitor);

			} else {
				file.setContents(source, true, false, monitor);
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

		monitor.beginTask("saving...", this.getPageCount());
		for (int i = 0; i < this.getPageCount(); i++) {
			IEditorPart editor = this.getEditor(i);
			editor.doSave(monitor);
			monitor.worked(i + 1);
		}
		monitor.done();
		monitor.setTaskName("finalize...");

		validate();
		monitor.done();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSaveAs() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		for (int i = 0; i < this.getPageCount(); i++) {
			ERDiagramEditor editor = (ERDiagramEditor) this.getEditor(i);
			editor.removeSelection();
		}

		ERDiagramEditor selectedEditor = (ERDiagramEditor) this
				.getActiveEditor();
		selectedEditor.changeCategory();

		if (selectedEditor instanceof EROneDiagramEditor) {
			EROneDiagramEditor editor = (EROneDiagramEditor) selectedEditor;
			this.diagram.setCurrentErmodel(editor.getModel(), editor.getModel().getName());
		} else {
			this.diagram.setCurrentErmodel(null, null);
			this.diagram.changeAll();
//			Category category = this.getCurrentPageCategory();
//			this.diagram.setCurrentCategory(category, 0); // 常に全体表示（ここでアウトライン作成）
//			this.diagram.setCurrentErmodel(null, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEditorPart getActiveEditor() {
		return super.getActiveEditor();
	}

	public Category getCurrentPageCategory() {
//		List<Category> categories = diagram.getDiagramContents().getSettings()
//				.getCategorySetting().getSelectedCategories();

		return null; // カテゴリの概念は無くす
//		int page = this.getActivePage();
//
//		if (page == 0) {
//			return null;
//		}
//
//		return categories.get(page - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.fElementStateListener = new ERDiagramElementStateListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.fElementStateListener.disposeDocumentProvider();
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setInputWithNotify(IEditorInput input) {
		super.setInputWithNotify(input);
	}

	private void validate() {
		final IFile file = ((IFileEditorInput) this.getEditorInput()).getFile();

		if (this.diagram.getDiagramContents().getSettings()
				.isSuspendValidator()) {
			try {
				file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
				Activator.showExceptionDialog(e);
			}

		} else {
			IWorkspaceRunnable editorMarker = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					ERDiagramEditor editor = (ERDiagramEditor) getActiveEditor();

					file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
					editor.clearMarkedObject();

					Validator validator = new Validator();
					List<ValidateResult> errorList = validator
							.validate(diagram);

					for (ValidateResult error : errorList) {
						IMarker marker = file.createMarker(IMarker.PROBLEM);

						marker.setAttribute(IMarker.MESSAGE, error.getMessage());
						marker.setAttribute(IMarker.TRANSIENT, true);
						marker.setAttribute(IMarker.LOCATION,
								error.getLocation());
						marker.setAttribute(IMarker.SEVERITY,
								error.getSeverity());
						editor.setMarkedObject(marker, error.getObject());
					}

					List<ValidateResult> todoList = validateTodo();

					for (ValidateResult todo : todoList) {
						IMarker marker = file.createMarker(IMarker.TASK);

						marker.setAttribute(IMarker.MESSAGE, todo.getMessage());
						marker.setAttribute(IMarker.TRANSIENT, true);
						marker.setAttribute(IMarker.LOCATION,
								todo.getLocation());
						marker.setAttribute(IMarker.SEVERITY,
								todo.getSeverity());
						editor.setMarkedObject(marker, todo.getObject());
					}
				}
			};
			try {
				ResourcesPlugin.getWorkspace().run(editorMarker, null);
			} catch (CoreException e) {
				Activator.showExceptionDialog(e);
			}
		}
	}

	private List<ValidateResult> validateTodo() {
		List<ValidateResult> resultList = new ArrayList<ValidateResult>();

		for (ERTable table : this.diagram.getDiagramContents().getContents()
				.getTableSet()) {

			String description = table.getDescription();
			resultList.addAll(this.createTodo(description,
					table.getLogicalName(), table));

			for (NormalColumn column : table.getNormalColumns()) {
				description = column.getDescription();
				resultList.addAll(this.createTodo(description,
						table.getLogicalName(), table));
			}

			for (Index index : table.getIndexes()) {
				description = index.getDescription();
				resultList.addAll(this.createTodo(description, index.getName(),
						index));
			}
		}

		for (View view : this.diagram.getDiagramContents().getContents()
				.getViewSet().getList()) {

			String description = view.getDescription();
			resultList
					.addAll(this.createTodo(description, view.getName(), view));

			for (NormalColumn column : view.getNormalColumns()) {
				description = column.getDescription();
				resultList.addAll(this.createTodo(description,
						view.getLogicalName(), view));
			}
		}

		for (Trigger trigger : this.diagram.getDiagramContents()
				.getTriggerSet().getTriggerList()) {

			String description = trigger.getDescription();
			resultList.addAll(this.createTodo(description, trigger.getName(),
					trigger));
		}

		for (Sequence sequence : this.diagram.getDiagramContents()
				.getSequenceSet().getSequenceList()) {

			String description = sequence.getDescription();
			resultList.addAll(this.createTodo(description, sequence.getName(),
					sequence));
		}

		return resultList;
	}

	private List<ValidateResult> createTodo(String description,
			String location, Object object) {
		List<ValidateResult> resultList = new ArrayList<ValidateResult>();

		if (description != null) {
			StringTokenizer tokenizer = new StringTokenizer(description, "\n\r");

			while (tokenizer.hasMoreElements()) {
				String token = tokenizer.nextToken();
				int startIndex = token.indexOf("// TODO");

				if (startIndex != -1) {
					String message = token.substring(
							startIndex + "// TODO".length()).trim();

					ValidateResult result = new ValidateResult();

					result.setLocation(location);
					result.setMessage(message);
					result.setObject(object);

					resultList.add(result);
				}
			}
		}

		return resultList;
	}
	
//	@Override
//	protected void setActivePage(int pageIndex) {
//		System.out.println("setActivePage : " + pageIndex);
//		viewer.setContents(diagram); // とりあえずOFFってみる
//
//		// TODO Auto-generated method stub
//		super.setActivePage(pageIndex);
//	}

	public void setCurrentCategoryPageName() {
		Category category = this.getCurrentPageCategory();
		this.setPageText(this.getActivePage(),
				Format.null2blank(category.getName()));
	}

	private void addMouseListenerToTabFolder() {
		CTabFolder tabFolder = (CTabFolder) this.getContainer();

		tabFolder.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent mouseevent) {
				Category category = getCurrentPageCategory();

				if (category != null) {
					CategoryNameChangeDialog dialog = new CategoryNameChangeDialog(
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							category);

					if (dialog.open() == IDialogConstants.OK_ID) {
						ChangeCategoryNameCommand command = new ChangeCategoryNameCommand(
								diagram, category, dialog.getCategoryName());
						execute(command);
					}
				}

				super.mouseDoubleClick(mouseevent);
			}
		});
	}

	private void execute(Command command) {
		ERDiagramEditor selectedEditor = (ERDiagramEditor) this
				.getActiveEditor();

		selectedEditor.getGraphicalViewer().getEditDomain().getCommandStack()
				.execute(command);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(Class type) {
		if (type == ERDiagram.class) {
			return this.diagram;
		}

		return super.getAdapter(type);
	}

	/**
	 * editPartFactoryを取得します。
	 * 
	 * @return editPartFactory
	 */
	public ERDiagramEditPartFactory getEditPartFactory() {
		return editPartFactory;
	}

	/**
	 * zoomComboContributionItemを取得します。
	 * 
	 * @return zoomComboContributionItem
	 */
	public ZoomComboContributionItem getZoomComboContributionItem() {
		return zoomComboContributionItem;
	}

	/**
	 * outlinePageを取得します。
	 * 
	 * @return outlinePage
	 */
	public ERDiagramOutlinePage getOutlinePage() {
		return outlinePage;
	}

	public int addPage(IEditorPart editor, IEditorInput input, String name)
			throws PartInitException {
		int pageNo = super.addPage(editor, input);
		setPageText(pageNo, Format.null2blank(name));
		return pageNo;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void setCurrentErmodel(ERModel model) {
		// とりあえずはエディタは2タブ固定で、pageNo=0 が全体、=1がビュー（ERModel）とする

		if (getPageCount() == 1) {
			// 1つだけの場合は、新しくエディタを作成する
			EROneDiagramEditor diagramEditor = new EROneDiagramEditor(
					this.diagram, model, getEditPartFactory(),
					getZoomComboContributionItem(), getOutlinePage());

			try {
				addPage(diagramEditor, getEditorInput(), model.getName());
				setActiveEditor(diagramEditor);

			} catch (PartInitException e) {
				Activator.showExceptionDialog(e);
			}
		} else {
			EROneDiagramEditor diagramEditor = (EROneDiagramEditor) getEditor(1);
			setPageText(1, Format.null2blank(model.getName()));
			diagramEditor.setContents(model);
			model.getDiagram().setCurrentErmodel(model, model.getName());
			setActiveEditor(diagramEditor);
		}
		
	}

	public void initGroupPages() {
		// TODO Auto-generated method stub
		
	}

}
