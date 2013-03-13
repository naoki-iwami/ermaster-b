package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class CopyGroup extends ColumnGroup {

	private static final long serialVersionUID = 8453816730649838482L;

	private ColumnGroup original;

	public CopyGroup(ColumnGroup original) {
		super();

		this.original = original;

		this.setGroupName(this.original.getGroupName());

		for (NormalColumn fromColumn : this.original.getColumns()) {
			CopyColumn copyColumn = new CopyColumn(fromColumn);
			if (fromColumn.getWord() != null) {
				copyColumn.setWord(new CopyWord(fromColumn.getWord()));
			}
			this.addColumn(copyColumn);
		}
	}

	public ColumnGroup restructure(ERDiagram diagram) {
		if (this.original == null) {
			this.original = new ColumnGroup();
		}

		this.restructure(diagram, this.original);

		return this.original;
	}

	private void restructure(ERDiagram diagram, ColumnGroup to) {
		Dictionary dictionary = null;

		if (diagram != null) {
			dictionary = diagram.getDiagramContents().getDictionary();
			for (NormalColumn toColumn : to.getColumns()) {
				dictionary.remove(toColumn);
			}
		}

		to.setGroupName(this.getGroupName());

		List<NormalColumn> columns = new ArrayList<NormalColumn>();

		for (NormalColumn fromColumn : this.getColumns()) {
			// グループの更新ボタンを押した場合、
			CopyColumn copyColumn = (CopyColumn) fromColumn;
			CopyWord copyWord = copyColumn.getWord();

			if (copyWord != null) {
				Word originalWord = copyColumn.getOriginalWord();

				if (dictionary != null) {
					dictionary.copyTo(copyWord, originalWord);

				} else {
					while (originalWord instanceof CopyWord) {
						originalWord = ((CopyWord) originalWord).getOriginal();
					}

					//originalWord = new CopyWord(originalWord);
					//copyWord.copyTo(originalWord);
					copyWord.setOriginal(originalWord);
				}
			}

			NormalColumn restructuredColumn = copyColumn
					.getRestructuredColumn();

			if (to instanceof CopyGroup) {
				if (!(restructuredColumn instanceof CopyColumn)) {
					Word restructuredWord = restructuredColumn.getWord();
					
					restructuredColumn = new CopyColumn(restructuredColumn);
					
					if (restructuredWord != null && !(restructuredWord instanceof CopyWord)) {
						restructuredColumn.setWord(new CopyWord(restructuredWord));
					}
				}
			}

			columns.add(restructuredColumn);

			if (dictionary != null) {
				dictionary.add(restructuredColumn);
			}

		}

		to.setColumns(columns);
	}

}
