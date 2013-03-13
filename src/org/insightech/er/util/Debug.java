package org.insightech.er.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class Debug {

	private static Logger logger = Logger.getLogger(Debug.class.getName());

	public static String getStackTrace(Exception e) {
		StringWriter writer = new StringWriter();

		e.printStackTrace(new PrintWriter(writer));

		return writer.toString();
	}

	public static void memoryLog() {
		memoryLog(null);
	}

	public static void memoryLog(String message) {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();

		if (message != null) {
			logger.info(message);
		}
		logger.info("Total Memory : " + Format.getFileSizeStr(total));
		logger.info("Free  Memory : " + Format.getFileSizeStr(free));
	}

}
