package cy.ac.ouc.cognition.nlrg.lib;

import java.util.List;

public abstract class NLRGQuery extends NLRGKnowledgeBaseElement {
	
	protected boolean QueryReady;
	


	NLRGQuery() {
		QueryReady = false;
	}

	public void BuildQuery(List<NLPredicate> SentencePredicates) {

	}
	
	public String getQueryTextData() {
		return "";
	}

	
	
	/**
	 * @return the queryReady
	 */
	public boolean isQueryReady() {
		return QueryReady;
	}

}
