package org.insightech.er.editor.view.action.dbimport;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbimport.DBObjectSet;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.view.dialog.dbimport.AbstractSelectImportedObjectDialog;
import org.insightech.er.editor.view.dialog.dbimport.ImportDBSettingDialog;
import org.insightech.er.editor.view.dialog.dbimport.SelectImportedObjectFromDBDialog;
import org.insightech.er.editor.view.dialog.dbimport.SelectImportedSchemaDialog;

public class ImportFromDBAction extends AbstractImportAction {

	public static final String ID = ImportFromDBAction.class.getName();

	private DBSetting dbSetting;

	public ImportFromDBAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.import.db"),
				editor);
		this
				.setImageDescriptor(Activator
						.getImageDescriptor(ImageKey.DATABASE));
	}

	protected AbstractSelectImportedObjectDialog createSelectImportedObjectDialog(
			DBObjectSet dbObjectSet) {
		ERDiagram diagram = this.getDiagram();

		return new SelectImportedObjectFromDBDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), diagram, dbObjectSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) throws Exception {
		ERDiagram diagram = this.getDiagram();

		int step = 0;
		int dialogResult = -1;

		List<String> selectedSchemaList = new ArrayList<String>();
		AbstractSelectImportedObjectDialog importDialog = null;

		while (true) {
			if (step == -1) {
				break;

			} else if (step == 0) {
				// �ڑ��̐ݒ�
				ImportDBSettingDialog settingDialog = new ImportDBSettingDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), diagram);
				dialogResult = settingDialog.open();

				this.dbSetting = settingDialog.getDbSetting();

			} else {
				DBManager manager = DBManagerFactory
						.getDBManager(this.dbSetting.getDbsystem());

				Connection con = null;

				try {
					con = dbSetting.connect();

					if (step == 1) {
						// �X�L�[�}�̑I��
						List<String> schemaList = manager
								.getImportSchemaList(con);

						if (!schemaList.isEmpty()) {
							SelectImportedSchemaDialog selectDialog = new SelectImportedSchemaDialog(
									PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow()
											.getShell(), diagram,
									this.dbSetting.getDbsystem(), schemaList,
									selectedSchemaList);

							dialogResult = selectDialog.open();

							selectedSchemaList = selectDialog
									.getSelectedSchemas();
						}

					} else if (step == 2) {
						// �I�u�W�F�N�g�̑I��
						PreImportFromDBManager preTableImportManager = manager
								.getPreTableImportManager();
						preTableImportManager.init(con, this.dbSetting,
								diagram, selectedSchemaList);
						preTableImportManager.run();

						Exception e = preTableImportManager.getException();
						if (e != null) {
							Activator.showMessageDialog(e.getMessage());
							throw new InputException("error.jdbc.version");

						}

						DBObjectSet dbObjectSet = preTableImportManager
								.getImportObjects();

						importDialog = this
								.createSelectImportedObjectDialog(dbObjectSet);

						dialogResult = importDialog.open();

					} else if (step == 3) {
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell());
						ImportFromDBManagerBase tableImportManager = (ImportFromDBManagerBase) manager
								.getTableImportManager();
						tableImportManager.init(con, this.dbSetting, diagram,
								importDialog.getSelectedDbObjects(),
								importDialog.isUseCommentAsLogicalName(),
								importDialog.isMergeWord());

						try {
							dialog.run(true, true, tableImportManager);

							Exception e1 = tableImportManager.getException();
							if (e1 != null) {
								throw e1;

							} else {
								this.importedNodeElements = new ArrayList<NodeElement>();

								this.importedNodeElements
										.addAll(tableImportManager
												.getImportedTables());
								this.importedNodeElements
										.addAll(tableImportManager
												.getImportedViews());
								this.importedSequences = tableImportManager
										.getImportedSequences();
								this.importedTriggers = tableImportManager
										.getImportedTriggers();
								this.importedTablespaces = tableImportManager
										.getImportedTablespaces();
							}

						} catch (InvocationTargetException e1) {
							Activator.showExceptionDialog(e1);
						} catch (InterruptedException e1) {
							Activator.showExceptionDialog(e1);
						}

						this.showData();

						break;
					}

				} finally {
					if (con != null) {
						con.close();
					}
				}
			}

			if (dialogResult == IDialogConstants.OK_ID) {
				step++;

			} else if (dialogResult == IDialogConstants.BACK_ID) {
				step--;

			} else {
				step = -1;
			}
		}
	}
}
