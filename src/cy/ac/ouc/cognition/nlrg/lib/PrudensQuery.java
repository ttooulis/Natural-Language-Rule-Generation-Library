package cy.ac.ouc.cognition.nlrg.lib;

import java.util.List;

public class PrudensQuery extends NLRGQuery {

	private static String 		ls = NLRGParameterLib.NLRGThing_LineSeperator;

	private String[] QueryText;
	
	public void BuildQuery(List<NLPredicate> SentencePredicates) {

		/*
		 * ******************************************************** 
		 * Build Prolog Query for MetaKnowledge Base
		 * ********************************************************
		 */


		/* Create query string */
		QueryText = new String[3];
		QueryText[0] = ""; QueryText[1] = ""; QueryText[2] = "";
		
		
		for (NLPredicate nlpredicate : SentencePredicates) {
				String queryString = nlpredicate.toString();
				if (!queryString.isEmpty())
					QueryText[0] += queryString + ";" + ls;
		}

		QueryReady = true;
		
	}
	
	
	
	public String getQueryTextData() {

		String QueryTextData = "";
		
		for (int i=0; i < 3; i++)
			if (QueryText[i] != "")
				QueryTextData += QueryText[i] + ls;

		return QueryTextData;	

	}



	/**
	 * @return the queryText
	 */
	public String[] getQueryText() {
		return QueryText;
	}

}
