package cy.ac.ouc.cognition.nlrg.lib;

import java.util.ArrayList;
import java.util.List;

public class NLRGPredicate extends NLRGKnowledgeBaseElement {

	static enum RulePart {
		HEAD, BODY, UNDEFINED;
	}
	
	protected	List<String>				Name;
	protected	List<NLRGPredicateArgument>	Arguments;
	protected	boolean						Generate;



	public NLRGPredicate() {
		Name = new ArrayList<String>();
		Arguments = new ArrayList<NLRGPredicateArgument>();
		Generate = true;
	}
	

	
	public NLRGPredicate(String name, boolean generate) {
		Name = new ArrayList<String>();
		Name.add(new String(name));
		Arguments = new ArrayList<NLRGPredicateArgument>();
		Generate = generate;
	}
	

	
	public NLRGPredicate(List<String> name, boolean generate) {
		Name = name;
		Generate = generate;
	}
	

	
	public NLRGPredicate(String name, List<NLRGPredicateArgument> arguments, boolean generate) {
		Name = new ArrayList<String>();
		Name.add(new String(name));
		Arguments = arguments;
		Generate = generate;
	}
	

	
	public NLRGPredicate(List<String> name, List<NLRGPredicateArgument> arguments, boolean generate) {
		Name = name;
		Arguments = arguments;
		Generate = generate;
	}
	

	
	/**
	 * @return the name
	 */
	public List<String> getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(List<String> name) {
		Name = name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name.add(new String(name));
	}

	public String getNameText( ) {
		
		String nameText = "";
		int i = 0;
		
		for (String name : Name) {
			i++;
			if (i==1)
				nameText += name;
			else {
				nameText += NLRGParameterLib.NLRGRule_PredNameConcatChar;
				if (NLRGParameterLib.NLRGRule_PredNameCapitalize == 1)
					nameText += name.substring(0, 1).toUpperCase() + name.substring(1);
				else
					nameText += name;
			}
		}
		
		return nameText;

	}
	
	
	public boolean sameNameAs(List<String> name) {
		return Name.toString().equals(name.toString());
	}


	/**
	 * @return the arguments
	 */
	public List<NLRGPredicateArgument> getArguments() {
		return Arguments;
	}

	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(List<NLRGPredicateArgument> arguments) {
		Arguments = arguments;
	}

	public void addArgument(NLRGPredicateArgument argument) {
		Arguments.add(argument);
	}

	/**
	 * @return Generate flag
	 */
	public boolean getGenerate() {
		return Generate;
	}

	/**
	 * @param Generate flag
	 */
	public void setGenerate(boolean generate) {
		Generate = generate;
	}


    public String toString() {
    	
    	String predicateString = "";

    	if (Generate) {

	    	predicateString = getNameText();
    	
	    	if (!predicateString.isEmpty()) {

		    	if (!Arguments.isEmpty()) {
		    		
		    		predicateString += "(";
		
		        	int count = 0;
		    		for (NLRGPredicateArgument Argument : Arguments) {
			
			    		count++;
			    		if (count > 1)
			    			predicateString += ", ";
			
			    		predicateString += Argument.toString();
			
		    		}
			
			    	predicateString += ")";
		    	}
	    	}
    	}
    	
    	return predicateString;
    }


}
