package cy.ac.ouc.cognition.nlrg.lib;

import java.util.ArrayList;
import java.util.HashMap;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGPredicate.RulePart;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.TL;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.outln;

public abstract class NLRGRule extends NLRGKnowledgeBaseElement {
	
	protected String							Name;
	protected HashMap<String, NLRGPredicate>	Head;
	protected HashMap<String, NLRGPredicate>	Body;

	protected int		CurrentPredicateIndex = 0;
	protected boolean	Complete;
	
	protected boolean	Conflict = false;
	

	
	NLRGRule() {
		this("");
	}
	
	
	NLRGRule(String name) {
		Name = new String(name);
		Head = new HashMap<String, NLRGPredicate>();
		Body = new HashMap<String, NLRGPredicate>();
		Complete = false;
	}
	

	public void addPredicate(String index, NLRGPredicate predicate, RulePart predicateRulePart) {
		if (predicateRulePart == RulePart.HEAD)
			addHeadPredicate(index, predicate);
		else
			addBodyPredicate(index, predicate);
	}


	public void addHeadPredicate(String index, NLRGPredicate headPredicate) {
		Head.put(index, headPredicate);
		Complete = false;
		outln(TL, "Added Head predicate ["+index+"]: " + headPredicate.getNameText());	
	}


	public void addBodyPredicate(String index, NLRGPredicate bodyPredicate) {
		Body.put(index, bodyPredicate);
		Complete = false;
		outln(TL, "Added Body predicate ["+index+"]: " + bodyPredicate.getNameText());	
	}
	
	
	public void addArgumentToPredicate(NLRGPredicateArgument argument, String index, RulePart predicateRulePart) {
		if (predicateRulePart == RulePart.HEAD)
			addArgumentToHeadPredicate(argument, index);
		else
			addArgumentToBodyPredicate(argument, index);
	}

	
	public void addArgumentToHeadPredicate(NLRGPredicateArgument argument, String index) {
		Head.get(index).addArgument(argument);;
		outln(TL, "Added Argument to Head predicate " + Head.get(index).getNameText() + "["+index+"]: " + argument.getNameText());
	}

	
	public void addArgumentToBodyPredicate(NLRGPredicateArgument argument, String index) {
		Body.get(index).addArgument(argument);;
		outln(TL, "Added Argument to Body predicate " + Body.get(index).getNameText() + "["+index+"]: " + argument.getNameText());
	}


	public void addPredicateWithArguments(
			String index,
			NLRGPredicate predicate,
			ArrayList<NLRGPredicateArgument> arguments,
			RulePart predicateRulePart) {

		addPredicate(index, predicate, predicateRulePart);
		
		for (NLRGPredicateArgument argument : arguments)
			addArgumentToPredicate(argument, index, predicateRulePart);
	}

	
	public int addPredicateWithArgumentsFromLists(
			ArrayList<String> predicateIdentifier,
			ArrayList<String> argumentIdentifiers,
			RulePart predicateRulePart) {


		NLRGPredicate predicate = new NLRGPredicate();
		
		/* If new predicate is not empty proceed to create relevant argument object list and add it to predicate */	
		predicate.setName(predicateIdentifier);
		if (predicate.getNameText() != "") {
			
			ArrayList<NLRGPredicateArgument> arguments = new ArrayList<NLRGPredicateArgument>();	
			for (String argumentIdentifier : argumentIdentifiers)
				arguments.add(new NLRGPredicateArgument(argumentIdentifier));
			
			predicate.setArguments(arguments);

			/* If new predicate with the same argumentsis  not already added to the rule
			 * proceed to add it to the respective part of the rule
			 */	
			if (!this.containsPredicateWithArguments(predicate, predicateRulePart, true)) {
		
				String predicateKey = Integer.toString(this.CurrentPredicateIndex++);
			
				addPredicate(predicateKey, predicate, predicateRulePart);
			
				return 1;
			}
		
		}

		return 0;

	}



	public boolean headContainsPredicateName(NLRGPredicate testPred, boolean ignoreNegation) {

		for (NLRGPredicate headPredicate : Head.values())
			if (headPredicate.sameNameAs(testPred.getName(), ignoreNegation))
				return true;

		return false;
	}
	

	public boolean bodyContainsPredicateName(NLRGPredicate testPred, boolean ignoreNegation) {

		for (NLRGPredicate bodyPredicate : Body.values())
			if (bodyPredicate.sameNameAs(testPred.getName(), ignoreNegation))
				return true;

		return false;
	}

	
	
	public boolean containsPredicateName(NLRGPredicate testPred, RulePart predicateRulePart, boolean ignoreNegation) {

		if (predicateRulePart == RulePart.HEAD)
			return headContainsPredicateName(testPred, ignoreNegation);
		else
			return bodyContainsPredicateName(testPred, ignoreNegation);

	}



	public boolean headContainsPredicateWithArguments(NLRGPredicate testPred, boolean ignoreNegation) {

		for (NLRGPredicate headPredicate : Head.values())
			if (headPredicate.sameNameAndArgumentsAs(testPred.getName(), testPred.getArguments(), ignoreNegation))
				return true;

		return false;
	}
	

	public boolean bodyContainsPredicateWithArguments(NLRGPredicate testPred, boolean ignoreNegation) {

		for (NLRGPredicate bodyPredicate : Body.values())
			if (bodyPredicate.sameNameAndArgumentsAs(testPred.getName(), testPred.getArguments(), ignoreNegation))
				return true;

		return false;
	}

	
	
	public boolean containsPredicateWithArguments(NLRGPredicate testPred, RulePart predicateRulePart, boolean ignoreNegation) {

		if (predicateRulePart == RulePart.HEAD)
			return headContainsPredicateWithArguments(testPred, ignoreNegation);
		else
			return bodyContainsPredicateWithArguments(testPred, ignoreNegation);

	}

	

	public String predicateSetToString(RulePart predicateRulePart) {

		HashMap<String, NLRGPredicate> predicateSet;
		
		if (predicateRulePart == RulePart.HEAD)
			predicateSet = Head;
		else
			predicateSet = Body;


		  String serializedText = "";
		  
		  serializedText +=  "Predicates=[" ;
		  int i = 1;
	      for (NLRGPredicate headPredicate : predicateSet.values()) {
	
	    	  if (i != 1)
	    		  serializedText += ", ";
	    	  
	    	  serializedText += headPredicate.getNameText();
	    	  i++;
	        	
	      }
		  serializedText +=  "]" ;
        
        return serializedText;
        
	}



	public String toString() {

		String serializedText = "Rule " + Name + ": ";
		  
		serializedText += 	"Head " + predicateSetToString(RulePart.HEAD) + ", " +
							"Body " + predicateSetToString(RulePart.BODY);
	
		return serializedText;
	  
	}



	public String generatePredicateSetText(RulePart predicateRulePart) {

		return predicateSetToString(predicateRulePart);
        
	}


	
	public String generateRuleText() {

		return toString();
	  
	}



	/**
	 * @return the complete
	 */
	public  boolean isComplete() {
		return Complete;
	}
	


	/**
	 * @@param complete the complete to set
	 */
	public void setComplete(boolean complete) {
		Complete = complete;
		if (Complete)
			outln(TL, "Rule Generation Completed = [" + this.toString() +"]");

	}
	



	/**
	 * @return the Conflict
	 */
	public  boolean isConflict() {
		return Conflict;
	}
	


	/**
	 * @param the conflict to set
	 */
	public void setConflict(boolean conflict) {
		Conflict = conflict;
	}
}
