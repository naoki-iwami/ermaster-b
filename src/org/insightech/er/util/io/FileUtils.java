package org.insightech.er.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {

	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists())
			return;
		cleanDirectory(directory);
		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		} else {
			return;
		}
	}

	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}
		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}
		File files[] = directory.listFiles();
		if (files == null)
			throw new IOException("Failed to list contents of " + directory);
		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception)
			throw exception;
		else
			return;
	}

	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (!file.exists())
				throw new FileNotFoundException("File does not exist: " + file);
			if (!file.delete()) {
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	public static void copyFile(File srcFile, File destFile,
			boolean preserveFileDate) throws IOException {
		if (srcFile == null)
			throw new NullPointerException("Source must not be null");
		if (destFile == null)
			throw new NullPointerException("Destination must not be null");
		if (!srcFile.exists())
			throw new FileNotFoundException("Source '" + srcFile
					+ "' does not exist");
		if (srcFile.isDirectory())
			throw new IOException("Source '" + srcFile
					+ "' exists but is a directory");
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath()))
			throw new IOException("Source '" + srcFile + "' and destination '"
					+ destFile + "' are the same");
		if (destFile.getParentFile() != null
				&& !destFile.getParentFile().exists()
				&& !destFile.getParentFile().mkdirs())
			throw new IOException("Destination '" + destFile
					+ "' directory cannot be created");
		if (destFile.exists() && !destFile.canWrite()) {
			throw new IOException("Destination '" + destFile
					+ "' exists but is read-only");
		} else {
			doCopyFile(srcFile, destFile, preserveFileDate);
			return;
		}
	}

	private static void doCopyFile(File srcFile, File destFile,
			boolean preserveFileDate) throws IOException {
		if (destFile.exists() && destFile.isDirectory())
			throw new IOException("Destination '" + destFile
					+ "' exists but is a directory");
		FileInputStream input = new FileInputStream(srcFile);
		try {
			FileOutputStream output = new FileOutputStream(destFile);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
		if (srcFile.length() != destFile.length())
			throw new IOException("Failed to copy full contents from '"
					+ srcFile + "' to '" + destFile + "'");
		if (preserveFileDate)
			destFile.setLastModified(srcFile.lastModified());
	}

	public static byte[] readFileToByteArray(File file) throws IOException {
		java.io.InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte abyte0[] = IOUtils.toByteArray(in);
			return abyte0;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static void writeByteArrayToFile(File file, byte data[])
			throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			out.write(data);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public static void writeStringToFile(File file, String data, String encoding)
			throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			IOUtils.write(data, out, encoding);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
