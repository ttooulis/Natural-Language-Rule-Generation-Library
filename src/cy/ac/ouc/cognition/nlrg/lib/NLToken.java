package cy.ac.ouc.cognition.nlrg.lib;

public class NLToken extends NLThing {
	
	/* NLPToken Attributes */
	private int		Index;
	private String	Lemma;
	private String	NER;
	private String	Tag;



	public NLToken(String originalText) {
		super(originalText);
	}


	NLToken(int index, String originalText, String lemma, String ner, String tag) {
		
		super(originalText);
		
		Index = index;
		Lemma = new String(lemma);
		NER = new String(ner);
		Tag = new String(tag);

		Complete = true;
		
	}


	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return Index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		Index = index;
		Complete = false;
	}

	
	
	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return Lemma;
	}

	/**
	 * @param lemma the lemma to set
	 */
	public void setLemma(String lemma) {
		Lemma = new String(lemma);
		Complete = false;
	}



	/**
	 * @return the nER
	 */
	public String getNER() {
		return NER;
	}

	/**
	 * @param nER the nER to set
	 */
	public void setNER(String ner) {
		NER = new String(ner);
		Complete = false;
	}



	/**
	 * @return the tag
	 */
	public String getTag() {
		return Tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		Tag = new String(tag);
		Complete = false;
	}

}
