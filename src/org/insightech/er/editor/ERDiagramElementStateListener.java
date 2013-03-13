package org.insightech.er.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.insightech.er.Activator;

public class ERDiagramElementStateListener implements IElementStateListener {

	private IDocumentProvider documentProvider;

	private ERDiagramMultiPageEditor editorPart;

	public ERDiagramElementStateListener(ERDiagramMultiPageEditor editorPart) {
		this.editorPart = editorPart;

		documentProvider = DocumentProviderRegistry.getDefault()
				.getDocumentProvider(editorPart.getEditorInput());

		documentProvider.addElementStateListener(this);

		try {
			documentProvider.connect(editorPart.getEditorInput());
		} catch (CoreException e) {
			Activator.showExceptionDialog(e);
		}
	}

	public void elementDirtyStateChanged(Object element, boolean isDirty) {
	}

	public void elementContentAboutToBeReplaced(Object element) {
	}

	public void elementContentReplaced(Object element) {
	}

	public void elementDeleted(Object deletedElement) {
		if (deletedElement != null
				&& deletedElement.equals(editorPart.getEditorInput())) {
			Runnable r = new Runnable() {
				public void run() {
					close(false);
				}
			};
			execute(r, false);
		}
	}

	public void elementMoved(final Object originalElement,
			final Object movedElement) {
		if (originalElement != null
				&& originalElement.equals(editorPart.getEditorInput())) {
			final boolean doValidationAsync = Display.getCurrent() != null;
			Runnable r = new Runnable() {
				public void run() {
					if (movedElement == null
							|| movedElement instanceof IEditorInput) {

						final String previousContent;
						IDocumentUndoManager previousUndoManager = null;
						IDocument changed = null;
						boolean wasDirty = editorPart.isDirty();
						changed = documentProvider.getDocument(editorPart
								.getEditorInput());
						if (changed != null) {
							if (wasDirty)
								previousContent = changed.get();
							else
								previousContent = null;

							previousUndoManager = DocumentUndoManagerRegistry
									.getDocumentUndoManager(changed);
							if (previousUndoManager != null)
								previousUndoManager.connect(this);
						} else
							previousContent = null;

						editorPart
								.setInputWithNotify((IEditorInput) movedElement);

						if (previousUndoManager != null) {
							IDocument newDocument = documentProvider
									.getDocument(movedElement);
							if (newDocument != null) {
								IDocumentUndoManager newUndoManager = DocumentUndoManagerRegistry
										.getDocumentUndoManager(newDocument);
								if (newUndoManager != null)
									newUndoManager
											.transferUndoHistory(previousUndoManager);
							}
							previousUndoManager.disconnect(this);
						}

						if (wasDirty && changed != null) {
							Runnable r2 = new Runnable() {
								public void run() {
									documentProvider.getDocument(
											editorPart.getEditorInput()).set(
											previousContent);

								}
							};
							execute(r2, doValidationAsync);
						}

					}
				}
			};
			execute(r, false);
		}
	}

	private void execute(Runnable runnable, boolean postAsync) {
		if (postAsync || Display.getCurrent() == null) {
			this.editorPart.getSite().getShell().getDisplay().asyncExec(
					runnable);
		} else {
			runnable.run();
		}
	}

	public void close(final boolean save) {
		Display display = this.editorPart.getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				editorPart.getSite().getPage().closeEditor(editorPart, save);
			}
		});
	}

	protected void disposeDocumentProvider() {
		if (this.documentProvider != null) {

			IEditorInput input = editorPart.getEditorInput();
			if (input != null) {
				this.documentProvider.disconnect(input);
			}

			this.documentProvider.removeElementStateListener(this);
		}
		this.documentProvider = null;
	}

}