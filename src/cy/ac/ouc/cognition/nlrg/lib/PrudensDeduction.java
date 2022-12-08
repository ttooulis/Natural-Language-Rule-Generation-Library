package cy.ac.ouc.cognition.nlrg.lib;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONPropertyIgnore;

import coaching.Literal;
import coaching.Rule;

public class PrudensDeduction extends NLRGDeduction {

	private ArrayList<Rule>						PrudensMarkedRules;
	private ArrayList<Rule>						ExtractionMarkedRules;

	private	ArrayList<NLRGDisplayRuleNode>		MarkedRules;
	private	ArrayList<NLRGDisplayRuleNode>		MarkedRulesChain;

	private boolean								PrudensMarkedRulesSet = false;
	private boolean								MarkedRulesBuilt = false;
	private boolean								MarkedRulesChainBuilt = false;
	

	PrudensDeduction() {
		ExtractionMarkedRules = new ArrayList<Rule>();
		MarkedRulesChain = new ArrayList<NLRGDisplayRuleNode>();
		MarkedRules = new ArrayList<NLRGDisplayRuleNode>();
	}

	
	/**
	 * @return the prudensMarkedRules
	 */
	@JSONPropertyIgnore
	public ArrayList<Rule> getPrudensMarkedRules() {
		return PrudensMarkedRules;
	}



	/**
	 * @param prudensMarkedRules the prudensMarkedRules to set
	 */
	@JSONPropertyIgnore
	public void setPrudensMarkedRules(ArrayList<Rule> prudensMarkedRules) {
		PrudensMarkedRules = prudensMarkedRules;
		PrudensMarkedRulesSet = true;
	}

	

	private void buildMarkedRulesChain(Iterator<Rule> extractionMarkedRules, ArrayList<NLRGDisplayRuleNode> markedRulesChain) {
		
		if (!extractionMarkedRules.hasNext())
	    	return;

		
		Rule extractionRule = extractionMarkedRules.next();

		NLRGDisplayRuleNode newDisplayRuleNode = new NLRGDisplayRuleNode(
																			extractionRule.getName(),
																			extractionRule.getHead().toString(),
																			extractionRule.getBody().toString()
																		);
		markedRulesChain.add(newDisplayRuleNode);

		
		ArrayList<Rule> bodyMarkedRules = new ArrayList<Rule>();

		// Search for marked rules from body predicates
		for (Literal bodyLiteral : extractionRule.getBody())
			for (Rule rule : PrudensMarkedRules)
				if (bodyLiteral.coincidesWith(rule.getHead(), true))
					bodyMarkedRules.add(rule);
		
		// Build deduction list for marked rules from body predicates, if there are any
		// if (bodyMarkedRules.size() > 0) // Should be OK without this because recursion ends when there is no data in list
			buildMarkedRulesChain(bodyMarkedRules.iterator(), newDisplayRuleNode.ChainedMarkedRules);

		// Continue with the next rule
		buildMarkedRulesChain(extractionMarkedRules, markedRulesChain);

	}
	
	
	public ArrayList<NLRGDisplayRuleNode> getMarkedRulesChain(Rule markedRule) {

		ArrayList<NLRGDisplayRuleNode> markedRuleChain = new ArrayList<NLRGDisplayRuleNode>();
		
		if (PrudensMarkedRulesSet) {
			
			ArrayList<Rule> ruleArray = new ArrayList<Rule>();
			ruleArray.add(markedRule);
			
			buildMarkedRulesChain(ruleArray.iterator(), markedRuleChain);
		
		}
		
		return markedRuleChain;

	}

	
	public ArrayList<NLRGDisplayRuleNode> getMarkedRulesChain() {

		if (PrudensMarkedRulesSet) {
			
			if (!MarkedRulesChainBuilt) {
				buildMarkedRulesChain(ExtractionMarkedRules.iterator(), MarkedRulesChain);
				MarkedRulesChainBuilt = true;
			}
		
			return MarkedRulesChain;
		}
		
		return new ArrayList<NLRGDisplayRuleNode>();

	}

	
	private void buildMarkedRules(Iterator<Rule> prudensMarkedRules, ArrayList<NLRGDisplayRuleNode> markedRules) {
		
		if (!prudensMarkedRules.hasNext())
	    	return;

		
		Rule extractionRule = prudensMarkedRules.next();

		NLRGDisplayRuleNode newDisplayRuleNode = new NLRGDisplayRuleNode(
																			extractionRule.getName(),
																			extractionRule.getHead().toString(),
																			extractionRule.getBody().toString()
																		);
		markedRules.add(newDisplayRuleNode);

		
		// Continue with the next rule
		buildMarkedRules(prudensMarkedRules, markedRules);

	}
	
	
	public ArrayList<NLRGDisplayRuleNode> getMarkedRules() {

		if (PrudensMarkedRulesSet) {
			
			if (!MarkedRulesBuilt) {
				buildMarkedRules(PrudensMarkedRules.iterator(), MarkedRules);
				MarkedRulesBuilt = true;
			}
		
			return MarkedRules;
		}
		
		return new ArrayList<NLRGDisplayRuleNode>();

	}

	
	@JSONPropertyIgnore
    public String getMarkedRulesChainText() {
	
    	getMarkedRulesChain();
    	
    	String markedRulesChainString = "";
    	
		for (NLRGDisplayRuleNode displayRule : MarkedRulesChain)
			markedRulesChainString += displayRule.buildText(0);
		
		return markedRulesChainString;

    }

	
	@JSONPropertyIgnore
    public String getMarkedRulesText() {
    	
    	getMarkedRules();
    	
    	String markedRulesString = "";
    	
		for (NLRGDisplayRuleNode displayRule : MarkedRules)
			markedRulesString += displayRule.buildText(0);
		
		return markedRulesString;

    }

	
//    public String toJSONString() {
//    	  	
//    	String displayRuleJSONString = "";
//    	
//    	getMarkedRulesChain();
//		for (NLRGDisplayRuleNode displayRule : MarkedRulesChain)
//			displayRuleJSONString += displayRule.toJSONString();
//		displayRuleJSONString += '\n';
//		
//    	getMarkedRules();
//		for (NLRGDisplayRuleNode displayRule : MarkedRules)
//			displayRuleJSONString += displayRule.toJSONString();
//		
//		return displayRuleJSONString;
//
//    }
//
//	
	public void addToUsedMetaRules(Rule extractionRule) {
		
		for (Rule rule : ExtractionMarkedRules)
			if (extractionRule.getHead().coincidesWith(rule.getHead(), true))
				return;

		ExtractionMarkedRules.add(extractionRule);
	}

}
