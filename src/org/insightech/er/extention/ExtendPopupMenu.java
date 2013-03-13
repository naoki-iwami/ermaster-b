package org.insightech.er.extention;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.ERDiagramEditor;

public class ExtendPopupMenu {

	/** 拡張ポイントのID */
	private static final String EXTENSION_POINT_ID = "org.insightech.er.popupMenus";

	/** 拡張ポイントの element の名前 */
	private static final String EXTENSION_NAME = "popupMenu";

	/** メニューを追加する位置を指定する Attribute の名前 */
	private static final String ATTRIBUTE_PATH = "path";

	/** 追加するメニューのアクションファクトリークラスを指定する Attribute の名前 */
	private static final String ATTRIBUTE_CLASS = "class";

	private IAction action;

	private String path;

	public IAction getAction() {
		return action;
	}

	public String getPath() {
		return path;
	}

	public static ExtendPopupMenu createExtendPopupMenu(
			IConfigurationElement configurationElement, ERDiagramEditor editor)
			throws CoreException {
		ExtendPopupMenu menu = null;

		if (ExtendPopupMenu.EXTENSION_NAME.equals(configurationElement
				.getName())) {

		}
		String path = configurationElement.getAttribute(ATTRIBUTE_PATH);
		Object obj = configurationElement
				.createExecutableExtension(ATTRIBUTE_CLASS);

		if (obj instanceof IERDiagramActionFactory) {
			menu = new ExtendPopupMenu();
			IERDiagramActionFactory actionFactory = (IERDiagramActionFactory) obj;

			menu.action = actionFactory.createIAction(editor);
			menu.path = path;
		}

		return menu;
	}

	/**
	 * plugin.xmlからタグを読み込む.
	 * 
	 * @throws CoreException
	 * 
	 * @throws CoreException
	 */
	public static List<ExtendPopupMenu> loadExtensions(ERDiagramEditor editor)
			throws CoreException {
		List<ExtendPopupMenu> extendPopupMenuList = new ArrayList<ExtendPopupMenu>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(EXTENSION_POINT_ID);

		if (extensionPoint != null) {
			for (IExtension extension : extensionPoint.getExtensions()) {
				for (IConfigurationElement configurationElement : extension
						.getConfigurationElements()) {

					ExtendPopupMenu extendPopupMenu = ExtendPopupMenu
							.createExtendPopupMenu(configurationElement, editor);

					if (extendPopupMenu != null) {
						extendPopupMenuList.add(extendPopupMenu);
					}
				}
			}
		}

		return extendPopupMenuList;
	}

}
