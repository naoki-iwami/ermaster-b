package org.insightech.er.util;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CSV出力クラス
 * 
 * @author generator
 * @version $Id: CsvWriter.java,v 1.1 2008/08/17 10:49:17 h_nakajima Exp $
 */
public class CsvWriter {

	private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd");

	private static final String DELIMITER = ",";

	private PrintWriter writer;

	private DateFormat dateFormat;

	private String delimiter;

	/**
	 * コンストラクタ
	 * 
	 * @param writer
	 *            出力先
	 */
	public CsvWriter(PrintWriter writer) {
		this.writer = writer;
		this.delimiter = "";
		this.dateFormat = DEFAULT_FORMAT;
	}

	/**
	 * Date 型のデータを出力する際のフォーマット形式を指定します
	 * 
	 * @param format
	 *            フォーマット形式
	 */
	public void setDateFormat(String format) {
		this.dateFormat = new SimpleDateFormat(format);
	}

	/**
	 * CSVのために文字列をエスケープします。
	 * 
	 * @param str
	 *            エスケープ前の文字列
	 * @return エスケープされた文字列
	 */
	public static String escape(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\"", "\"\"");
	}

	/**
	 * オブジェクトの文字列表現を出力します
	 * 
	 * @param object
	 *            オブジェクト
	 */
	public void print(Object object) {
		String value = null;

		if (object instanceof Date) {
			value = dateFormat.format((Date) object);
		} else {
			if (object == null) {
				value = "";
			} else {
				value = object.toString();
			}
		}

		writer.print(this.delimiter);

		writer.print("\"");
		writer.print(escape(value));
		writer.print("\"");

		this.setDelimiter();
	}

	/**
	 * デリミターを出力対象にセットします
	 */
	private void setDelimiter() {
		this.delimiter = DELIMITER;
	}

	/**
	 * デリミターを出力対象からリセットします
	 */
	private void resetDelimiter() {
		this.delimiter = "";
	}

	/**
	 * 改行コードを出力します
	 */
	public void crln() {
		writer.print("\r\n");

		this.resetDelimiter();
	}

	/**
	 * 出力先を閉じます
	 */
	public void close() {
		this.writer.close();
	}

}
