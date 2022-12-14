package cy.ac.ouc.cognition.nlrg.lib;

import org.json.JSONPropertyIgnore;

public abstract class NLThing extends NLRGThing {

	protected String	Text;
	protected boolean	Complete;
	
	NLThing(String text) {
		Text = new String(text);
		Complete = false;
	}
	


	/**
	 * @return the text
	 */
	public  String getText() {
		return Text;
	}
	


	/**
	 * @@param text the text to set
	 */
	public void setText(String text) {
		Text = new String(text);
		Complete = false;
	}

	

	/**
	 * @return the complete
	 */
	@JSONPropertyIgnore
	public  boolean isComplete() {
		return Complete;
	}
	


	/**
	 * @@param complete the complete to set
	 */
	@JSONPropertyIgnore
	public void setComplete(boolean complete) {
		Complete = complete;
	}
	
}
