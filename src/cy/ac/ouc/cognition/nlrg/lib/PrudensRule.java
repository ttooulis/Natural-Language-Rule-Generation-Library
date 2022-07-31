package cy.ac.ouc.cognition.nlrg.lib;

import java.util.HashMap;

import cy.ac.ouc.cognition.nlrg.lib.NLRGPredicate.RulePart;

public class PrudensRule extends NLRGRule {


	
	public PrudensRule() {
		this("");
	}

	
	public PrudensRule(String name) {
		super(name);
	}

	

	public String generatePredicateSetText(RulePart predicateRulePart) {

		HashMap<String, NLRGPredicate> predicateSet;
		
		if (predicateRulePart == RulePart.HEAD)
			predicateSet = Head;
		else
			predicateSet = Body;


		String setString = "";
        for (NLRGPredicate setPredicate : predicateSet.values()) {

        	String predicateString = setPredicate.toString();
        	
       		if (!setString.isEmpty())
       			setString += ", ";
       		
       		setString += predicateString;

        }
        
        return setString;
	}



	
    public String generateRuleText() {

    	String ruleText = "";
    	
    	if (Complete) {

    		String bodyText = generatePredicateSetText(RulePart.BODY);

    		if (!bodyText.isEmpty())
    			ruleText +=	bodyText + " " + NLRGParameterLib.NLRGRule_NeckSymbol + " ";
    		
    		String headText = generatePredicateSetText(RulePart.HEAD);
    		
    		if (!headText.isEmpty())
    			ruleText += headText + ";";

    		if (NLRGParameterLib.NLRGRule_GenerateName == 1)
    			ruleText =	Name + " " + NLRGParameterLib.NLRGRule_NameSeperator + " " + ruleText;

    	}

	    return ruleText;
	    
	} 

	  

}
