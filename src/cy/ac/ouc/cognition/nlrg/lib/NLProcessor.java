package cy.ac.ouc.cognition.nlrg.lib;


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

	public String generateParseData() {
		
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
