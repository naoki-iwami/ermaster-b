package org.insightech.er.editor.model.dbexport.ddl;

import java.io.Serializable;

public class DDLTarget implements Serializable, Cloneable {

	private static final long serialVersionUID = 8212409392159961699L;

	public boolean dropTablespace = false;

	public boolean dropSequence = false;

	public boolean dropTrigger = false;

	public boolean dropView = false;

	public boolean dropIndex = false;

	public boolean dropTable = false;

	public boolean createTablespace = true;

	public boolean createSequence = true;

	public boolean createTrigger = true;

	public boolean createView = true;

	public boolean createIndex = true;

	public boolean createTable = true;

	public boolean createForeignKey = true;

	public boolean createComment = true;

	public boolean inlineTableComment = false;

	public boolean inlineColumnComment = false;

	public boolean commentValueDescription = false;

	public boolean commentValueLogicalName = false;

	public boolean commentValueLogicalNameDescription = true;

	public boolean commentReplaceLineFeed = false;

	public String commentReplaceString;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DDLTarget other = (DDLTarget) obj;
		if (commentReplaceLineFeed != other.commentReplaceLineFeed)
			return false;
		if (commentReplaceString == null) {
			if (other.commentReplaceString != null)
				return false;
		} else if (!commentReplaceString.equals(other.commentReplaceString))
			return false;
		if (commentValueDescription != other.commentValueDescription)
			return false;
		if (commentValueLogicalName != other.commentValueLogicalName)
			return false;
		if (commentValueLogicalNameDescription != other.commentValueLogicalNameDescription)
			return false;
		if (createComment != other.createComment)
			return false;
		if (createForeignKey != other.createForeignKey)
			return false;
		if (createIndex != other.createIndex)
			return false;
		if (createSequence != other.createSequence)
			return false;
		if (createTable != other.createTable)
			return false;
		if (createTablespace != other.createTablespace)
			return false;
		if (createTrigger != other.createTrigger)
			return false;
		if (createView != other.createView)
			return false;
		if (dropIndex != other.dropIndex)
			return false;
		if (dropSequence != other.dropSequence)
			return false;
		if (dropTable != other.dropTable)
			return false;
		if (dropTablespace != other.dropTablespace)
			return false;
		if (dropTrigger != other.dropTrigger)
			return false;
		if (dropView != other.dropView)
			return false;
		if (inlineColumnComment != other.inlineColumnComment)
			return false;
		if (inlineTableComment != other.inlineTableComment)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DDLTarget clone() {
		try {
			DDLTarget clone = (DDLTarget) super.clone();

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
