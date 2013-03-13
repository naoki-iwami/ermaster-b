package org.insightech.er.editor.view.figure.connection.decoration;

import org.eclipse.draw2d.RotatableDecoration;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.figure.connection.decoration.idef1x.IDEF1XOneDecoration;
import org.insightech.er.editor.view.figure.connection.decoration.idef1x.IDEF1XTargetDecoration;
import org.insightech.er.editor.view.figure.connection.decoration.idef1x.IDEF1XZeroOneSourceDecoration;
import org.insightech.er.editor.view.figure.connection.decoration.ie.IEOneDecoration;
import org.insightech.er.editor.view.figure.connection.decoration.ie.IEOptionalTargetDecoration;
import org.insightech.er.editor.view.figure.connection.decoration.ie.IETargetDecoration;
import org.insightech.er.editor.view.figure.connection.decoration.ie.IEZeroOneDecoration;

public class DecorationFactory {

	public static Decoration getDecoration(String notation,
			String parentCardinality, String childCardinality) {

		Decoration decoration = new Decoration();

		if ("0..1".equals(parentCardinality)) {
			if (Settings.NOTATION_IDEF1X.equals(notation)) {
				decoration.sourceDecoration = new IDEF1XZeroOneSourceDecoration();

			} else {
				decoration.sourceDecoration = new IEZeroOneDecoration();
			}

		} else {
			if (Settings.NOTATION_IDEF1X.equals(notation)) {
				decoration.sourceDecoration = new IDEF1XOneDecoration();

			} else {
				decoration.sourceDecoration = new IEOneDecoration();
			}
		}

		if ("0..n".equals(childCardinality)) {
			if (Settings.NOTATION_IDEF1X.equals(notation)) {
				// “YŽš ‚È‚µ
				decoration.targetDecoration = new IDEF1XTargetDecoration();

			} else {
				decoration.targetDecoration = new IEOptionalTargetDecoration();
			}

		} else if ("1".equals(childCardinality)) {
			if (Settings.NOTATION_IDEF1X.equals(notation)) {
				decoration.targetDecoration = new IDEF1XOneDecoration();

			} else {
				decoration.targetDecoration = new IEOneDecoration();
			}

		} else if ("0..1".equals(childCardinality)) {
			if (Settings.NOTATION_IDEF1X.equals(notation)) {
				// “YŽš Z
				decoration.targetDecoration = new IDEF1XTargetDecoration();
				decoration.targetLabel = "Z";

			} else {
				decoration.targetDecoration = new IEZeroOneDecoration();
			}

		} else {
			if (Settings.NOTATION_IDEF1X.equals(notation)) {
				// “YŽš P
				decoration.targetDecoration = new IDEF1XTargetDecoration();
				decoration.targetLabel = "P";

			} else {
				decoration.targetDecoration = new IETargetDecoration();
			}
		}

		return decoration;
	}

	public static class Decoration {
		private RotatableDecoration sourceDecoration;

		private RotatableDecoration targetDecoration;

		private String targetLabel;

		public RotatableDecoration getSourceDecoration() {
			return sourceDecoration;
		}

		public RotatableDecoration getTargetDecoration() {
			return targetDecoration;
		}

		public String getTargetLabel() {
			return targetLabel;
		}

	}
}
