package org.insightech.er.editor.controller.command.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.util.POIUtils;

public class EditExcelCommand extends AbstractCommand {

	private ViewableModel model;

	public EditExcelCommand(ViewableModel model) {
		this.model = model;
	}

	@Override
	protected void doExecute() {
		if (model instanceof ERVirtualTable) {
			ERVirtualTable vtable = (ERVirtualTable) model;
			String tableName = vtable.getRawTable().getPhysicalName();
			Settings settings = vtable.getDiagram().getDiagramContents().getSettings();
			String path = settings.getMasterDataBasePath();
			if (path != null) {
				IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(path));

				try {
					IResource[] members = folder.members();
					boolean hit = false;
					for (IResource excelFile : members) {
						String ext = excelFile.getFileExtension();
						if (ext == null) {
							continue;
						}
						if (!ext.equals("xls") && !ext.equals("xlsx")) {
							continue;
						}
//						Activator.log(new Exception(excelFile.getLocation().toFile().toString()));
						try {
							HSSFWorkbook book = POIUtils.readExcelBook(excelFile.getLocation().toFile());
							for (int i = 0; i < book.getNumberOfSheets(); i++) {
								String name = book.getSheetName(i);
								if (name.equalsIgnoreCase(tableName)) {
									IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
									IDE.openEditor(page, (IFile)excelFile);
									hit = true;
									break;
								}
							}
						} catch (Throwable e) {
							// たまにExcelが開けないことがあるが無視
						}
					}
					if (!hit) {
						if (Activator.showConfirmDialog(tableName + " テーブルのデータを記載したExcelが見つかりません。ディレクトリを開きますか？", SWT.OK, SWT.CANCEL)) {
							ERModelUtil.openDirectory(members[0]);
						}
					}

				} catch (CoreException e) {
					Activator.log(e);
				}

			}

		}
	}

	@Override
	protected void doUndo() {
		// do nothing
	}

}
