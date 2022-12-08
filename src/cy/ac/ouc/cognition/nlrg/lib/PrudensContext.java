package cy.ac.ouc.cognition.nlrg.lib;

import java.util.List;

import org.json.JSONPropertyIgnore;

public class PrudensContext extends NLRGContext {

	private static String 		ls = NLRGParameterLib.NLRGThing_LineSeperator;

	public void BuildContext(List<NLPredicate> SentencePredicates) {

		/*
		 * ******************************************************** 
		 * Build Prudens Context
		 * ********************************************************
		 */

		for (NLPredicate nlpredicate : SentencePredicates) {
				String contextString = nlpredicate.toString();
				if (contextString != null && !contextString.isEmpty())
					ContextText += contextString + ";" + ls;
		}

		ContextReady = true;
		
	}
	
	
	
	@JSONPropertyIgnore
	public String getContextTextData() {

		String ContextTextData = "";
		
		ContextTextData += ContextText + ls;

		return ContextTextData;	

	}



	/**
	 * @return the contextText
	 */
	public String getContextText() {
		return ContextText;
	}

}
