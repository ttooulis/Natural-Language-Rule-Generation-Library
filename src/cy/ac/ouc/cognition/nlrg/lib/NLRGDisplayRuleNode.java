package cy.ac.ouc.cognition.nlrg.lib;

import java.util.ArrayList;

//import org.json.JSONObject;

public class NLRGDisplayRuleNode extends NLRGThing {
	
	private static String 					ls = NLRGParameterLib.NLRGThing_LineSeperator;

	public ArrayList<NLRGDisplayRuleNode>	ChainedMarkedRules;
	
	public String	Name;
	public String	Head;
	public String	Body;
	
	public NLRGDisplayRuleNode(String name, String head, String body) {
		Name = name;
		Head = head;
		Body = body;
		
		ChainedMarkedRules = new ArrayList<NLRGDisplayRuleNode>();
	}
	
	
    /**
	 * @return the chainedMarkedRules
	 */
	public ArrayList<NLRGDisplayRuleNode> getChainedMarkedRules() {
		return ChainedMarkedRules;
	}


	/**
	 * @param chainedMarkedRules the chainedMarkedRules to set
	 */
	public void setChainedMarkedRules(ArrayList<NLRGDisplayRuleNode> chainedMarkedRules) {
		ChainedMarkedRules = chainedMarkedRules;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}


	/**
	 * @return the head
	 */
	public String getHead() {
		return Head;
	}


	/**
	 * @param head the head to set
	 */
	public void setHead(String head) {
		Head = head;
	}


	/**
	 * @return the body
	 */
	public String getBody() {
		return Body;
	}


	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		Body = body;
	}


	public String buildText(int level) {
    	
    	String displayRuleString = "[" + Name + "]: " + Head + ";" + ls;
    	
		for (NLRGDisplayRuleNode displayRule : ChainedMarkedRules) {
			for (int i=0; i <= level; i++)
				displayRuleString += "    ";
			displayRuleString += displayRule.buildText(level+1);
		}

		if (level == 0) displayRuleString += ls;
		
		return displayRuleString;

    }
    

//    public String toJSONString(){
//        return new JSONObject(this).toString();
//    }


}
