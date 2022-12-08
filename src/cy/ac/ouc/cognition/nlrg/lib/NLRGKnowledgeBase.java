package cy.ac.ouc.cognition.nlrg.lib;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.errln;

public abstract class NLRGKnowledgeBase extends NLRGKnowledgeBaseElement {

	public int load(String knowledgeBaseString) {

		errln("Error loading Meta-level Knowledge Base: Cannot load from a generic class!");
		return 0;

	}


    public String getKBText() {

		errln("Error getting Meta-level Knowledge Base Text: Meta-Knowledge Base Content cannot be retrieved from a generic class!");
		return "Generic Text";

    }
    

	public String getKBVersion(String knowledgeBaseString) {

		errln("Error getting Meta-level Knowledge Base Version: Version context cannot be run on a generic class!");
		return "Generic Undefined";

	}
	
	
	public void runSentenceContext(String knowledgeBaseString, NLSentence sentence) throws NLRGMetaKBException {
		
		errln("Error inferring from Meta-level Knowledge Base: Sentence Context cannot be run on a generic class!");
    	throw new NLRGMetaKBException("Error inferring from Meta-level Knowledge Base: Sentence Context cannot be run on a generic class!");

	}

}
