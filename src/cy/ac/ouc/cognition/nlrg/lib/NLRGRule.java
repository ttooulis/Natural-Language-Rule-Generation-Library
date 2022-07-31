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
		
		/* If new predicate is not empty and not already added to the rule
		 * proceed to add its relevant arguments
		 */	
		predicate.setName(predicateIdentifier);
		if (predicate.getNameText() != "" && !this.containsPredicate(predicate, predicateRulePart)) {
		
			String predicateKey = Integer.toString(this.CurrentPredicateIndex++);
			
			/* Also add respective variables */	
			ArrayList<NLRGPredicateArgument> arguments = new ArrayList<NLRGPredicateArgument>();	
			for (String argumentIdentifier : argumentIdentifiers)
				arguments.add(new NLRGPredicateArgument(argumentIdentifier));
			
			addPredicateWithArguments(predicateKey, predicate, arguments, predicateRulePart);
			
			return 1;
		
		}
		else
			return 0;

	}



	public boolean headContains(NLRGPredicate testPred) {

		for (NLRGPredicate headPredicate : Head.values())
			if (headPredicate.sameNameAs(testPred.getName()))
				return true;

		return false;
	}
	

	public boolean bodyContains(NLRGPredicate testPred) {

		for (NLRGPredicate headPredicate : Body.values())
			if (headPredicate.sameNameAs(testPred.getName()))
				return true;

		return false;
	}

	
	
	public boolean containsPredicate(NLRGPredicate testPred, RulePart predicateRulePart) {

		if (predicateRulePart == RulePart.HEAD)
			return headContains(testPred);
		else
			return bodyContains(testPred);

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
	
}
