package cy.ac.ouc.cognition.nlrg.lib;


import java.util.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.outln;


public class StanfordCoreNLProcessor extends NLProcessor {

    private static Properties		CoreNLPProperties;
	private static StanfordCoreNLP	CoreNLPPipeline;
	private CoreDocument			CoreNLPDocument;
	


	public void load() {
		
		/*
		 * ***************************** 
		 * Load Stanford CoreNLP System
		 * *****************************
		 */

		// Set up pipeline properties
		CoreNLPProperties = new Properties();
	
		// Set the list of annotators to run
		CoreNLPProperties.setProperty("annotators", NLRGParameterLib.StanfordCoreNLProcessor_Annotators);

		// Set a property for coref algorithm annotator 
		CoreNLPProperties.setProperty("coref.algorithm", NLRGParameterLib.StanfordCoreNLProcessor_Algorithm);
		
		// Set a property for how to split document to sentences 
		CoreNLPProperties.setProperty("ssplit.eolonly", NLRGParameterLib.StanfordCoreNLProcessor_Split);
		
		CoreNLPProperties.setProperty("ner.useSUTime", "0");

		// Build pipeline
		CoreNLPPipeline = new StanfordCoreNLP(CoreNLPProperties);
		
		outln(NLRGTrace.TraceLevel.IMPORTANT, "Stanford Core NLP Properties Set: " + CoreNLPProperties.toString());

		super.load();

	}



	public NLDocument annotateDocument(String nlText) {
		
		// Create a document object
		CoreNLPDocument = new CoreDocument(nlText);

		// Annotate the document
		CoreNLPPipeline.annotate(CoreNLPDocument);
		DocumentAnnotated  = true;
		
		/*
		 * *******************************************
		 * Process document and generate predicates
		 * *******************************************
		 */
		
		NLDocument nlDocument = new NLDocument(nlText);

		for (CoreSentence CoreNLPSentence : CoreNLPDocument.sentences()) {
			
			NLSentence  nlSentence = new NLSentence(CoreNLPSentence.text());
			// CID - MAYBE RETHINK "ROOT" TOKEN AND DEPENDENCY
			nlSentence.addToken(0, "root", "root", "O", "ROOT");

			/* Get Token Information */
			for (CoreLabel CoreNLPToken : CoreNLPSentence.tokens()) {

				String lemma = CoreNLPToken.lemma();
				if (lemma == null)
					lemma = CoreNLPToken.originalText();

				/* In case NER annotator is not loaded set to default value */
				String nerTag = CoreNLPToken.ner();
				if (nerTag == null)
					nerTag = "O";

				String tag = CoreNLPToken.tag();
				if (tag == null)
					tag = "UNKN";

				nlSentence.addToken(	CoreNLPToken.index(),
										CoreNLPToken.originalText(),
										lemma,
										nerTag,
										tag
									);
			}


			/* If Dependency Parser Annotator is set */
			/* CID - Find a way to make a better check! */
			if (CoreNLPProperties.toString().contains("depparse")) {
				/* Get Dependency Information */
				SemanticGraph dependencyParse = CoreNLPSentence.dependencyParse();
	
				IndexedWord root = dependencyParse.getFirstRoot();

				// CID - MAYBE RETHINK "ROOT" TOKEN AND DEPENDENCY
				nlSentence.addDependency("root", 0, root.index());
				
				for (SemanticGraphEdge edge : dependencyParse.edgeListSorted()) {
					
					nlSentence.addDependency(	edge.getRelation().getShortName().toLowerCase(),
												edge.getGovernor().index(),
												edge.getDependent().index()
											);
				}
			}

			nlSentence.setComplete(true);
			nlDocument.addSentence(nlSentence);
			
		}
		
		nlDocument.setComplete(true);

		return nlDocument;

	}

	
	
