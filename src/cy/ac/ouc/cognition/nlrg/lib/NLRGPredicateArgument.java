package cy.ac.ouc.cognition.nlrg.lib;

import java.util.ArrayList;
import java.util.List;

public class NLRGPredicateArgument extends NLRGKnowledgeBaseElement {

	protected List<String>		Name;
	protected int				Index;


	NLRGPredicateArgument(String  name, int index) {
		Name = new ArrayList<String>();
		Name.add(new String(name));
		Index = index;
	}
	
	NLRGPredicateArgument(String  name) {
		this(name, -1);
	}
	
	public NLRGPredicateArgument(List<String> name, int index) {
		Name = name;
		Index = index;
	}
	
	public NLRGPredicateArgument(List<String> name) {
		this(name, -1);
	}



    /**
	 * @return the name
	 */
	public List<String> getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(List<String> name) {
		Name = name;
	}



	public String getNameText( ) {
		
		String nameText = "";
		int i = 0;
		
		for (String name : Name) {
			i++;
			if (i==1)
				nameText += name;
			else
				nameText += NLRGParameterLib.NLRGRule_PredNameConcatChar + name;
		}
		
		return nameText;

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
	}



	public String toString() {
    	
    	String ArgumentName;
    	
   		ArgumentName = getNameText();

    	if (Index >=0)
    		ArgumentName += ", "  + Index;

    	return ArgumentName;
    	
    }

}
