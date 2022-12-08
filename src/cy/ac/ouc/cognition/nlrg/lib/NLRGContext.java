package cy.ac.ouc.cognition.nlrg.lib;

import java.util.List;

public abstract class NLRGContext extends NLRGKnowledgeBaseElement {
	
	protected String ContextText;
	
	protected boolean ContextReady;
	


	NLRGContext() {
		ContextText = "";
		ContextReady = false;
	}

	public void BuildContext(List<NLPredicate> SentencePredicates) {

	}
	
	public String getContextTextData() {
		return "";
	}

	
	
	/**
	 * @return the contextReady
	 */
	public boolean isContextReady() {
		return ContextReady;
	}

}
