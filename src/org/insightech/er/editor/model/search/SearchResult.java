package org.insightech.er.editor.model.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchResult {

	public static final int SORT_TYPE_PATH = 1;

	public static final int SORT_TYPE_TYPE = 2;

	public static final int SORT_TYPE_NAME = 3;

	public static final int SORT_TYPE_VALUE = 4;

	private int sortType;

	private Object resultObject;

	private List<SearchResultRow> rows;

	public SearchResult(Object resultObject, List<SearchResultRow> rows) {
		this.resultObject = resultObject;
		this.rows = rows;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public List<SearchResultRow> getRows() {
		return rows;
	}

	public void addRow(SearchResultRow row) {
		this.rows.add(row);
	}

	public void sort(int sortType) {
		this.sortType = sortType;

		Collections.sort(rows, new SearchResultRowComparator());
	}

	private class SearchResultRowComparator implements
			Comparator<SearchResultRow> {
		public int compare(SearchResultRow o1, SearchResultRow o2) {
			if (o1 == null) {
				return 1;
			}
			if (o2 == null) {
				return -1;
			}

			String value1 = null;
			String value2 = null;

			if (sortType == SORT_TYPE_PATH) {
				value1 = o1.getPath();
				value2 = o2.getPath();

			} else if (sortType == SORT_TYPE_TYPE || sortType == SORT_TYPE_NAME) {
				int type1 = o1.getType();
				int type2 = o2.getType();

				return type1 - type2;

			} else if (sortType == SORT_TYPE_VALUE) {
				value1 = o1.getText();
				value2 = o2.getText();
			}

			if (value1 == null) {
				return 1;
			}
			if (value2 == null) {
				return -1;
			}

			return value1.compareTo(value2);
		}

	}
}
