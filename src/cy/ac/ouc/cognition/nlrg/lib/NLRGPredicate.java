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


	
	public static String buildNameText(List<String> name, boolean ignoreNegation) {
		
		String nameText = "";
		int i = 0, doNotCapitalizeIndex = 1;
		
		for (String namePart : name) {
			i++;
			
			if (i==1 && ignoreNegation && namePart.equals("\'-\'"))
				i = 0;
			
			// If current word belongs in capitalisation exceptions list, do not capitalise next word, if should
			else if (namePart.matches(NLRGParameterLib.NLRGRule_PredNameCapitalizeExceptions)) {
				doNotCapitalizeIndex = i + 1;
				nameText += namePart.replaceAll("\'", "");
			}

			else if (i==1)
				nameText += namePart;

			else {
				nameText += NLRGParameterLib.NLRGRule_PredNameConcatChar;
				if (NLRGParameterLib.NLRGRule_PredNameCapitalize == 1 && i != doNotCapitalizeIndex)
					nameText += namePart.substring(0, 1).toUpperCase() + namePart.substring(1);
				else
					nameText += namePart;
			}
		}
		
		return nameText;

	}
	
	
	public static boolean comparePredicateNames(List<String> nameOne, List<String> nameTwo, boolean ignoreNegation) {
		return buildNameText(nameOne, ignoreNegation).equals(buildNameText(nameTwo, ignoreNegation));
	}


	public String getNameText() {
		return buildNameText(this.Name, false);
	}

	
	public String getNameText(boolean ignoreNegation) {
		return buildNameText(this.Name, ignoreNegation);
	}

	
	public boolean sameNameAs(List<String> name, boolean ignoreNegation) {
		return comparePredicateNames(Name, name, ignoreNegation);
	}

	
	public boolean sameNameAs(List<String> name) {
		return this.sameNameAs(name, false);
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



    public static String buildPredicateText(List<String> name, List<NLRGPredicateArgument> arguments, boolean ignoreNegation) {
    	
    	String predicateString = "";

    	predicateString = buildNameText(name, ignoreNegation);
    	
	    if (!predicateString.isEmpty()) {

		    if (!arguments.isEmpty()) {
		    		
		    	predicateString += "(";
		
		        int count = 0;
		    	for (NLRGPredicateArgument argument : arguments) {
			
			    	count++;
			    	if (count > 1)
			    		predicateString += ", ";
			
			    	predicateString += argument.toString();
			
		    	}
			
			    predicateString += ")";
		    }
	    }
    	
    	return predicateString;
    }

    
    public static String buildPredicateText(NLRGPredicate predicate, boolean ignoreNegation) {
		return buildPredicateText(predicate.Name, predicate.Arguments, false);
    }
    
    
	public static boolean comparePredicateNameAndArguments(
			List<String> nameOne,
			List<NLRGPredicateArgument> argumentsOne,
			List<String> nameTwo,
			List<NLRGPredicateArgument> argumentsTwo,
			boolean ignoreNegation) {
		
		return buildPredicateText(nameOne, argumentsOne, ignoreNegation).equals(buildPredicateText(nameTwo, argumentsTwo, ignoreNegation));
	}

	
	public boolean sameNameAndArgumentsAs(List<String> name, List<NLRGPredicateArgument> arguments) {
		return this.sameNameAndArgumentsAs(name, arguments, false);
	}


	public boolean sameNameAndArgumentsAs(List<String> name, List<NLRGPredicateArgument> arguments, boolean ignoreNegation) {
		return comparePredicateNameAndArguments(this.Name, this.Arguments, name, arguments, ignoreNegation);
	}
	
	
    public String toString(boolean ignoreNegation) {
    	if (Generate)
    		return buildPredicateText(this, false);
    	
    	return "";
    }

    
    public String toString() {
    	return this.toString(false);
    }

}
