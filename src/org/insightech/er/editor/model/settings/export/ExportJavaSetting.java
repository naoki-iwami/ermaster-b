package org.insightech.er.editor.model.settings.export;

import java.io.Serializable;

public class ExportJavaSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 8062761326645885449L;

	private String javaOutput;

	private String packageName;

	private String classNameSuffix;

	private String srcFileEncoding;

	private boolean withHibernate;

	private String extendsClass;

	public String getExtendsClass() {
		return extendsClass;
	}

	public void setExtendsClass(String extendsClass) {
		this.extendsClass = extendsClass;
	}

	public String getJavaOutput() {
		return javaOutput;
	}

	public void setJavaOutput(String javaOutput) {
		this.javaOutput = javaOutput;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassNameSuffix() {
		return classNameSuffix;
	}

	public void setClassNameSuffix(String classNameSuffix) {
		this.classNameSuffix = classNameSuffix;
	}

	public String getSrcFileEncoding() {
		return srcFileEncoding;
	}

	public void setSrcFileEncoding(String srcFileEncoding) {
		this.srcFileEncoding = srcFileEncoding;
	}

	public boolean isWithHibernate() {
		return withHibernate;
	}

	public void setWithHibernate(boolean withHibernate) {
		this.withHibernate = withHibernate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportJavaSetting other = (ExportJavaSetting) obj;
		if (classNameSuffix == null) {
			if (other.classNameSuffix != null)
				return false;
		} else if (!classNameSuffix.equals(other.classNameSuffix))
			return false;
		if (javaOutput == null) {
			if (other.javaOutput != null)
				return false;
		} else if (!javaOutput.equals(other.javaOutput))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (srcFileEncoding == null) {
			if (other.srcFileEncoding != null)
				return false;
		} else if (!srcFileEncoding.equals(other.srcFileEncoding))
			return false;
		if (withHibernate != other.withHibernate)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportJavaSetting clone() {
		try {
			ExportJavaSetting clone = (ExportJavaSetting) super.clone();

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
