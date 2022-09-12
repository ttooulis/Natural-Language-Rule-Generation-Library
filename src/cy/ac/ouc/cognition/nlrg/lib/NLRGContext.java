package cy.ac.ouc.cognition.nlrg.lib;

import java.util.List;

public abstract class NLRGContext extends NLRGKnowledgeBaseElement {
	
	protected boolean ContextReady;
	


	NLRGContext() {
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
