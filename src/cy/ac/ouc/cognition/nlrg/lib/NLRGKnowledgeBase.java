package cy.ac.ouc.cognition.nlrg.lib;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.errln;

public abstract class NLRGKnowledgeBase extends NLRGKnowledgeBaseElement {

	public int load(String knowledgeBaseString) {

		errln("Error loading Meta-level Knowledge Base: Cannot load from a generic class!");
		return 0;

	}


	public String getKBVersion(String knowledgeBaseString) {

		errln("Error getting Meta-level Knowledge Base Version: Version Query cannot be run on a generic class!");
		return "Generic Undefined";
	}
	
	
	public void runExtractionQueryOnKB(String knowledgeBaseString, NLRGQuery queryToRun, NLRGRule extractedRule) throws NLRGMetaKBException {
		
		errln("Error querying Meta-level Knowledge Base: Extraction Query cannot be run on a generic class!");
    	throw new NLRGMetaKBException("Error querying Meta-level Knowledge Base: Extraction Query cannot be run on a generic class!");

	}

}
