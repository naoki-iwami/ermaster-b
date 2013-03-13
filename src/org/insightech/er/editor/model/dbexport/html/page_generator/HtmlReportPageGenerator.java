package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;

public interface HtmlReportPageGenerator {

	public String generatePackageFrame(ERDiagram diagram) throws IOException;

	public String generatePackageSummary(
			HtmlReportPageGenerator prevPageGenerator,
			HtmlReportPageGenerator nextPageGenerator, ERDiagram diagram)
			throws IOException;

	public String generateContent(ERDiagram diagram, Object object,
			Object prevObject, Object nextObject) throws IOException;

	public String getPageTitle();

	public String getType();

	public String getObjectId(Object object);

	public String getObjectName(Object object);

	public List<Object> getObjectList(ERDiagram diagram);

}
