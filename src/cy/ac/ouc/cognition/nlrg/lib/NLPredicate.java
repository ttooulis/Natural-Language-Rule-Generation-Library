package cy.ac.ouc.cognition.nlrg.lib;

import java.util.List;


public class NLPredicate extends NLRGPredicate {

	static enum RelationType {
		DEPNDENCY, POS, NER, TOKEN, UNDEFINED;
	}
	
	static String[]		RelationTypeName = {"", "pos", "ner", "token", ""};
	
	private		RelationType	Type;


	public NLPredicate(	String name,
						NLPredicateArgument firstArgument,
						NLPredicateArgument secondArgument,
						RelationType type,
						boolean generate
					) {
		super(name, generate);
		Arguments.add(firstArgument);
		Arguments.add(secondArgument);
		Type = type;
	}
	
	public NLPredicate(String name, List<NLRGPredicateArgument> arguments, RelationType type, boolean generate) {
		super(name, arguments, generate);
		Type = type;
	}
	
	public NLPredicate(	String name,
						String firstArgName, int firstArgIndex, String firstArgTag,
						String secondArgName, int secondArgIndex, String secondArgTag,
						RelationType type,
						boolean generate
					) {
		super(name, generate);
		Arguments.add(new NLPredicateArgument(firstArgName, firstArgIndex, firstArgTag));
		Arguments.add(new NLPredicateArgument(secondArgName, secondArgIndex, secondArgTag));
		Type = type;
	}

	public NLPredicate(	String name,
						String firstArgName, int firstArgIndex,
						String secondArgName, int secondArgIndex,
						RelationType type,
						boolean generate
					) {
		super(name, generate);
		Arguments.add(new NLRGPredicateArgument(firstArgName, firstArgIndex));
		Arguments.add(new NLRGPredicateArgument(secondArgName, secondArgIndex));
		Type = type;
	}



	/**
	 * @return the type
	 */
	public RelationType getType() {
		return Type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RelationType type) {
		Type = type;
	}

}
