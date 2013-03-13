package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class OverviewHtmlReportPageGenerator {

	private Map<Object, Integer> idMap;

	public OverviewHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		this.idMap = idMap;
	}

	public String getObjectId(Object object) {
		Integer id = (Integer) idMap.get(object);

		if (id == null) {
			id = new Integer(idMap.size());
			this.idMap.put(object, id);
		}

		return String.valueOf(id);
	}

	public String generateFrame(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("overview/overview-frame_template.html");

		Object[] args = { this.generateFrameTable(htmlReportPageGeneratorList) };
		return MessageFormat.format(template, args);
	}

	private String generateFrameTable(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-frame_row_template.html");

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
			Object[] args = { pageGenerator.getType(),
					pageGenerator.getPageTitle() };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateSummary(String imageSrc,
			Map<TableView, Location> tableLocationMap,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_template.html");

		Object[] args = { this.generateImage(imageSrc, tableLocationMap),
				this.generateSummaryTable(htmlReportPageGeneratorList) };

		return MessageFormat.format(template, args);
	}

	private String generateImage(String imageSrc,
			Map<TableView, Location> tableLocationMap) throws IOException {
		if (imageSrc == null) {
			return "";
		}

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_image_template.html");

		Object[] args = { imageSrc, this.generateImageMap(tableLocationMap) };

		return MessageFormat.format(template, args);
	}

	private String generateImageMap(Map<TableView, Location> tableLocationMap)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		if (tableLocationMap != null) {
			String template = ExportToHtmlManager
					.getTemplate("overview/overview-summary_image_map_template.html");

			for (Map.Entry<TableView, Location> entry : tableLocationMap
					.entrySet()) {
				Location location = entry.getValue();

				Object[] args = { String.valueOf(location.x),
						String.valueOf(location.y),
						String.valueOf(location.x + location.width),
						String.valueOf(location.y + location.height),
						entry.getKey().getObjectType(),
						this.getObjectId(entry.getKey()) };
				String row = MessageFormat.format(template, args);

				sb.append(row);
			}
		}

		return sb.toString();
	}

	private String generateSummaryTable(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_row_template.html");

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
			Object[] args = { pageGenerator.getType(),
					pageGenerator.getPageTitle() };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateAllClasses(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("allclasses_template.html");

		Object[] args = { this.generateAllClassesTable(diagram,
				htmlReportPageGeneratorList) };

		return MessageFormat.format(template, args);
	}

	private String generateAllClassesTable(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("allclasses_row_template.html");

		for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {
			HtmlReportPageGenerator pageGenerator = htmlReportPageGeneratorList
					.get(i);

			for (Object object : pageGenerator.getObjectList(diagram)) {
				Object[] args = {
						pageGenerator.getType() + "/"
								+ pageGenerator.getObjectId(object) + ".html",
						pageGenerator.getObjectName(object) };
				String row = MessageFormat.format(template, args);

				sb.append(row);
			}
		}

		return sb.toString();
	}

	public int countAllClasses(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList) {
		int count = 0;

		for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {
			HtmlReportPageGenerator pageGenerator = htmlReportPageGeneratorList
					.get(i);
			count += pageGenerator.getObjectList(diagram).size();
		}

		return count;
	}
}
