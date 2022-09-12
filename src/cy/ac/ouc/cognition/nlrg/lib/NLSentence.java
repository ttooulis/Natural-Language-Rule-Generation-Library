package cy.ac.ouc.cognition.nlrg.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class NLSentence extends NLThing {

	private HashMap<Integer, NLToken>		Tokens;
	private List<NLDependency>				Dependencies;
	private List<NLPredicate>				Predicates;
	private	NLRGContext						Context;
	private NLRGRule						ExtractedRule;
	
	boolean									PredicatesGenerated = false;
	boolean									ContextBuilt = false;
	boolean									RuleSet = false;
	
	
	NLSentence(String sentenceText) {
		super(sentenceText);
		Tokens = new HashMap<Integer, NLToken>();
		Dependencies = new ArrayList<>();
		Predicates = new ArrayList<>();
	}
	
	
	
	public void addToken(int index, NLToken nlpToken) {
		Tokens.put(index, nlpToken);
		PredicatesGenerated = false;
		ContextBuilt = false;
		RuleSet = false;
		Complete = false;
	}
	
	public void addToken(int index, String originalText, String lemma, String ner, String tag) {
		this.addToken(index, new NLToken(index, originalText, lemma, ner, tag));
	}
	

	
	public void addDependency(NLDependency dependency) {
		Dependencies.add(dependency);
		PredicatesGenerated = false;
		ContextBuilt = false;
		RuleSet = false;
		Complete = false;
	}
	
	// CID - There could be inconsistency here
	public void addDependency(String dependencyName, NLToken governor, NLToken dependent) {
		this.addDependency(new NLDependency(	dependencyName,
				governor,
				dependent)
			);
	}
	
	public void addDependency(String dependencyName, int governorIndex, int dependentIndex) {
		this.addDependency(new NLDependency(	dependencyName,
												Tokens.get(governorIndex),
												Tokens.get(dependentIndex))
											);
	}
	
	
	
	public void generatePredicates() {
		
		if (Complete) {

			// Create the list of the part-of-speech tags for a sentence
			// Convert all text to lower case text
			// % CID - 02/08/2020 - Use Lemma instead of value of a word. This will give us the root of the word
			// % and no other processing will probably be required.
			for (NLToken nlToken : Tokens.values()) {
				
				String wordLemma = nlToken.getLemma().toLowerCase();
				boolean generatePredicate = true;
				String posTag = nlToken.getTag().toLowerCase();
				if (posTag.equals(".") || posTag.equals(",") || posTag.equals("!") || posTag.equals("?"))
					generatePredicate = false;
				
				if (NLRGParameterLib.NLRGMetaKB_PredicateMode == 0) {
					
					List<NLRGPredicateArgument>	Arguments = new ArrayList<NLRGPredicateArgument>();
					Arguments.add(new NLRGPredicateArgument(nlToken.getText().toLowerCase(), -1));
					Arguments.add(new NLRGPredicateArgument(posTag, -1));
					String nerTag = "nner";
					if (!nlToken.getNER().equals("O"))
						nerTag = "ner";
					Arguments.add(new NLRGPredicateArgument(nerTag, -1));
					Arguments.add(new NLRGPredicateArgument(nlToken.getNER().toLowerCase(), -1));
					Arguments.add(new NLRGPredicateArgument(wordLemma, nlToken.getIndex()));
	
					Predicates.add(new NLPredicate(
														NLPredicate.RelationTypeName[NLPredicate.RelationType.TOKEN.ordinal()],
														Arguments,
														NLPredicate.RelationType.TOKEN,
														generatePredicate
													)
										);
				}
				else {
					Predicates.add(new NLPredicate(
														NLPredicate.RelationTypeName[NLPredicate.RelationType.POS.ordinal()],
														nlToken.getTag().toLowerCase(),
														-1,
														wordLemma,
														nlToken.getIndex(),
														NLPredicate.RelationType.POS,
														generatePredicate
													)
											);
					
					if (!nlToken.getNER().equals("O"))
						Predicates.add(new NLPredicate(
															NLPredicate.RelationTypeName[NLPredicate.RelationType.NER.ordinal()],
															nlToken.getNER().toLowerCase(),
															-1,
															wordLemma,
															nlToken.getIndex(),
															NLPredicate.RelationType.NER,
															generatePredicate
														)
												);
				}
			
			}


			/* Add Dependency predicates */
			// % CID - 02/08/2020 - Use Lemma instead of value of a word. This will give us the root of the word
			// % and no other processing will probably be required.
			// % For the "TO BE" verb, some Meta-Rules need to be changed 
			for (NLDependency nlDependency : Dependencies) {

				boolean generatePredicate = true;
				String dependencyName = nlDependency.getDependencyName().toLowerCase();
				if (dependencyName.equals("punct"))
					generatePredicate = false;
				
				String governorName = nlDependency.getGovernor().getLemma().toLowerCase();
				if (governorName.equals(".") || governorName.equals(",") || governorName.equals("!") || governorName.equals("?"))
					governorName = "punct_symbol";

				String dependentName = nlDependency.getDependent().getLemma().toLowerCase();
				if (dependentName.equals(".") || dependentName.equals(",") || dependentName.equals("!") || dependentName.equals("?"))
					dependentName = "punct_symbol";

				
				Predicates.add(new NLPredicate(
														dependencyName,
														governorName,
														nlDependency.getGovernor().getIndex(),
														dependentName,
														nlDependency.getDependent().getIndex(),
														NLPredicate.RelationType.DEPNDENCY,
														generatePredicate
													)
										);

			}

		}
		
		PredicatesGenerated = true;

	}
	


	public void buildAndSetContext(NLRGContext context) {
		Context = context;
		if (PredicatesGenerated) {
			Context.BuildContext(Predicates);
			ContextBuilt = true;
		}
	}

	
	
	public boolean isContextBuilt( ) {
		return ContextBuilt;
	}


	
	public void setRule(NLRGRule extractedRule) {
		ExtractedRule = extractedRule;
		RuleSet = true;
	}

	public NLRGRule getRule() {
		return ExtractedRule;
	}

	public boolean isRuleSet( ) {
		return RuleSet;
	}


	
	/**
	 * @return the tokens
	 */
	public  HashMap<Integer, NLToken> getTokens() {
		return Tokens;
	}


	
	/**
	 * @return the dependencies
	 */
	public List<NLDependency> getDependencies() {
		return Dependencies;
	}


	
	/**
	 * @return the predicates
	 */
	public List<NLPredicate> getPredicates() {
		return Predicates;
	}


	
	/**
	 * @return the context
	 */
	public NLRGContext getContext() {
		return Context;
	}

}
