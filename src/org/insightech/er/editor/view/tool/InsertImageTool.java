package org.insightech.er.editor.view.tool;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;

public class InsertImageTool extends CreationToolEntry {

	public InsertImageTool() {
		super(ResourceString.getResourceString("label.image.insert"), ResourceString
				.getResourceString("label.image.insert"), new SimpleFactory(
				InsertedImage.class), Activator
				.getImageDescriptor(ImageKey.IMAGE), Activator
				.getImageDescriptor(ImageKey.IMAGE));
	}

	@Override
	public Tool createTool() {
		InsertedImageTool tool = new InsertedImageTool();
		tool.setProperties(getToolProperties());

		return tool;
	}

	private class InsertedImageTool extends CreationTool {

		@Override
		protected void performCreation(int button) {
			String path = getLoadFilePath();

			if (path != null) {
				InsertedImage insertedImage = (InsertedImage) this
						.getCreateRequest().getNewObject();
				insertedImage.setImageFilePath(path);

				super.performCreation(button);
			}
		}

		private String getLoadFilePath() {

			FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.OPEN);

			String[] filterExtensions = { "*.bmp;*.jpg;*.jpeg;*.gif;*.png;*.tif;*.tiff" };

			fileDialog.setFilterExtensions(filterExtensions);

			return fileDialog.open();
		}

	}

}
