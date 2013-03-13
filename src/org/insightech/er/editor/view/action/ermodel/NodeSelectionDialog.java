package org.insightech.er.editor.view.action.ermodel;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class NodeSelectionDialog extends FilteredItemsSelectionDialog {

//	private ItemsFilter itemsFilter = new ItemsFilter() {
//		@Override
//		public boolean matchItem(Object item) {
//			System.out.println("matchItem");
//			return true;
//		}
//		
//		@Override
//		public boolean isConsistentItem(Object item) {
//			System.out.println("isConsistentItem");
//			return true;
//		}
//	};
	
	private ERDiagram diagram;

	public NodeSelectionDialog(Shell shell, ERDiagram diagram) {
		super(shell);
		this.diagram = diagram;
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		// TODO Auto-generated method stub
		System.out.println("createExtendedContentArea");
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings result = new DialogSettings("NodeSelectionDialog"); //$NON-NLS-1$
		return result;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	@Override
	protected ItemsFilter createFilter() {
		System.out.println("createFilter");
		return new ItemsFilter() {
			@Override
			public boolean matchItem(Object item) {
				if (item instanceof ERTable) {
					ERTable table = (ERTable) item;
//					System.out.println(table.getPhysicalName());
					return this.patternMatcher.matches(table.getPhysicalName());
				}
				System.out.println("matchItem");
				return false;
//				return true;
			}
			
			@Override
			public boolean isConsistentItem(Object item) {
				System.out.println("isConsistentItem");
				return true;
			}
		};
	}

	@Override
	protected Comparator getItemsComparator() {
		return new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	@Override
	protected void applyFilter() {
		super.applyFilter();
	}
	
	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		for (ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {
			if (itemsFilter.matchItem(table)) {
				contentProvider.add(table, itemsFilter);
			}
		}
	}

	@Override
	public String getElementName(Object item) {
//		System.out.println("getElementName");
		if (item instanceof ERTable) {
			ERTable table = (ERTable) item;
			return table.getLogicalName();
		}
		return null;
	}

}
