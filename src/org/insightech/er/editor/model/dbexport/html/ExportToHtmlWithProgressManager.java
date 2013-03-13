package org.insightech.er.editor.model.dbexport.html;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.HtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ExportToHtmlWithProgressManager extends ExportToHtmlManager
		implements IRunnableWithProgress {

	private Exception exception;

	private IProgressMonitor monitor;

	public ExportToHtmlWithProgressManager(String outputDir, ERDiagram diagram,
			Map<TableView, Location> tableLocationMap) {
		super(outputDir, diagram, tableLocationMap);
	}

	/**
	 * exception ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return exception
	 */
	public Exception getException() {
		return exception;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		int count = overviewPageGenerator.countAllClasses(diagram,
				htmlReportPageGeneratorList);

		monitor.beginTask(ResourceString
				.getResourceString("dialog.message.export.html"), count);

		try {
			this.monitor = monitor;
			doProcess();

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			this.exception = e;
		}

		monitor.done();
	}

	@Override
	protected void doPreTask(HtmlReportPageGenerator pageGenerator,
			Object object) {
		this.monitor
				.subTask("writing : " + pageGenerator.getObjectName(object));
	}

	@Override
	protected void doPostTask() throws InterruptedException {
		this.monitor.worked(1);

		if (this.monitor.isCanceled()) {
			throw new InterruptedException("Cancel has been requested.");
		}
	}

}
