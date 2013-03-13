package org.insightech.er.editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.operations.IOperationApprover;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;
import org.eclipse.ui.texteditor.IDocumentProviderExtension2;
import org.eclipse.ui.texteditor.IDocumentProviderExtension3;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.IElementStateListenerExtension;
import org.insightech.er.Activator;
import org.osgi.framework.Bundle;

public class TestEditor extends EditorPart {

	class ElementStateListener implements IElementStateListener,
			IElementStateListenerExtension {

		class Validator implements VerifyListener {

			public void verifyText(VerifyEvent e) {
				IDocument document = getDocumentProvider().getDocument(
						getEditorInput());
				final boolean[] documentChanged = new boolean[1];
				IDocumentListener listener = new IDocumentListener() {
					public void documentAboutToBeChanged(DocumentEvent event) {
					}

					public void documentChanged(DocumentEvent event) {
						documentChanged[0] = true;
					}
				};
				try {
					if (document != null)
						document.addDocumentListener(listener);
					if (!validateEditorInputState() || documentChanged[0])
						e.doit = false;
				} finally {
					if (document != null)
						document.removeDocumentListener(listener);
				}
			}
		}

		private Validator fValidator;

		private Display fDisplay;

		public void elementStateValidationChanged(final Object element,
				final boolean isStateValidated) {
			if (element != null && element.equals(getEditorInput())) {
				Runnable r = new Runnable() {
					public void run() {
						enableSanityChecking(true);
						if (isStateValidated) {
							if (fValidator != null) {
								ISourceViewer viewer = fSourceViewer;
								if (viewer != null) {
									StyledText textWidget = viewer
											.getTextWidget();
									if (textWidget != null
											&& !textWidget.isDisposed())
										textWidget
												.removeVerifyListener(fValidator);
									fValidator = null;
								}
							}
							enableStateValidation(false);
						} else if (!isStateValidated && fValidator == null) {
							ISourceViewer viewer = fSourceViewer;
							if (viewer != null) {
								StyledText textWidget = viewer.getTextWidget();
								if (textWidget != null
										&& !textWidget.isDisposed()) {
									fValidator = new Validator();
									enableStateValidation(true);
									textWidget.addVerifyListener(fValidator);
								}
							}
						}
					}
				};
				execute(r, false);
			}
		}

		/*
		 * @see IElementStateListener#elementDirtyStateChanged(Object, boolean)
		 */
		public void elementDirtyStateChanged(Object element, boolean isDirty) {
			if (element != null && element.equals(getEditorInput())) {
				Runnable r = new Runnable() {
					public void run() {
						enableSanityChecking(true);
						firePropertyChange(PROP_DIRTY);
					}
				};
				execute(r, false);
			}
		}

		/*
		 * @see IElementStateListener#elementContentAboutToBeReplaced(Object)
		 */
		public void elementContentAboutToBeReplaced(Object element) {
			if (element != null && element.equals(getEditorInput())) {
				Runnable r = new Runnable() {
					public void run() {
						enableSanityChecking(true);
					}
				};
				execute(r, false);
			}
		}

		/*
		 * @see IElementStateListener#elementContentReplaced(Object)
		 */
		public void elementContentReplaced(Object element) {
			if (element != null && element.equals(getEditorInput())) {
				Runnable r = new Runnable() {
					public void run() {
						enableSanityChecking(true);
						firePropertyChange(PROP_DIRTY);
					}
				};
				execute(r, false);
			}
		}

		/*
		 * @see IElementStateListener#elementDeleted(Object)
		 */
		public void elementDeleted(Object deletedElement) {
			if (deletedElement != null
					&& deletedElement.equals(getEditorInput())) {
				Runnable r = new Runnable() {
					public void run() {
						enableSanityChecking(true);
						close(false);
					}
				};
				execute(r, false);
			}
		}

		/*
		 * @see IElementStateListener#elementMoved(Object, Object)
		 */
		public void elementMoved(final Object originalElement,
				final Object movedElement) {
			if (originalElement != null
					&& originalElement.equals(getEditorInput())) {
				final boolean doValidationAsync = Display.getCurrent() != null;
				Runnable r = new Runnable() {
					public void run() {
						enableSanityChecking(true);

						if (fSourceViewer == null)
							return;

						if (movedElement == null
								|| movedElement instanceof IEditorInput) {

							final IDocumentProvider d = getDocumentProvider();
							final String previousContent;
							IDocumentUndoManager previousUndoManager = null;
							IDocument changed = null;
							boolean wasDirty = isDirty();
							changed = d.getDocument(getEditorInput());
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

							setInput((IEditorInput) movedElement);

							// The undo manager needs to be replaced with one
							// for the new document.
							// Transfer the undo history and then disconnect
							// from the old undo manager.
							if (previousUndoManager != null) {
								IDocument newDocument = getDocumentProvider()
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
										validateState(getEditorInput());
										d.getDocument(getEditorInput()).set(
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

		/*
		 * @see IElementStateListenerExtension#elementStateChanging(Object)
		 * @since 2.0
		 */
		public void elementStateChanging(Object element) {
			if (element != null && element.equals(getEditorInput()))
				enableSanityChecking(false);
		}

		/*
		 * @see IElementStateListenerExtension#elementStateChangeFailed(Object)
		 * @since 2.0
		 */
		public void elementStateChangeFailed(Object element) {
			if (element != null && element.equals(getEditorInput()))
				enableSanityChecking(true);
		}

		private void execute(Runnable runnable, boolean postAsync) {
			if (postAsync || Display.getCurrent() == null) {
				if (fDisplay == null)
					fDisplay = getSite().getShell().getDisplay();
				fDisplay.asyncExec(runnable);
			} else
				runnable.run();
		}
	}

	private IDocumentProvider fImplicitDocumentProvider;
	private SourceViewerConfiguration fConfiguration;
	private ISourceViewer fSourceViewer;
	private Image fTitleImage;
	private IElementStateListener fElementStateListener = new ElementStateListener();
	private long fModificationStamp = -1;
	private boolean fIsSanityCheckEnabled = true;
	private boolean fIsStateValidationEnabled = true;
	private IOperationApprover fNonLocalOperationApprover;
	private IOperationApprover fLinearUndoViolationApprover;

	public IDocumentProvider getDocumentProvider() {
		return fImplicitDocumentProvider;
	}

	protected final SourceViewerConfiguration getSourceViewerConfiguration() {
		return fConfiguration;
	}

	protected final ISourceViewer getSourceViewer() {
		return fSourceViewer;
	}

	protected void setSourceViewerConfiguration(
			SourceViewerConfiguration configuration) {
		Assert.isNotNull(configuration);
		fConfiguration = configuration;
	}

	public boolean isEditable() {
		IDocumentProvider provider = getDocumentProvider();
		if (provider instanceof IDocumentProviderExtension) {
			IDocumentProviderExtension extension = (IDocumentProviderExtension) provider;
			return extension.isModifiable(getEditorInput());
		}
		return false;
	}

	protected final void internalInit(IWorkbenchWindow window,
			final IEditorSite site, final IEditorInput input)
			throws PartInitException {

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {

					if (getDocumentProvider() instanceof IDocumentProviderExtension2) {
						IDocumentProviderExtension2 extension = (IDocumentProviderExtension2) getDocumentProvider();
						extension.setProgressMonitor(monitor);
					}

					doSetInput(input);

				} catch (CoreException x) {
					throw new InvocationTargetException(x);
				} finally {
					if (getDocumentProvider() instanceof IDocumentProviderExtension2) {
						IDocumentProviderExtension2 extension = (IDocumentProviderExtension2) getDocumentProvider();
						extension.setProgressMonitor(null);
					}
				}
			}
		};

		try {
			getSite().getWorkbenchWindow().run(false, true, runnable);

		} catch (InterruptedException x) {
		} catch (InvocationTargetException e) {
			Activator.log(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {

		setSite(site);

		internalInit(site.getWorkbenchWindow(), site, input);

	}

	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		return new SourceViewer(parent, ruler, styles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {

		int styles = SWT.V_SCROLL;
		fSourceViewer = createSourceViewer(parent, null, styles);

		if (fConfiguration == null)
			fConfiguration = new SourceViewerConfiguration();
		fSourceViewer.configure(fConfiguration);

		initializeSourceViewer(getEditorInput());

	}

	private void initializeSourceViewer(IEditorInput input) {

		IDocumentProvider documentProvider = getDocumentProvider();
		IAnnotationModel model = documentProvider.getAnnotationModel(input);
		IDocument document = documentProvider.getDocument(input);

		if (document != null) {
			fSourceViewer.setDocument(document, model);
			fSourceViewer.setEditable(isEditable());
			fSourceViewer.showAnnotations(model != null);
		}

		if (fElementStateListener instanceof IElementStateListenerExtension) {
			boolean isStateValidated = false;
			if (documentProvider instanceof IDocumentProviderExtension)
				isStateValidated = ((IDocumentProviderExtension) documentProvider)
						.isStateValidated(input);

			IElementStateListenerExtension extension = (IElementStateListenerExtension) fElementStateListener;
			extension.elementStateValidationChanged(input, isStateValidated);
		}

	}

	private void initializeTitle(IEditorInput input) {

		Image oldImage = fTitleImage;
		fTitleImage = null;
		String title = ""; //$NON-NLS-1$

		if (input != null) {
			IEditorRegistry editorRegistry = PlatformUI.getWorkbench()
					.getEditorRegistry();
			IEditorDescriptor editorDesc = editorRegistry.findEditor(getSite()
					.getId());
			ImageDescriptor imageDesc = editorDesc != null ? editorDesc
					.getImageDescriptor() : null;

			fTitleImage = imageDesc != null ? imageDesc.createImage() : null;
			title = input.getName();
		}

		setTitleImage(fTitleImage);
		setPartName(title);

		firePropertyChange(PROP_DIRTY);

		if (oldImage != null && !oldImage.isDisposed())
			oldImage.dispose();
	}

	protected void setDocumentProvider(IEditorInput input) {
		fImplicitDocumentProvider = DocumentProviderRegistry.getDefault()
				.getDocumentProvider(input);
	}

	private void updateDocumentProvider(IEditorInput input) {

		IProgressMonitor rememberedProgressMonitor = null;

		IDocumentProvider provider = getDocumentProvider();
		if (provider != null) {
			provider.removeElementStateListener(fElementStateListener);
			if (provider instanceof IDocumentProviderExtension2) {
				IDocumentProviderExtension2 extension = (IDocumentProviderExtension2) provider;
				rememberedProgressMonitor = extension.getProgressMonitor();
				extension.setProgressMonitor(null);
			}
		}

		setDocumentProvider(input);

		provider = getDocumentProvider();
		if (provider != null) {
			provider.addElementStateListener(fElementStateListener);
			if (provider instanceof IDocumentProviderExtension2) {
				IDocumentProviderExtension2 extension = (IDocumentProviderExtension2) provider;
				extension.setProgressMonitor(rememberedProgressMonitor);
			}
		}
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		if (input == null) {
			close(isSaveOnCloseNeeded());

		} else {
			IEditorInput oldInput = getEditorInput();
			if (oldInput != null)
				getDocumentProvider().disconnect(oldInput);

			super.setInput(input);

			updateDocumentProvider(input);

			IDocumentProvider provider = getDocumentProvider();

			provider.connect(input);

			initializeTitle(input);

			if (fSourceViewer != null) {
				initializeSourceViewer(input);

			}

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void setInputWithNotify(IEditorInput input) {
		try {

			doSetInput(input);

			firePropertyChange(IEditorPart.PROP_INPUT);

		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setInput(IEditorInput input) {
		setInputWithNotify(input);
	}

	/*
	 * @see ITextEditor#close
	 */
	public void close(final boolean save) {

		enableSanityChecking(false);

		Display display = getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (fSourceViewer != null)
					getSite().getPage().closeEditor(TestEditor.this, save);
			}
		});
	}

	/**
	 * The <code>AbstractTextEditor</code> implementation of this
	 * <code>IWorkbenchPart</code> method may be extended by subclasses.
	 * Subclasses must call <code>super.dispose()</code>.
	 * <p>
	 * Note that many methods may return <code>null</code> after the editor is
	 * disposed.
	 * </p>
	 */
	@Override
	public void dispose() {

		if (fTitleImage != null) {
			fTitleImage.dispose();
			fTitleImage = null;
		}

		disposeDocumentProvider();

		if (fSourceViewer != null) {
			fSourceViewer = null;
		}

		if (fConfiguration != null)
			fConfiguration = null;

		IOperationHistory history = OperationHistoryFactory
				.getOperationHistory();
		if (history != null) {
			if (fNonLocalOperationApprover != null)
				history.removeOperationApprover(fNonLocalOperationApprover);
			if (fLinearUndoViolationApprover != null)
				history.removeOperationApprover(fLinearUndoViolationApprover);
		}
		fNonLocalOperationApprover = null;
		fLinearUndoViolationApprover = null;

		super.dispose();
	}

	/**
	 * Disposes of the connection with the document provider. Subclasses may
	 * extend.
	 * 
	 * @since 3.0
	 */
	protected void disposeDocumentProvider() {
		IDocumentProvider provider = getDocumentProvider();
		if (provider != null) {

			IEditorInput input = getEditorInput();
			if (input != null)
				provider.disconnect(input);

			if (fElementStateListener != null) {
				provider.removeElementStateListener(fElementStateListener);
				fElementStateListener = null;
			}

		}
		fImplicitDocumentProvider = null;
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
	public void doSave(IProgressMonitor progressMonitor) {
	}

	/**
	 * Enables/disables sanity checking.
	 * 
	 * @param enable
	 *            <code>true</code> if sanity checking should be enabled,
	 *            <code>false</code> otherwise
	 * @since 2.0
	 */
	protected void enableSanityChecking(boolean enable) {
		synchronized (this) {
			fIsSanityCheckEnabled = enable;
		}
	}

	/**
	 * Checks the state of the given editor input if sanity checking is enabled.
	 * 
	 * @param input
	 *            the editor input whose state is to be checked
	 * @since 2.0
	 */
	protected void safelySanityCheckState(IEditorInput input) {
		boolean enabled = false;

		synchronized (this) {
			enabled = fIsSanityCheckEnabled;
		}

		if (enabled)
			sanityCheckState(input);
	}

	/**
	 * Checks the state of the given editor input.
	 * 
	 * @param input
	 *            the editor input whose state is to be checked
	 * @since 2.0
	 */
	protected void sanityCheckState(IEditorInput input) {

		IDocumentProvider p = getDocumentProvider();
		if (p == null)
			return;

		if (p instanceof IDocumentProviderExtension3) {
			long stamp = p.getModificationStamp(input);
			if (stamp != fModificationStamp) {
				fModificationStamp = stamp;
			}

		} else {
			if (fModificationStamp == -1)
				fModificationStamp = p.getSynchronizationStamp(input);

			long stamp = p.getModificationStamp(input);
			if (stamp != fModificationStamp) {
				fModificationStamp = stamp;
			}
		}

		updateState(getEditorInput());
	}

	/**
	 * Enables/disables state validation.
	 * 
	 * @param enable
	 *            <code>true</code> if state validation should be enabled,
	 *            <code>false</code> otherwise
	 * @since 2.1
	 */
	protected void enableStateValidation(boolean enable) {
		synchronized (this) {
			fIsStateValidationEnabled = enable;
		}
	}

	protected void validateState(IEditorInput input) {

		IDocumentProvider provider = getDocumentProvider();
		if (!(provider instanceof IDocumentProviderExtension))
			return;

		IDocumentProviderExtension extension = (IDocumentProviderExtension) provider;

		try {

			extension.validateState(input, getSite().getShell());

		} catch (CoreException x) {
			IStatus status = x.getStatus();
			if (status == null || status.getSeverity() != IStatus.CANCEL) {
				Bundle bundle = Platform.getBundle(PlatformUI.PLUGIN_ID);
				ILog log = Platform.getLog(bundle);
				log.log(x.getStatus());

				Shell shell = getSite().getShell();
				String title = "EditorMessages.Editor_error_validateEdit_title";
				String msg = "EditorMessages.Editor_error_validateEdit_message";
				ErrorDialog.openError(shell, title, msg, x.getStatus());
			}
			return;
		}

		if (fSourceViewer != null)
			fSourceViewer.setEditable(isEditable());

	}

	/*
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension2#validateEditorInputState()
	 * @since 2.1
	 */
	public boolean validateEditorInputState() {

		boolean enabled = false;

		synchronized (this) {
			enabled = fIsStateValidationEnabled;
		}

		if (enabled) {

			ISourceViewer viewer = fSourceViewer;
			if (viewer == null)
				return false;

			final IEditorInput input = getEditorInput();
			BusyIndicator.showWhile(getSite().getShell().getDisplay(),
					new Runnable() {
						/*
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							validateState(input);
						}
					});
			sanityCheckState(input);
			return true;

		}

		return true;
	}

	protected void updateState(IEditorInput input) {
		IDocumentProvider provider = getDocumentProvider();
		if (provider instanceof IDocumentProviderExtension) {
			IDocumentProviderExtension extension = (IDocumentProviderExtension) provider;
			try {

				extension.updateStateCache(input);

				if (fSourceViewer != null)
					fSourceViewer.setEditable(isEditable());

			} catch (CoreException e) {
				Activator.log(e);
			}
		}
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
	public boolean isDirty() {
		IDocumentProvider p = getDocumentProvider();
		return p == null ? false : p.canSaveDocument(getEditorInput());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(Class required) {
		return super.getAdapter(required);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void firePropertyChange(int property) {
		super.firePropertyChange(property);
	}

}
