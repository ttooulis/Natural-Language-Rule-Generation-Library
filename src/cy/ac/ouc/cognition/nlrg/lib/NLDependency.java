package cy.ac.ouc.cognition.nlrg.lib;

import org.json.JSONPropertyIgnore;

public class NLDependency extends NLThing {
	
	private String	DependencyName;
	private	NLToken	Governor;
	private NLToken	Dependent;

	
		
	public NLDependency(String dependencyName) {
		super(dependencyName);
		DependencyName = new String(dependencyName);
	}

	NLDependency(String dependencyName, NLToken governor, NLToken dependent) {
		
		this(dependencyName);
		
		Governor = governor;
		Dependent = dependent;
		
		Complete = true;
	}
	


	/**
	 * @return the dependencyName
	 */
	public String getDependencyName() {
		return DependencyName;
	}
	
	/**
	 * @param dependencyName the dependencyName to set
	 */
	public void setDependency(String dependencyName) {
		DependencyName = new String(dependencyName);
		Complete = false;
	}
	
	
	
	/**
	 * @return the governor
	 */
	@JSONPropertyIgnore
	public NLToken getGovernor() {
		return Governor;
	}
	
	/**
	 * @param governor the governor to set
	 */
	@JSONPropertyIgnore
	public void setGovernor(NLToken governor) {
		Governor = governor;
		Complete = false;
	}
	
	
	
	/**
	 * @return the dependent
	 */
	public NLToken getDependent() {
		return Dependent;
	}
	/**
	 * @param dependent the dependent to set
	 */
	public void setDependent(NLToken dependent) {
		Dependent = dependent;
	}

}
