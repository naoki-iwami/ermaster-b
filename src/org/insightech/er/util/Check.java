package org.insightech.er.util;

public class Check {

	public static boolean isAlphabet(String str) {
		char[] ch = str.toCharArray();

		for (int i = 0; i < ch.length; i++) {
			if (ch[i] < '0' || 'z' < ch[i]) {
				return false;
			}

			if ('9' < ch[i] && ch[i] < 'A') {
				return false;
			}

			if ('z' < ch[i] && ch[i] < '_') {
				return false;
			}

			if ('_' < ch[i] && ch[i] < 'a') {
				return false;
			}
		}

		return true;
	}

	public static boolean equals(Object str1, Object str2) {
		if (str1 == null) {
			if (str2 == null) {
				return true;
			}

			return false;
		}

		return str1.equals(str2);
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyTrim(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		}
		return false;
	}
}
