package org.insightech.er.editor.model.dbexport.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.HtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.OverviewHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.CategoryHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.GroupHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.IndexHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.SequenceHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TableHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TablespaceHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TriggerHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.ViewHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.WordHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.util.io.FileUtils;
import org.insightech.er.util.io.IOUtils;

public class ExportToHtmlManager {

	private static final Map PROPERTIES = ResourceString
			.getResources("html.report.");

	private static final String[] FIX_FILES = { "help-doc.html", "index.html",
			"stylesheet.css" };

	public static final String[] ICON_FILES = { "icons/pkey.gif",
			"icons/foreign_key.gif" };

	private static final String TEMPLATE_DIR = "html/";

	protected List<HtmlReportPageGenerator> htmlReportPageGeneratorList = new ArrayList<HtmlReportPageGenerator>();

	protected OverviewHtmlReportPageGenerator overviewPageGenerator;

	private String outputDir;

	protected ERDiagram diagram;

	private Map<TableView, Location> tableLocationMap;

	public ExportToHtmlManager(String outputDir, ERDiagram diagram,
			Map<TableView, Location> tableLocationMap) {
		this.outputDir = outputDir;
		this.diagram = diagram;
		this.tableLocationMap = tableLocationMap;

		Map<Object, Integer> idMap = new HashMap<Object, Integer>();

		this.overviewPageGenerator = new OverviewHtmlReportPageGenerator(idMap);
		htmlReportPageGeneratorList
				.add(new TableHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList
				.add(new IndexHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new SequenceHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList.add(new ViewHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new TriggerHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList
				.add(new GroupHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new TablespaceHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList.add(new WordHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new CategoryHtmlReportPageGenerator(
				idMap));
	}

	protected void doPreTask(HtmlReportPageGenerator pageGenerator,
			Object object) {
	}

	protected void doPostTask() throws InterruptedException {
	}

	public void doProcess() throws IOException, InterruptedException {
		// 固定ファイルのコピー
		for (int i = 0; i < FIX_FILES.length; i++) {
			this.copyOut(FIX_FILES[i], FIX_FILES[i]);
		}

		// テンプレートから生成
		String template = null;

		// イメージ
		String imageSrc = "image/er.png";

		// アイコン
		for (String iconFile : ICON_FILES) {
			this.copyOutResource("image/" + iconFile, iconFile);
		}

		// トップ階層
		String allclasses = overviewPageGenerator.generateAllClasses(diagram,
				htmlReportPageGeneratorList);
		this.writeOut("allclasses.html", allclasses);

		String overviewFrame = overviewPageGenerator
				.generateFrame(htmlReportPageGeneratorList);
		this.writeOut("overview-frame.html", overviewFrame);

		String overviewSummary = overviewPageGenerator.generateSummary(
				imageSrc, tableLocationMap, htmlReportPageGeneratorList);
		this.writeOut("overview-summary.html", overviewSummary);

		// オブジェクトタイプ毎の階層
		for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {

			HtmlReportPageGenerator pageGenerator = (HtmlReportPageGenerator) htmlReportPageGeneratorList
					.get(i);
			try {
				HtmlReportPageGenerator prevPageGenerator = null;
				if (i != 0) {
					prevPageGenerator = (HtmlReportPageGenerator) htmlReportPageGeneratorList
							.get(i - 1);
				}
				HtmlReportPageGenerator nextPageGenerator = null;
				if (i != htmlReportPageGeneratorList.size() - 1) {
					nextPageGenerator = (HtmlReportPageGenerator) htmlReportPageGeneratorList
							.get(i + 1);
				}

				String type = pageGenerator.getType();

				template = pageGenerator.generatePackageFrame(diagram);
				this.writeOut(type + "/package-frame.html", template);

				template = pageGenerator.generatePackageSummary(
						prevPageGenerator, nextPageGenerator, diagram);
				this.writeOut(type + "/package-summary.html", template);

				List<Object> objectList = pageGenerator.getObjectList(diagram);
				for (int j = 0; j < objectList.size(); j++) {
					Object object = objectList.get(j);

					this.doPreTask(pageGenerator, object);

					Object prevObject = null;
					if (j != 0) {
						prevObject = (Object) objectList.get(j - 1);
					}
					Object nextObject = null;
					if (j != objectList.size() - 1) {
						nextObject = (Object) objectList.get(j + 1);
					}

					template = pageGenerator.generateContent(diagram, object,
							prevObject, nextObject);

					String objectId = pageGenerator.getObjectId(object);
					this.writeOut(type + "/" + objectId + ".html", template);

					this.doPostTask();
				}

			} catch (RuntimeException e) {
				throw new IllegalStateException(pageGenerator.getClass()
						.getName(), e);
			}
		}
	}

	public static String getTemplate(String key) throws IOException {
		InputStream in = ExportToHtmlManager.class.getClassLoader()
				.getResourceAsStream(TEMPLATE_DIR + key);
		if (in == null) {
			throw new FileNotFoundException(TEMPLATE_DIR + key);
		}

		try {
			String content = IOUtils.toString(in);
			content = replaceProperties(content);

			return content;

		} finally {
			in.close();
		}
	}

	private void writeOut(String dstPath, String content) throws IOException {
		dstPath = this.outputDir + dstPath;
		File file = new File(dstPath);
		file.getParentFile().mkdirs();

		FileUtils.writeStringToFile(file, content, "UTF-8");
	}

	private void copyOut(String dstPath, String key)
			throws FileNotFoundException, IOException {
		String content = getTemplate(key);
		this.writeOut(dstPath, content);
	}

	private static String replaceProperties(String content) {
		for (Object key : PROPERTIES.keySet()) {
			content = content.replaceAll(String.valueOf(key), String
					.valueOf(PROPERTIES.get(key)));
		}

		return content;
	}

	private void copyOutResource(String dstPath, String srcPath)
			throws FileNotFoundException, IOException {
		InputStream in = null;

		try {
			in = ExportToHtmlManager.class.getClassLoader()
					.getResourceAsStream(srcPath);
			copyOutResource(dstPath, in);

		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void copyOutResource(String dstPath, InputStream in)
			throws FileNotFoundException, IOException {
		FileOutputStream out = null;

		try {
			dstPath = this.outputDir + dstPath;
			File file = new File(dstPath);
			file.getParentFile().mkdirs();

			out = new FileOutputStream(file);

			IOUtils.copy(in, out);

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}
