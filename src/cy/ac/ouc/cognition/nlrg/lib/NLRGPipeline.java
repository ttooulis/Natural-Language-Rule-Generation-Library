package cy.ac.ouc.cognition.nlrg.lib;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.outln;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.errln;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.TL;

public class NLRGPipeline extends NLRGThing {
	
	private static String 		ls = NLRGParameterLib.NLRGThing_LineSeperator;
	
	private NLProcessor			NLDocumentProcessor;
	private NLDocument			ProcessedDocument;
	private NLRGKnowledgeBase	MetaKnowledgeBase;
	
	private boolean				NLProcessorSet = false;
	private boolean				MetaKnowledgeBaseSet = false;


	public NLRGPipeline() throws NLRGException {
		
		NLRGParameterLib.Load("NLRG.xml");
		setNLProcessor();
		setMetaKnowledgeBase();
		
	}


	
	private void setNLProcessor() {

		if (NLRGParameterLib.NLRGPipeline_NLProcessor == "corenlp") {
			NLDocumentProcessor = new StanfordCoreNLProcessor();
			NLProcessorSet = true;
		}

		else {
        	errln(NLRGParameterLib.NLRGPipeline_NLProcessor + " type is not supported for natural language processing");
        	NLProcessorSet = false;
		}

	}



	public void loadNLProcessor() {

		if (NLProcessorSet)
			NLDocumentProcessor.load();

		else {
        	errln(NLRGParameterLib.NLRGPipeline_NLProcessor + " natural language processor is not set correctly!");
        	NLDocumentProcessor.NLProcessorLoaded = false;
		}

	}



	public void resetDocument() {

		ProcessedDocument = null;

		if (NLProcessorSet && NLDocumentProcessor.isLoaded())
			NLDocumentProcessor.resetNLDocument();

	}



	public void unloadNLProcessor() {

		resetDocument();

		if (NLProcessorSet && NLDocumentProcessor.isLoaded())
			NLDocumentProcessor.unload();;

	}
	


	public void resetNLProcessor() {

		unloadNLProcessor();
		setNLProcessor();

	}
	


	private void setMetaKnowledgeBase() {

		if (NLRGParameterLib.NLRGPipeline_MetaKBType.equals("prudens")) {
			MetaKnowledgeBase = new PrudensKnowledgeBase(NLRGParameterLib.NLRGPipeline_MetaKnowledgeBaseFile);
			MetaKnowledgeBaseSet = true;
		}

		else
        	errln(NLRGParameterLib.NLRGPipeline_MetaKBType + " type is not supported for meta-level Knowledge Base");

	}



	public int loadMetaKnowledgeBase(String knowledgeBaseString) {

		if (MetaKnowledgeBaseSet) {
			int loadResult = MetaKnowledgeBase.load(knowledgeBaseString);
			if (loadResult != 0)
				outln(TL, "Meta-Knowledge Base File (" + NLRGParameterLib.NLRGPipeline_MetaKnowledgeBaseFile + ") loaded successfuly");
			else
				errln("Meta-Knowledge Base File (" + NLRGParameterLib.NLRGPipeline_MetaKnowledgeBaseFile + ") load error: " + loadResult);
			return loadResult;
		}
		else {
			errln("Cannot load Meta-Knowledge Base File (" + NLRGParameterLib.NLRGPipeline_MetaKnowledgeBaseFile + "). NLRGKnowledgeBase Object not set");
			return 0;
		}

	}



	public String getMetaKnowledgeBaseText() {

		if (MetaKnowledgeBaseSet) {

			if (NLRGParameterLib.NLRGPipeline_KnowledgeBaseType == "prudens")

				return ((PrudensKnowledgeBase) MetaKnowledgeBase).getKBText();

			else {
				outln(TL, "Meta-Knowledge Base Content cannot be retrieved for this type of Knowledge Base");
				return "";
			}
		}
		else {
			errln("Cannot retreive Meta-Knowledge Base File (" + NLRGParameterLib.NLRGPipeline_MetaKnowledgeBaseFile + "). NLRGKnowledgeBase Object not set");
			return "";
		}

	}



	public String getMetaKBVersion(String knowledgeBaseString) {

		if (MetaKnowledgeBaseSet)
			return MetaKnowledgeBase.getKBVersion(knowledgeBaseString);

		return "Meta-KB Not Set";

	}


        
        
	public void processNL(String nlText) {
		
		if (NLProcessorSet && NLDocumentProcessor.isLoaded()) {
			resetDocument();
			ProcessedDocument = NLDocumentProcessor.annotateDocument(nlText);
		}

		else
        	errln("Cannot do NLP on document. " + NLRGParameterLib.NLRGPipeline_NLProcessor + " NL processor not set or not loaded!");

	}



	public String getParseData() {

		if (NLProcessorSet && NLDocumentProcessor.isAnnotated())
			return NLDocumentProcessor.generateParseData();

		else
        	errln("Cannot get NLP data. " + NLRGParameterLib.NLRGPipeline_NLProcessor + " NL processor not set or document not annotated!");
		
		return "";

	}
	
	
	
