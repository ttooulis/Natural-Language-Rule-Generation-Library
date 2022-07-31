package cy.ac.ouc.cognition.nlrg.lib;

import java.util.ArrayList;
import java.util.List;

public class NLDocument extends NLThing {

	private List<NLSentence>	DocumentSentences;
	
	
	
	NLDocument(String documentText) {
		super(documentText);
		DocumentSentences = new ArrayList<>();
	}
	
	
	
	public void addSentence(NLSentence sentence) {
		DocumentSentences.add(sentence);
		Complete = false;
	}



	/**
	 * @return the documentSentences
	 */
	public List<NLSentence> getDocumentSentences() {
		return DocumentSentences;
	}

}
