package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.insightech.er.Activator;

public class EnvironmentSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 4937234635221817893L;

	private List<Environment> environments;

	private Environment currentEnvironment;

	public EnvironmentSetting() {
		this.environments = new ArrayList<Environment>();
//		this.environments.add(new Environment(Activator
//				.getResourceString("label.default")));
	}

	/**
	 * environments ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return environments
	 */
	public List<Environment> getEnvironments() {
		return environments;
	}

	/**
	 * environments ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param environments
	 *            environments
	 */
	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}

	/**
	 * currentEnvironment ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return currentEnvironment
	 */
	public Environment getCurrentEnvironment() {
		return currentEnvironment;
	}

	/**
	 * currentEnvironment ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param currentEnvironment
	 *            currentEnvironment
	 */
	public void setCurrentEnvironment(Environment currentEnvironment) {
		this.currentEnvironment = currentEnvironment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		try {
			EnvironmentSetting setting = (EnvironmentSetting) super.clone();

			setting.environments = new ArrayList<Environment>();

			Map<Environment, Environment> oldNewMap = new HashMap<Environment, Environment>();

			for (Environment environment : this.environments) {
				Environment newEnvironment = environment.clone();
				setting.environments.add(newEnvironment);
				oldNewMap.put(environment, newEnvironment);
			}

			setting.currentEnvironment = oldNewMap.get(this.currentEnvironment);

			return setting;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
