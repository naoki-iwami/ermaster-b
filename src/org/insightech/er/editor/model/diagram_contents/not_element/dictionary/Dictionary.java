package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class Dictionary extends AbstractModel {

	private static final long serialVersionUID = -4476318682977312216L;

	public static final String PROPERTY_CHANGE_DICTIONARY = "dictionary";

	private Map<Word, List<NormalColumn>> wordMap;

	public Dictionary() {
		this.wordMap = new HashMap<Word, List<NormalColumn>>();
	}

	public void add(NormalColumn column) {
		Word word = column.getWord();

		if (word == null) {
			return;
		}

		List<NormalColumn> useColumns = this.wordMap.get(word);

		if (useColumns == null) {
			useColumns = new ArrayList<NormalColumn>();
			this.wordMap.put(word, useColumns);
		}

		if (!useColumns.contains(column)) {
			useColumns.add(column);
		}

		this.firePropertyChange(PROPERTY_CHANGE_DICTIONARY, null, null);
	}

	public void remove(NormalColumn column) {
		Word word = column.getWord();

		if (word == null) {
			return;
		}

		List<NormalColumn> useColumns = this.wordMap.get(word);

		if (useColumns != null) {
			useColumns.remove(column);
			if (useColumns.isEmpty()) {
				this.wordMap.remove(word);
			}
		}

		this.firePropertyChange(PROPERTY_CHANGE_DICTIONARY, null, null);
	}

	public void remove(TableView tableView) {
		if (tableView instanceof ERVirtualTable) {
			return; // 仮想テーブルを消すときはワードは消さない
		}
		for (NormalColumn normalColumn : tableView.getNormalColumns()) {
			this.remove(normalColumn);
		}
	}

	public void clear() {
		this.wordMap.clear();
	}

	public List<Word> getWordList() {
		List<Word> list = new ArrayList<Word>(this.wordMap.keySet());

		Collections.sort(list);

		return list;
	}

	public List<NormalColumn> getColumnList(Word word) {
		return this.wordMap.get(word);
	}

	public void copyTo(Word from, Word to) {
		from.copyTo(to);
	}
}
