package cy.ac.ouc.cognition.nlrg.lib;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.errln;

public abstract class NLProcessor extends NLRGThing {
	
	protected boolean	DocumentAnnotated;
	protected boolean	NLProcessorLoaded;


	NLProcessor() {
		DocumentAnnotated = false;
		NLProcessorLoaded = false;
	}
	
	NLProcessor(boolean load) {
		this();
		if (load)
			load();
	}
	
	public void load() {
		NLProcessorLoaded = true;
	}

	public NLDocument annotateDocument(String nlText) {
		
		return new NLDocument(nlText);
		
	}

	public String generateParseData(NLDocument nlDocument) {
		errln("Error generating Parse Data: Cannot generate Parse Data from a generic class!");
		return "";
	}
	
	public void resetNLDocument() {
		DocumentAnnotated = false;
	}

	public void unload() {
		DocumentAnnotated = false;
		NLProcessorLoaded = false;
	}
	
	public boolean isLoaded( ) {
		return NLProcessorLoaded;
	}

	
	public boolean isAnnotated( ) {
		return DocumentAnnotated;
	}


}
