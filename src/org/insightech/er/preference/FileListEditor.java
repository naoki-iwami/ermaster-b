package org.insightech.er.preference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.util.io.FileUtils;

public abstract class FileListEditor extends ListEditor {

	public static final String VALUE_SEPARATOR = "/";

	private String lastPath;

	private Composite parent;

	private Map<String, String> namePathMap;

	private String extention;

	public FileListEditor(String name, String labelText, Composite parent,
			String extention) {
		super(name, labelText, parent);

		this.parent = parent;

		this.namePathMap = new HashMap<String, String>();

		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());

		this.extention = extention;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getNewInputObject() {

		FileDialog dialog = new FileDialog(getShell());

		if (lastPath != null) {
			if (new File(lastPath).exists()) {
				dialog.setFilterPath(lastPath);
			}
		}

		String[] filterExtensions = new String[] { extention };
		dialog.setFilterExtensions(filterExtensions);

		String filePath = dialog.open();
		if (filePath != null) {
			File file = new File(filePath);
			String fileName = file.getName();

			if (this.contains(fileName)) {
				MessageBox messageBox = new MessageBox(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
				messageBox.setText(ResourceString
						.getResourceString("dialog.title.warning"));
				messageBox.setMessage(ResourceString
						.getResourceString("dialog.message.update.file"));

				if (messageBox.open() == SWT.CANCEL) {
					return null;
				}

				this.namePathMap.put(fileName, filePath);
				return null;
			}

			this.namePathMap.put(fileName, filePath);
			try {
				lastPath = file.getParentFile().getCanonicalPath();
			} catch (IOException e) {
			}

			return fileName;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList, VALUE_SEPARATOR);
		List<String> list = new ArrayList<String>();

		while (st.hasMoreElements()) {
			list.add(st.nextToken());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String createList(String[] items) {
		StringBuilder path = new StringBuilder("");

		for (int i = 0; i < items.length; i++) {
			path.append(items[i]);
			path.append(VALUE_SEPARATOR);
		}

		return path.toString();
	}

	protected abstract String getStorePath(String name);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStore() {
		try {
			File dir = new File(this.getStorePath(""));
			dir.mkdirs();

			for (String name : this.namePathMap.keySet()) {
				File from = new File(this.namePathMap.get(name));
				File to = new File(this.getStorePath(name));
				FileUtils.copyFile(from, to);
			}

		} catch (IOException e) {
			Activator.showErrorDialog(ResourceString
					.getResourceString("error.read.file"));
		}

		super.doStore();
	}

	private boolean contains(String name) {
		org.eclipse.swt.widgets.List list = this.getListControl(this.parent);

		String[] items = list.getItems();

		for (String item : items) {
			if (name.equals(item)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLoad() {
		org.eclipse.swt.widgets.List list = this.getListControl(this.parent);

		String s = getPreferenceStore().getString(getPreferenceName());
		String[] array = parseString(s);

		Set<String> names = new HashSet<String>();

		for (int i = 0; i < array.length; i++) {
			File file = new File(this.getStorePath(array[i]));
			if (file.exists() && !names.contains(array[i])) {
				list.add(array[i]);
				names.add(array[i]);
			}
		}
	}

}
