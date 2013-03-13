package org.insightech.er.editor.view.action.dbimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.DBObjectSet;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.edit.CopyManager;
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.editor.view.dialog.dbimport.AbstractSelectImportedObjectDialog;
import org.insightech.er.editor.view.dialog.dbimport.SelectImportedObjectFromFileDialog;

public class ImportFromFileAction extends AbstractImportAction {

	public static final String ID = ImportFromFileAction.class.getName();

	private ERDiagram loadedDiagram;

	public ImportFromFileAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.import.file"),
				editor);
		this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.TABLE));
	}

	protected DBObjectSet preImport() throws Exception {
		String fileName = this.getLoadFilePath(this.getEditorPart());
		if (fileName == null) {
			return null;
		}

		Persistent persistent = Persistent.getInstance();

		Path path = new Path(fileName);

		InputStream in = null;

		try {
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(path);
			
			if (file == null || !file.exists()) {
				File realFile = path.toFile();
				if (realFile == null || !realFile.exists()) {
					Activator.showErrorDialog("error.import.file");
					return null;
				}

				in = new FileInputStream(realFile);

			} else {
				if (!file.isSynchronized(IResource.DEPTH_ONE)) {
					file.refreshLocal(IResource.DEPTH_ONE,
							new NullProgressMonitor());
				}

				in = file.getContents();
			}

			this.loadedDiagram = persistent.load(in);

		} finally {
			in.close();
		}

		return this.getAllObjects(loadedDiagram);
	}

	protected AbstractSelectImportedObjectDialog createSelectImportedObjectDialog(
			DBObjectSet dbObjectSet) {
		ERDiagram diagram = this.getDiagram();

		return new SelectImportedObjectFromFileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), diagram, dbObjectSet);
	}

	protected String getLoadFilePath(IEditorPart editorPart) {

		IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();

		FileDialog fileDialog = new FileDialog(editorPart.getEditorSite()
				.getShell(), SWT.OPEN);

		IProject project = file.getProject();

		fileDialog.setFilterPath(project.getLocation().toString());

		String[] filterExtensions = this.getFilterExtensions();
		fileDialog.setFilterExtensions(filterExtensions);

		return fileDialog.open();
	}

	protected String[] getFilterExtensions() {
		return new String[] { "*.erm" };
	}

	private DBObjectSet getAllObjects(ERDiagram loadedDiagram) {
		DBObjectSet dbObjects = new DBObjectSet();

		for (ERTable table : loadedDiagram.getDiagramContents().getContents()
				.getTableSet()) {
			DBObject dbObject = new DBObject(table.getTableViewProperties()
					.getSchema(), table.getName(), DBObject.TYPE_TABLE);
			dbObject.setModel(table);
			dbObjects.add(dbObject);
		}

		for (View view : loadedDiagram.getDiagramContents().getContents()
				.getViewSet()) {
			DBObject dbObject = new DBObject(view.getTableViewProperties()
					.getSchema(), view.getName(), DBObject.TYPE_VIEW);
			dbObject.setModel(view);
			dbObjects.add(dbObject);
		}

//		for (Note note : loadedDiagram.getDiagramContents().getContents()
//				.getNoteSet()) {
//			DBObject dbObject = new DBObject(null, note.getName(),
//					DBObject.TYPE_NOTE);
//			dbObject.setModel(note);
//			dbObjects.add(dbObject);
//		}

		for (Sequence sequence : loadedDiagram.getDiagramContents()
				.getSequenceSet()) {
			DBObject dbObject = new DBObject(sequence.getSchema(), sequence
					.getName(), DBObject.TYPE_SEQUENCE);
			dbObject.setModel(sequence);
			dbObjects.add(dbObject);
		}

		for (Trigger trigger : loadedDiagram.getDiagramContents()
				.getTriggerSet()) {
			DBObject dbObject = new DBObject(trigger.getSchema(), trigger
					.getName(), DBObject.TYPE_TRIGGER);
			dbObject.setModel(trigger);
			dbObjects.add(dbObject);
		}

		for (Tablespace tablespace : loadedDiagram.getDiagramContents()
				.getTablespaceSet()) {
			DBObject dbObject = new DBObject(null, tablespace.getName(),
					DBObject.TYPE_TABLESPACE);
			dbObject.setModel(tablespace);
			dbObjects.add(dbObject);
		}

		for (ColumnGroup columnGroup : loadedDiagram.getDiagramContents()
				.getGroups()) {
			DBObject dbObject = new DBObject(null, columnGroup.getName(),
					DBObject.TYPE_GROUP);
			dbObject.setModel(columnGroup);
			dbObjects.add(dbObject);
		}

		return dbObjects;
	}

	protected void loadData(List<DBObject> selectedObjectList,
			boolean useCommentAsLogicalName, boolean mergeWord,
			boolean mergeGroup) {

		Set<AbstractModel> selectedSets = new HashSet<AbstractModel>();
		for (DBObject dbObject : selectedObjectList) {
			selectedSets.add(dbObject.getModel());
		}

		DiagramContents contents = loadedDiagram.getDiagramContents();

		GroupSet columnGroupSet = contents.getGroups();

		for (Iterator<ColumnGroup> iter = columnGroupSet.iterator(); iter
				.hasNext();) {
			ColumnGroup columnGroup = iter.next();

			if (!selectedSets.contains(columnGroup)) {
				iter.remove();
			}
		}

		this.importedColumnGroups = columnGroupSet.getGroupList();

		SequenceSet sequenceSet = contents.getSequenceSet();

		for (Iterator<Sequence> iter = sequenceSet.iterator(); iter.hasNext();) {
			Sequence sequence = iter.next();

			if (!selectedSets.contains(sequence)) {
				iter.remove();
			}
		}

		this.importedSequences = sequenceSet.getSequenceList();

		TriggerSet triggerSet = contents.getTriggerSet();

		for (Iterator<Trigger> iter = triggerSet.iterator(); iter.hasNext();) {
			Trigger trigger = iter.next();

			if (!selectedSets.contains(trigger)) {
				iter.remove();
			}
		}

		this.importedTriggers = triggerSet.getTriggerList();

		TablespaceSet tablespaceSet = contents.getTablespaceSet();

		for (Iterator<Tablespace> iter = tablespaceSet.iterator(); iter
				.hasNext();) {
			Tablespace tablespace = iter.next();

			if (!selectedSets.contains(tablespace)) {
				iter.remove();
			}
		}

		this.importedTablespaces = tablespaceSet.getTablespaceList();

		NodeSet nodeSet = contents.getContents();
		List<NodeElement> nodeElementList = nodeSet.getNodeElementList();

		for (Iterator<NodeElement> iter = nodeElementList.iterator(); iter
				.hasNext();) {
			NodeElement nodeElement = iter.next();

			if (!selectedSets.contains(nodeElement)) {
				iter.remove();
			}
		}

		NodeSet selectedNodeSet = new NodeSet();

		Map<UniqueWord, Word> dictionary = new HashMap<UniqueWord, Word>();

		if (mergeWord) {
			for (Word word : this.getDiagram().getDiagramContents()
					.getDictionary().getWordList()) {
				dictionary.put(new UniqueWord(word), word);
			}
		}

		for (NodeElement nodeElement : nodeElementList) {
			if (mergeWord) {
				if (nodeElement instanceof TableView) {
					TableView tableView = (TableView) nodeElement;

					for (NormalColumn normalColumn : tableView
							.getNormalColumns()) {
						Word word = normalColumn.getWord();
						if (word != null) {
							UniqueWord uniqueWord = new UniqueWord(word);
							Word replaceWord = dictionary.get(uniqueWord);

							if (replaceWord != null) {
								normalColumn.setWord(replaceWord);
							}
						}
					}
				}
			}

			selectedNodeSet.addNodeElement(nodeElement);
		}

		for (NodeElement nodeElement : selectedNodeSet) {
			if (nodeElement instanceof TableView) {
				TableView tableView = (TableView) nodeElement;

				for (Iterator<Column> iter = tableView.getColumns().iterator(); iter
						.hasNext();) {
					Column column = iter.next();

					if (column instanceof ColumnGroup) {
						if (!this.importedColumnGroups.contains(column)) {
							iter.remove();
						}
					}
				}
			}
		}

		if (mergeGroup) {
			Map<String, ColumnGroup> groupMap = new HashMap<String, ColumnGroup>();

			for (ColumnGroup columnGroup : this.getDiagram()
					.getDiagramContents().getGroups()) {
				groupMap.put(columnGroup.getGroupName(), columnGroup);
			}

			for (Iterator<ColumnGroup> iter = this.importedColumnGroups
					.iterator(); iter.hasNext();) {
				ColumnGroup columnGroup = iter.next();

				ColumnGroup replaceColumnGroup = groupMap.get(columnGroup
						.getGroupName());

				if (replaceColumnGroup != null) {
					iter.remove();

					for (NodeElement nodeElement : selectedNodeSet) {
						if (nodeElement instanceof TableView) {
							TableView tableView = (TableView) nodeElement;
							tableView.replaceColumnGroup(columnGroup,
									replaceColumnGroup);
						}
					}
				}
			}
		}

		CopyManager copyManager = new CopyManager();
		NodeSet copyList = copyManager.copyNodeElementList(selectedNodeSet);

		this.importedNodeElements = copyList.getNodeElementList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) throws Exception {
		DBObjectSet dbObjectSet = this.preImport();

		if (dbObjectSet != null) {
			AbstractSelectImportedObjectDialog importDialog = this
					.createSelectImportedObjectDialog(dbObjectSet);

			int result = importDialog.open();

			if (result == IDialogConstants.OK_ID) {
				this.loadData(importDialog.getSelectedDbObjects(), importDialog
						.isUseCommentAsLogicalName(), importDialog
						.isMergeWord(), importDialog.isMergeGroup());
				this.showData();

			} else if (result == IDialogConstants.BACK_ID) {
				this.execute(event);
			}
		}
	}
}
