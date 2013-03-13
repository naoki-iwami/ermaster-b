package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.image.ChangeInsertedImagePropertyCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.view.dialog.element.InsertedImageDialog;
import org.insightech.er.editor.view.figure.InsertedImageFigure;

public class InsertedImageEditPart extends NodeElementEditPart implements
		IResizable {

	private Image image;

	private ImageData imageData;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		InsertedImage model = (InsertedImage) this.getModel();

		byte[] data = Base64.decodeBase64((model.getBase64EncodedData()
				.getBytes()));
		ByteArrayInputStream in = new ByteArrayInputStream(data);

		this.imageData = new ImageData(in);
		this.changeImage();

		InsertedImageFigure figure = new InsertedImageFigure(this.image, model
				.isFixAspectRatio(), model.getAlpha());
		figure.setMinimumSize(new Dimension(1, 1));

		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void disposeFont() {
		if (this.image != null && !this.image.isDisposed()) {
			this.image.dispose();
		}
		super.disposeFont();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());

		super.createEditPolicies();
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(InsertedImage.PROPERTY_CHANGE_IMAGE)) {
			changeImage();

			InsertedImageFigure figure = (InsertedImageFigure) this.getFigure();
			InsertedImage model = (InsertedImage) this.getModel();

			figure.setImg(this.image, model.isFixAspectRatio(), model
					.getAlpha());

			this.refreshVisuals();

			if (ERDiagramEditPart.isUpdateable()) {
				this.getFigure().repaint();
			}
		}

		super.doPropertyChange(event);
	}

	private void changeImage() {
		InsertedImage model = (InsertedImage) this.getModel();

		ImageData newImageData = new ImageData(this.imageData.width,
				this.imageData.height, this.imageData.depth,
				this.imageData.palette);

		for (int x = 0; x < this.imageData.width; x++) {
			for (int y = 0; y < this.imageData.height; y++) {
				RGB rgb = this.imageData.palette.getRGB(this.imageData
						.getPixel(x, y));
				float[] hsb = rgb.getHSB();

				if (model.getHue() != 0) {
					hsb[0] = model.getHue() & 360;
				}

				hsb[1] = hsb[1] + (model.getSaturation() / 100f);
				if (hsb[1] > 1.0f) {
					hsb[1] = 1.0f;
				} else if (hsb[1] < 0) {
					hsb[1] = 0f;
				}

				hsb[2] = hsb[2] + (model.getBrightness() / 100f);
				if (hsb[2] > 1.0f) {
					hsb[2] = 1.0f;

				} else if (hsb[2] < 0) {
					hsb[2] = 0f;
				}

				RGB newRGB = new RGB(hsb[0], hsb[1], hsb[2]);

				int pixel = imageData.palette.getPixel(newRGB);

				newImageData.setPixel(x, y, pixel);
			}
		}

		if (this.image != null && !this.image.isDisposed()) {
			this.image.dispose();
		}

		this.image = new Image(Display.getDefault(), newImageData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequestOpen() {
		InsertedImage insertedImage = (InsertedImage) this.getModel();

		InsertedImage oldInsertedImage = (InsertedImage) insertedImage.clone();

		ERDiagram diagram = this.getDiagram();

		InsertedImageDialog dialog = new InsertedImageDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				insertedImage);

		if (dialog.open() == IDialogConstants.OK_ID) {
			ChangeInsertedImagePropertyCommand command = new ChangeInsertedImagePropertyCommand(
					diagram, insertedImage, dialog.getNewInsertedImage(),
					oldInsertedImage);

			this.executeCommand(command);

		} else {
			ChangeInsertedImagePropertyCommand command = new ChangeInsertedImagePropertyCommand(
					diagram, insertedImage, oldInsertedImage, oldInsertedImage);
			command.execute();

		}
	}
}