	public void generateMetaPredicates() {
		
		if (ProcessedDocument != null && ProcessedDocument.isComplete())
			for (NLSentence nlSentence : ProcessedDocument.getDocumentSentences())
				nlSentence.generatePredicates();
		else
        	errln("Cannot generate meta-predicates. Document processing not completed!");

	}



	public String getMetaPredicateTextData() {

		/*
		 * ******************************************************** 
		 * Get Meta-Predicate Data String
		 * ********************************************************
		 */
		
		String metaPredicateText = "";

		if (ProcessedDocument != null && ProcessedDocument.isComplete()) {

			int count = 0;
			for (NLSentence nlSentence : ProcessedDocument.getDocumentSentences()) {
			
				count++;
				metaPredicateText += "Sentence " + count + " : " + nlSentence.getText() + ls;
				metaPredicateText += "************************************************" + ls;

				/* Create meta-predicate text */
				for (NLPredicate nlpredicate : nlSentence.getMetaPredicates()) {

					// CID - Re-think Query and Predicate class hierarchy a little bit
					// and how to generalize Knowledge Base types
					String predicateString;
					predicateString = nlpredicate.toString();

					if (!predicateString.isEmpty())
							metaPredicateText += predicateString + ";" + ls;

				}
			}
		}
		
		else
        	errln("Cannot get meta-predicate text data. Document processing not completed!");

		return metaPredicateText;

	}

	
	
	// CID - Re-think Query and Predicate class hierarchy a little bit and how to generalize Knowledge Base types
	public void buildMetaQueries() {
		
		if (ProcessedDocument != null && ProcessedDocument.isComplete()) {

			for (NLSentence nlSentence : ProcessedDocument.getDocumentSentences()) 
				nlSentence.buildAndSetQuery(new PrudensQuery());

		}

		else
        	errln("Cannot build meta-queries. Document processing not completed!");

	}
	

	
	public String getMetaQueryTextData() {

		/*
		 * ******************************************************** 
		 * Get Meta-Query Data String
		 * ********************************************************
		 */
		
		String metaQueryText = "";

		if (ProcessedDocument != null && ProcessedDocument.isComplete()) {

			int count = 0;
			for (NLSentence nlSentence : ProcessedDocument.getDocumentSentences()) {
			
				count++;
				metaQueryText += "Sentence " + count + " : " + nlSentence.getText() + ls;
				metaQueryText += "************************************************" + ls;

				/* Create meta-query text */
				// CID - Re-think Query and Predicate class hierarchy a little bit
				// and how to generalize Knowledge Base types
				if (nlSentence.isMetaQueryBuilt())
					metaQueryText += nlSentence.getMetaQuery().getQueryTextData();

			}
		}
		
		else
        	errln("Cannot get meta-queriy text data. Document processing not completed!");

		return metaQueryText;

	}

	
	
	// CID - Re-think Query and Predicate class hierarchy a little bit and how to generalize Knowledge Base types
	public void runMetaQueries(String metaKnoweldgeBaseString) {
		
		if (ProcessedDocument != null && ProcessedDocument.isComplete()) {

			int i = 0;
			for (NLSentence nlSentence : ProcessedDocument.getDocumentSentences()) {
				
				i++;

				if (nlSentence.isMetaQueryBuilt()) {
					
					NLRGRule extractedRule;
					String	ruleName = "S" + String.format("%07d", i);

					if (NLRGParameterLib.NLRGPipeline_KnowledgeBaseType == "prudens") {

						extractedRule = new PrudensRule(ruleName);

						try {

							MetaKnowledgeBase.runExtractionQueryOnKB(metaKnoweldgeBaseString, nlSentence.getMetaQuery(), extractedRule);
							nlSentence.setRule(extractedRule);

						} catch (NLRGMetaKBException e) {
							errln("Error running extraction query on KB: " + e.getMessage());
						}
						
					}

					else
						errln(NLRGParameterLib.NLRGPipeline_MetaKBType + " type is not supported for meta-level Knowledge Base");
					
				}
				
			}
					
		}

		else
        	errln("Cannot run meta-queries. Document processing not completed!");

		
	}


	public String buildExtractedRules() {
		
		String	extractedRulesText = "";
		
		if (ProcessedDocument != null && ProcessedDocument.isComplete()) {

			for (NLSentence nlSentence : ProcessedDocument.getDocumentSentences())

				if (nlSentence.isRuleSet()) 
					extractedRulesText += nlSentence.getRule().generateRuleText() + ls;

		}

		else
        	errln("Cannot build extracted rules. Document processing not completed!");

		return extractedRulesText;

	}
	

	
	/**
	 * @return the processedDocument
	 */
	public  NLDocument getProcessedDocument() {
		return ProcessedDocument;
	}
	

}