	/* CID - PORTED AS IS FROM PREVIOUS VERSION. NEEDS TO BE TAKEN CARE!! */
	public String generateParseData() {

		/*
		 * *******************************************
		 * Process (print annotators output) document 
		 * *******************************************
		 */
		String ParseDataText = "";
		
		if (DocumentAnnotated) {
			String ls = NLRGParameterLib.NLRGThing_LineSeperator;
	
			/* Create predicate string */
			ParseDataText += "Document Sentences:" + ls;
			ParseDataText += "-------------------" + ls + ls;
	
			int count = 0;
			for (CoreSentence sentence : CoreNLPDocument.sentences()) {
		
				count++;
				String sentenceText = sentence.text();
				ParseDataText += "Sentence " + count + " : " + sentenceText + ls;
			
				// Print sentence tokens
				ParseDataText += "Tokens: " + ls;
				for (CoreLabel token : sentence.tokens()) {

					ParseDataText += "Category:\t\t" + token.category() + ls;
					ParseDataText += "index:\t\t" + token.index() + ls;
					ParseDataText += "DocID:\t\t" + token.docID() + ls;
					ParseDataText += "Lemma:\t\t" + token.lemma() + ls;
					ParseDataText += "NER:\t\t" + token.ner() + ls;
					ParseDataText += "OriginalText:\t" + token.originalText() + ls;
					ParseDataText += "Tag:\t\t" + token.tag() + ls;
					ParseDataText += "Value:\t\t" + token.value() + ls;
					ParseDataText += "Word:\t\t" + token.word() + ls;
					ParseDataText += "BeginPosition:\t" + token.beginPosition() + ls;
					ParseDataText += "EndPosition:\t" + token.endPosition() + ls;
				}
				ParseDataText += ls + ls;
			
				/* If Dependency Parser Annotator is set */
				/* CID - Find a way to make a better check! */
				if (CoreNLPProperties.toString().contains("depparse")) {
					// Print dependency parse for the sentence
					ParseDataText += "Dependency Parse:" + ls;
					SemanticGraph dependencyParse = sentence.dependencyParse();
					ParseDataText += dependencyParse + ls;
					ParseDataText += "Dependency Parse (CompactString):" + ls;
					ParseDataText += dependencyParse.toCompactString() + ls;
					ParseDataText += "Dependency Parse (DotFormat):" + ls;
					ParseDataText += dependencyParse.toDotFormat() + ls;
					ParseDataText += "Dependency Parse (FormattedString):" + ls;
					ParseDataText += dependencyParse.toFormattedString() + ls;
					ParseDataText += "Dependency Parse (List):" + ls;
					ParseDataText += dependencyParse.toList() + ls;
					ParseDataText += "Dependency Parse (POSList):" + ls;
					ParseDataText += dependencyParse.toPOSList() + ls;
		
					ParseDataText += "Root: " + ls;
					IndexedWord root = dependencyParse.getFirstRoot();
					ParseDataText += "\troot (after):\t\t" + root.after() + ls;
					ParseDataText += "\troot (before):\t\t" + root.before() + ls;
					ParseDataText += "\troot (docID):\t\t" + root.docID() + ls;
					ParseDataText += "\troot (index):\t\t" + root.index() + ls;
					ParseDataText += "\troot (lemma):\t\t" + root.lemma() + ls;
					ParseDataText += "\troot (ner):\t\t" + root.ner() + ls;
					ParseDataText += "\troot (tag):\t\t" + root.tag() + ls;
					ParseDataText += "\troot (value):\t\t" + root.value() + ls;
					ParseDataText += "\troot (word):\t\t" + root.word() + ls;
		
					ParseDataText += "Edges: " + ls;
					for (SemanticGraphEdge edge : dependencyParse.edgeListSorted()) {
						ParseDataText += "Edge:\t\t" + edge.toString() + ls;
						ParseDataText += "Dependent:\t\t" + edge.getDependent().originalText() + ls;
						ParseDataText += "\t\tDependent (after):\t\t" + edge.getDependent().after() + ls;
						ParseDataText += "\t\tDependent (before):\t\t" + edge.getDependent().before() + ls;
						ParseDataText += "\t\tDependent (docID):\t\t" + edge.getDependent().docID() + ls;
						ParseDataText += "\t\tDependent (index):\t\t" + edge.getDependent().index() + ls;
						ParseDataText += "\t\tDependent (lemma):\t\t" + edge.getDependent().lemma() + ls;
						ParseDataText += "\t\tDependent (ner):\t\t" + edge.getDependent().ner() + ls;
						ParseDataText += "\t\tDependent (tag):\t\t" + edge.getDependent().tag() + ls;
						ParseDataText += "\t\tDependent (value):\t\t" + edge.getDependent().value() + ls;
						ParseDataText += "\t\tDependent (word):\t\t" + edge.getDependent().word() + ls;
						ParseDataText += "Governor:\t\t" + edge.getGovernor().originalText() + ls;
						ParseDataText += "Relation Short Name:\t\t" + edge.getRelation().getShortName() + ls;
						ParseDataText += "Relation Long Name:\t\t" + edge.getRelation().getLongName() + ls;
						ParseDataText += "\t\tRelation (specific):\t\t" + edge.getRelation().getSpecific() + ls;
						ParseDataText += "\t\tRelation (toString):\t\t" + edge.getRelation().toString() + ls;
						ParseDataText += "Source:\t\t" + edge.getSource().originalText() + ls;
						ParseDataText += "Target:\t\t" + edge.getTarget().originalText() + ls;
		
					}
				}
	
				// Print the list of the part-of-speech tags for the sentence
				ParseDataText += "List of the part-of-speech tags: ";
				List<String> posTags = sentence.posTags();
				ParseDataText += posTags + ls + ls;
				
				/* Annotators ner and lemma required! */
				// Print the list of the ner tags for the sentence
				ParseDataText += "List of the ner tags: ";
				List<String> nerTags = sentence.nerTags();
				ParseDataText += nerTags + ls + ls;
	
			
				// Print constituency parse for the sentence
				ParseDataText += "Constituency parse: ";
				Tree constituencyParse = sentence.constituencyParse();
				ParseDataText += constituencyParse + ls + ls;
			
				/* Annotators ner and lemma required! */
			    // Print entity mentions in the sentence
				ParseDataText += "Entity mentions: ";
				List<CoreEntityMention> entityMentions = sentence.entityMentions();
				ParseDataText += entityMentions + ls + ls;
	
				
				/* Annotators ner, lemma and coref required!
				 * CAUSES OUT OF MEMORY ERROR. NOT TRIED!
			    // Print coreference between entity mentions
			    ParseDataText += "Coreference between entity mentions: ";
				for (CoreEntityMention originalEntityMention : sentence.entityMentions()) {
					ParseDataText += "[" + originalEntityMention + " ";
					ParseDataText += originalEntityMention.canonicalEntityMention().get() + " ]";
				}
			    ParseDataText += ls;
			    */
	
				ParseDataText += "***********************************************************" + ls + ls;
	
			}	// Document sentence processing end
	
	
			/* Print document wide coref info */
			/* Annotators ner, lemma and coref required!
			 * CAUSES OUT OF MEMORY ERROR. NOT TRIED!
		    Map<Integer, CorefChain> corefChains = document.corefChains();
		    ParseDataText += "Document coref chains:" + ls;
		    ParseDataText += corefChains + ls;
		    */
	
			
			/* Print quote annotators */
			/* Annotators ner, lemma and quote required!
			 * CAUSES OUT OF MEMORY ERROR. NOT TRIED!
			ParseDataText += "Document Quotes:" + ls;
			ParseDataText += "----------------" + ls + ls;
	
			count = 0;
			for (CoreQuote quote : document.quotes()) {
		
				count++;
	
				// Print quotes in document
				ParseDataText += "Quote " + count + ":" + quote + ls + ls;
				
			    // Print original speaker of quote
			    // Note that quote.speaker() returns an Optional
			    ParseDataText += "Original Speaker:" + quote.speaker().get( + ls + ls;
	
			    // Print canonical speaker of quote
			    ParseDataText += "Canonical Speaker:" + quote.canonicalSpeaker().get() +  + ls + ls + ls;
	
			} // Quote processing end
			*/
		}
	    
		return ParseDataText;
		
	} // public String Generate end



	public void resetNLDocument() {

		CoreNLPDocument = null;

		super.resetNLDocument();

	}



	public void unload() {

		resetNLDocument();

		CoreNLPProperties.clear();
		CoreNLPProperties = null;
		CoreNLPPipeline = null;
		
		super.unload();
	}

}
