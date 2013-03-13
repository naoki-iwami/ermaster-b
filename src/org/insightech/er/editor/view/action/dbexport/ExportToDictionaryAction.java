package org.insightech.er.editor.view.action.dbexport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IEditorPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.util.CsvWriter;
import org.insightech.er.util.Format;

public class ExportToDictionaryAction extends AbstractExportAction {

	public static final String ID = ExportToDictionaryAction.class.getName();

	public ExportToDictionaryAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.export.dictionary"), editor);
		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.EXPORT_TO_CSV));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void save(IEditorPart editorPart, GraphicalViewer viewer,
			String saveFilePath) throws Exception {

		ERDiagram diagram = this.getDiagram();

		Dictionary dictionary = diagram.getDiagramContents().getDictionary();

		PrintWriter out = null;

		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(
					saveFilePath)));
			CsvWriter writer = new CsvWriter(out);

			writer.print(ResourceString
					.getResourceString("label.physical.name"));
			writer
					.print(ResourceString
							.getResourceString("label.logical.name"));
			writer.print(ResourceString.getResourceString("label.column.type"));
			writer.print(ResourceString
					.getResourceString("label.column.description"));
			writer.crln();

			String database = diagram.getDatabase();

			List<Word> list = dictionary.getWordList();

			Collections.sort(list);

			for (Word word : list) {
				writer.print(word.getPhysicalName());
				writer.print(word.getLogicalName());
				if (word.getType() != null) {
					writer.print(Format.formatType(word.getType(), word
							.getTypeData(), database));
				} else {
					writer.print("");
				}
				writer.print(word.getDescription());

				writer.crln();
			}

			Activator.showMessageDialog("dialog.message.export.finish");

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	protected String[] getFilterExtensions() {
		return new String[] { "*.csv" };
	}

	@Override
	protected String getDefaultExtension() {
		return ".csv";
	}

}
