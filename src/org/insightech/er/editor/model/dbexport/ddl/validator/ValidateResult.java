package org.insightech.er.editor.model.dbexport.ddl.validator;

public class ValidateResult {

	private String message;

	private String location;

	private int severity;
	
	private Object object;

	/**
	 * object ‚ğæ“¾‚µ‚Ü‚·.
	 *
	 * @return object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * object ‚ğİ’è‚µ‚Ü‚·.
	 *
	 * @param object object
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * message ‚ğæ“¾‚µ‚Ü‚·.
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * message ‚ğİ’è‚µ‚Ü‚·.
	 *
	 * @param message message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * location ‚ğæ“¾‚µ‚Ü‚·.
	 *
	 * @return location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * location ‚ğİ’è‚µ‚Ü‚·.
	 *
	 * @param location location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * severity ‚ğæ“¾‚µ‚Ü‚·.
	 *
	 * @return severity
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * severity ‚ğİ’è‚µ‚Ü‚·.
	 *
	 * @param severity severity
	 */
	public void setSeverity(int severity) {
		this.severity = severity;
	}

}
