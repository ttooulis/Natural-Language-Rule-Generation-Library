package cy.ac.ouc.cognition.nlrg.lib;

import java.util.List;

public class PrudensContext extends NLRGContext {

	private static String 		ls = NLRGParameterLib.NLRGThing_LineSeperator;

	private String ContextText;
	
	public void BuildContext(List<NLPredicate> SentencePredicates) {

		/*
		 * ******************************************************** 
		 * Build Prudens Context
		 * ********************************************************
		 */

		for (NLPredicate nlpredicate : SentencePredicates) {
				String contextString = nlpredicate.toString();
				if (!contextString.isEmpty())
					ContextText += contextString + ";" + ls;
		}

		ContextReady = true;
		
	}
	
	
	
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
