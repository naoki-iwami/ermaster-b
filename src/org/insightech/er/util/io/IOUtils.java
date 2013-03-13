package org.insightech.er.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOUtils {

	public static void closeQuietly(InputStream input) {
		try {
			if (input != null)
				input.close();
		} catch (IOException ioe) {
		}
	}

	public static void closeQuietly(OutputStream output) {
		try {
			if (output != null)
				output.close();
		} catch (IOException ioe) {
		}
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte buffer[] = new byte[4096];
		int count = 0;
		for (int n = 0; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
			count += n;
		}

		return count;
	}

	public static void copy(InputStream input, Writer output)
			throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(((Reader) (in)), output);
	}

	public static int copy(Reader input, Writer output) throws IOException {
		char buffer[] = new char[4096];
		int count = 0;
		for (int n = 0; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
			count += n;
		}

		return count;
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	public static String toString(InputStream input) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw);
		return sw.toString();
	}

	public static void write(String data, OutputStream output)
			throws IOException {
		if (data != null)
			output.write(data.getBytes());
	}

	public static void write(String data, OutputStream output, String encoding)
			throws IOException {
		if (data != null)
			if (encoding == null)
				write(data, output);
			else
				output.write(data.getBytes(encoding));
	}
}
