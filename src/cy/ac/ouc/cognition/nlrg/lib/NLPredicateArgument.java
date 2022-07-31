package cy.ac.ouc.cognition.nlrg.lib;

public class NLPredicateArgument extends NLRGPredicateArgument {

	private	String	Tag;


	
	public NLPredicateArgument(String  name, int index, String tag) {
		super(name, index);
		Tag = new String(tag);
	}

	public NLPredicateArgument(String  name, String tag) {
		this(name, -1, tag);
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
	}

}
