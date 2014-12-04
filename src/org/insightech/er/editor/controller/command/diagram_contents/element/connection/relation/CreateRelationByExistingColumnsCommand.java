package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.element.relation.RelationByExistingColumnsDialog;

/**
 * @author who?
 * @author jflute
 */
public class CreateRelationByExistingColumnsCommand extends
		AbstractCreateRelationCommand {

	private Relation relation;

	private List<NormalColumn> referencedColumnList;

	private List<NormalColumn> foreignKeyColumnList;

	private List<Word> wordList;

	public CreateRelationByExistingColumnsCommand() {
		super();
		this.wordList = new ArrayList<Word>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		ERTable sourceTable = (ERTable) this.source.getModel();
		TableView targetTable = (TableView) this.target.getModel();

		if (sourceTable instanceof ERVirtualTable) {
			sourceTable = ((ERVirtualTable)sourceTable).getRawTable();
		}
		if (targetTable instanceof ERVirtualTable) {
			targetTable = ((ERVirtualTable)targetTable).getRawTable();
		}

		this.relation.setSource(sourceTable);
		this.relation.setTargetWithoutForeignKey(targetTable);

		for (int i = 0; i < foreignKeyColumnList.size(); i++) {
			NormalColumn foreignKeyColumn = foreignKeyColumnList.get(i);
			this.wordList.add(foreignKeyColumn.getWord());

			sourceTable.getDiagram().getDiagramContents().getDictionary()
					.remove(foreignKeyColumn);

			foreignKeyColumn.addReference(referencedColumnList.get(i),
					this.relation);
			foreignKeyColumn.setWord(null);
		}

		if (this.relation.getSource() instanceof ERTable
				|| this.relation.getTarget() instanceof ERTable) {
			// ビュー内でリレーションを消した場合、ここにはERVirtualTableでなくERTableで来る
			ERModelSet modelSet = this.relation.getSource().getDiagram().getDiagramContents().getModelSet();
			modelSet.createRelation(relation);
		}

		targetTable.setDirty();

		ERModelUtil.refreshDiagram(relation.getSource().getDiagram(), sourceTable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		ERTable sourceTable = (ERTable) source.getModel();
		ERTable targetTable = (ERTable) target.getModel();

		this.relation.setSource(null);
		this.relation.setTargetWithoutForeignKey(null);

		for (int i = 0; i < foreignKeyColumnList.size(); i++) {
			NormalColumn foreignKeyColumn = foreignKeyColumnList.get(i);
			foreignKeyColumn.removeReference(this.relation);
			foreignKeyColumn.setWord(wordList.get(i));

			sourceTable.getDiagram().getDiagramContents().getDictionary().add(
					foreignKeyColumn);
		}

		targetTable.setDirty();
	}

	public boolean selectColumns() {
		if (this.target == null) {
			return false;
		}

		ERTable sourceTable = (ERTable) this.source.getModel();
		TableView targetTable = (TableView) this.target.getModel();

		Map<NormalColumn, List<NormalColumn>> referencedMap = new HashMap<NormalColumn, List<NormalColumn>>();
		Map<Relation, Set<NormalColumn>> foreignKeySetMap = new HashMap<Relation, Set<NormalColumn>>();

		for (NormalColumn normalColumn : targetTable.getNormalColumns()) {
			NormalColumn rootReferencedColumn = normalColumn.getRootReferencedColumn();
			if (rootReferencedColumn != null) {
				List<NormalColumn> foreignKeyList = referencedMap
						.get(rootReferencedColumn);

				if (foreignKeyList == null) {
					foreignKeyList = new ArrayList<NormalColumn>();
					referencedMap.put(rootReferencedColumn, foreignKeyList);
				}

				foreignKeyList.add(normalColumn);

				for (Relation relation : normalColumn.getRelationList()) {
					Set<NormalColumn> foreignKeySet = foreignKeySetMap
							.get(relation);
					if (foreignKeySet == null) {
						foreignKeySet = new HashSet<NormalColumn>();
						foreignKeySetMap.put(relation, foreignKeySet);
					}

					foreignKeySet.add(normalColumn);
				}
			}
		}

		List<NormalColumn> candidateForeignKeyColumns = new ArrayList<NormalColumn>();

		for (NormalColumn column : targetTable.getNormalColumns()) {
			if (!column.isForeignKey()) {
				candidateForeignKeyColumns.add(column);
			}
		}

		if (candidateForeignKeyColumns.isEmpty()) {
			Activator
					.showErrorDialog("error.no.candidate.of.foreign.key.exist");
			return false;
		}

		RelationByExistingColumnsDialog dialog = new RelationByExistingColumnsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				sourceTable, candidateForeignKeyColumns, referencedMap,
				foreignKeySetMap);

		if (dialog.open() == IDialogConstants.OK_ID) {
			this.relation = new Relation(dialog.isReferenceForPK(), dialog
					.getReferencedComplexUniqueKey(), dialog
					.getReferencedColumn());
			final String defaultName = prepareDefaultFKConstraintName(sourceTable, targetTable);
			if (defaultName != null) {
				this.relation.setName(defaultName);
			}
			this.referencedColumnList = dialog.getReferencedColumnList();
			this.foreignKeyColumnList = dialog.getForeignKeyColumnList();

		} else {
			return false;
		}

		return true;
	}

	protected String prepareDefaultFKConstraintName(ERTable sourceTable, TableView targetTable) {
		// MySQL only for now for quick implementation (2014/10/23)
		if (isDatabaseMySQL(targetTable)) {
			final String standardName = buildStandardFKConstraintName(sourceTable, targetTable);
			final int limitLength = 64; // where do I define it?
			return cutName(standardName, limitLength);
		}
		return null;
	}

	protected boolean isDatabaseMySQL(TableView targetTable) {
		return MySQLDBManager.ID.equals(targetTable.getDiagram().getDatabase()); // for now
	}

	protected String buildStandardFKConstraintName(ERTable sourceTable, TableView targetTable) {
		return "FK_" + targetTable.getPhysicalName() + "_" + sourceTable.getPhysicalName();
	}
	
	protected String cutName(final String name, int limitLength) {
		return name.length() > limitLength ? name.substring(0, limitLength) : name;
	}
}
