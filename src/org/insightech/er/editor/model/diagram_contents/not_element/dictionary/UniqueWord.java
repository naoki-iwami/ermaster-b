package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

public class UniqueWord extends Word {

	private static final long serialVersionUID = 6795589487175949331L;

	public UniqueWord(Word word) {
		super(word);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME
				* result
				+ ((this.getTypeData() == null) ? 0 : this.getTypeData()
						.hashCode());
		result = PRIME
				* result
				+ ((this.getDescription() == null) ? 0 : this.getDescription()
						.hashCode());
		result = PRIME
				* result
				+ ((this.getLogicalName() == null) ? 0 : this.getLogicalName()
						.hashCode());
		result = PRIME
				* result
				+ ((this.getPhysicalName() == null) ? 0 : this
						.getPhysicalName().hashCode());
		result = PRIME * result
				+ ((this.getType() == null) ? 0 : this.getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Word other = (Word) obj;
		if (getTypeData() == null) {
			if (other.getTypeData() != null)
				return false;
		} else if (!getTypeData().equals(other.getTypeData()))
			return false;
		if (getDescription() == null) {
			if (other.getDescription() != null)
				return false;
		} else if (!getDescription().equals(other.getDescription()))
			return false;
		if (getLogicalName() == null) {
			if (other.getLogicalName() != null)
				return false;
		} else if (!getLogicalName().equals(other.getLogicalName()))
			return false;
		if (getPhysicalName() == null) {
			if (other.getPhysicalName() != null)
				return false;
		} else if (!getPhysicalName().equals(other.getPhysicalName()))
			return false;
		if (this.getType() == null) {
			if (other.getType() != null)
				return false;
		} else if (!this.getType().equals(other.getType()))
			return false;
		return true;
	}
}
