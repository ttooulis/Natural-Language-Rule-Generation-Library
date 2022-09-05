package cy.ac.ouc.cognition.nlrg.lib;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.outln;

import java.util.regex.Pattern;

public class NLRGParameterLib extends NLRGParameter {

	/********************************
	 * NLRG Library General Parameters
	 ********************************/
	public static String NLRGThing_LineSeperator = System.getProperty("line.separator");
	public static int NLRGThing_TraceLevel = 2;
	public static String NLRGThing_DefaultTraceLevel = "NORMAL";

	
	
	/********************************
	 * MetaKnowledgeBase Parameters
	 ********************************/
	public static int NLRGMetaKB_PredicateMode = 0;

	
	
	/********************************
	 * Rule Generation Parameters
	 ********************************/
	public static String NLRGRule_PredNameConcatChar = "";
	public static int NLRGRule_PredNameCapitalize = 0;
	public static String NLRGRule_NeckSymbol = "implies";
	public static String NLRGRule_ConflictNeckSymbol = "#";
	public static int NLRGRule_GenerateName = 1;
	public static String NLRGRule_NameSeperator = "::";



	/********************************
	 * Stanford CoreNLP Parameters
	 ********************************/
	// KNOWN ANNOTATORS: tokenize, ssplit, pos, lemma, ner, parse, depparse, coref, kbp, quote
	// public static String StanfordCoreNLProcessor_Annotators = "tokenize, ssplit, pos, depparse, lemma, ner";
	public static String StanfordCoreNLProcessor_Annotators = "tokenize, ssplit, pos, depparse, lemma, ner";

	// The coref annotator is being set to use the neural algorithm
	public static String StanfordCoreNLProcessor_Algorithm = "neural";
	
	// Define how Stanford Core NLP will split a sentence
	public static String StanfordCoreNLProcessor_Split = "false";
	

	/********************************
	 * NLRG Pipeline Parameters
	 ********************************/
	public static String NLRGPipeline_NLProcessor;
	public static String NLRGPipeline_MetaKBType;
	public static String NLRGPipeline_KnowledgeBaseType;
	public static String NLRGPipeline_MetaKnowledgeBaseFile;
	public static String NLRGPipeline_KnowledgeBaseFile;

	
	public static void Load(String settingsFile) {

		Initialize(settingsFile);
		
		outln(NLRGTrace.TraceLevel.IMPORTANT, "\nSetting NLRG Lib parameters...");

		NLRGThing_LineSeperator = ReadParameterTrace("LineSeperator", System.getProperty("line.separator"));
		NLRGThing_DefaultTraceLevel = ReadParameterTrace("DefaultTraceLevel", "NORMAL");


		NLRGMetaKB_PredicateMode = ReadIntParameterTrace("MetaPredicateMode", 0);
		
		NLRGRule_PredNameConcatChar = ReadParameterTrace("PredicateNameConcatChar", "");
		NLRGRule_PredNameCapitalize = ReadIntParameterTrace("PredicateNameCapitalize", 0);
		NLRGRule_NeckSymbol = ReadParameterTrace("NeckSymbol", "implies");
		NLRGRule_ConflictNeckSymbol = ReadParameterTrace("ConflictNeckSymbol", "#");
		NLRGRule_GenerateName = ReadIntParameterTrace("GenerateName", 1);
		NLRGRule_NameSeperator = ReadParameterTrace("NameSeperator", "::");


		StanfordCoreNLProcessor_Annotators = ReadParameter("Annotators", "tokenize, ssplit, pos, depparse, lemma, ner");
		/* Make sure that annotators "tokenize" and "ssplit" are always included */
		/* CID - Not very elegant solution... */
		if (!StanfordCoreNLProcessor_Annotators.contains("tokenize") && !StanfordCoreNLProcessor_Annotators.contains("ssplit"))
			StanfordCoreNLProcessor_Annotators = "tokenize, ssplit, " + StanfordCoreNLProcessor_Annotators;
		else if (StanfordCoreNLProcessor_Annotators.contains("ssplit") && !StanfordCoreNLProcessor_Annotators.contains("tokenize"))
			StanfordCoreNLProcessor_Annotators = "tokenize, " + StanfordCoreNLProcessor_Annotators;
		else if (StanfordCoreNLProcessor_Annotators.contains("tokenize") && !StanfordCoreNLProcessor_Annotators.contains("ssplit"))
			StanfordCoreNLProcessor_Annotators = StanfordCoreNLProcessor_Annotators.replaceFirst(Pattern.quote("tokenize"), "tokenize, ssplit");
		outln(NLRGTrace.TraceLevel.IMPORTANT, "Annotators" + "=[" + StanfordCoreNLProcessor_Annotators + "]");

		StanfordCoreNLProcessor_Algorithm = ReadParameterTrace("Algorithm", "neural");
		StanfordCoreNLProcessor_Split = ReadParameterTrace("ssplit.eolonly", "false");

		
		NLRGPipeline_NLProcessor = ReadParameterTrace("NLProcessor", "corenlp");
		NLRGPipeline_MetaKBType = ReadParameterTrace("MetaKBType", "prudens");
		NLRGPipeline_KnowledgeBaseType = ReadParameterTrace("KnowledgeBaseType", "prudens");
		NLRGPipeline_MetaKnowledgeBaseFile =ReadParameterTrace("MetaKBFile", "MetaLevel-Translation-KB.prudens");
		NLRGPipeline_KnowledgeBaseFile = ReadParameterTrace("KnowledgeBaseFile", "KnowledgeBase.txt");
	
		outln(NLRGTrace.TraceLevel.IMPORTANT, "NLRG Lib Parameters Set!");

	}

}
