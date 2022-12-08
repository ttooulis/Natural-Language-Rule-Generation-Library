package cy.ac.ouc.cognition.nlrg.lib;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;

import coaching.*;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.outln;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.errln;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.TL;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGPredicate.RulePart;


public class PrudensKnowledgeBase extends NLRGKnowledgeBase {

	private static final int MAX_LAYERS = NLRGParameterLib.NLRGMetaKB_MaximumLayers;

	private String			KnowledgeBaseFilePath;
    private KnowledgeBase	TheKnowledgeBase;
    private KnowledgeBase	DefaultKnowledgeBase;
    private Prudens			Agent;


    
    PrudensKnowledgeBase(String knowledgeBaseFile) {
		KnowledgeBaseFilePath = knowledgeBaseFile;
	}

    
    
    private int inferFacts(String contextText, String knowledgeBaseString) {

		InputStream contextStream = new ByteArrayInputStream(contextText.getBytes());
		Path p;
		try {
			
			// If a Knowledge Base string is not passed, do not re-load default KB
        	if (knowledgeBaseString != null && !knowledgeBaseString.equals(""))
        		this.load(knowledgeBaseString);
        	else
        		TheKnowledgeBase = DefaultKnowledgeBase;
			
			p = Files.createTempFile(null, null);
			Files.copy(contextStream, p, StandardCopyOption.REPLACE_EXISTING);
	        File contextFile = new File(p.toUri());
			
	        Context context = new Context(contextFile, TheKnowledgeBase);
	        Agent = new Prudens(TheKnowledgeBase, context);
	        
	        if (!contextText.equals("metakbinfo(version)")) {
				outln(TL, "Context:");
				outln(TL, contextText);
	        }

		} catch (IOException e) {
			errln("Error inferring facts from Meta-level Knowledge Base: " + e.getMessage());
			return 0;
		}


		return 1;
	}

	
	
    public int load(String knowledgeBaseString) {

        File knowledgeBaseFile;

        try {

        	if (knowledgeBaseString != null && !knowledgeBaseString.equals("")) {

            	InputStream stream = new ByteArrayInputStream(knowledgeBaseString.getBytes());
        		Path p;

        		p = Files.createTempFile(null, null);
				Files.copy(stream, p, StandardCopyOption.REPLACE_EXISTING);
				knowledgeBaseFile = new File(p.toUri());

				TheKnowledgeBase = new KnowledgeBase(knowledgeBaseFile);
			}

        	else {

        		knowledgeBaseFile = new File(KnowledgeBaseFilePath);

        		TheKnowledgeBase = new KnowledgeBase(knowledgeBaseFile);
				DefaultKnowledgeBase = TheKnowledgeBase;
        	}

		} catch (FileNotFoundException nfe) {
			nfe.printStackTrace();
			return 0;

		} catch (IOException e) {
			errln("Error loading Meta-level Knowledge Base: " + e.getMessage());
		}

        return 1;

    }



    public String getKBText() {

        try {

    		return Files.readString(Paths.get(KnowledgeBaseFilePath));
        	
		} catch (FileNotFoundException nfe) {
			nfe.printStackTrace();

		} catch (IOException e) {
			errln("Error reading Meta-level Knowledge Base: " + e.getMessage());
		}

        return "";

    }



	public String getKBVersion(String knowledgeBaseString) {
		
		String	metaKBVersion = "Prudens Default";
		String	versionContext = "metakbinfo(version)";

    	try {

    		if (inferFacts(versionContext, knowledgeBaseString) == 0) {
    			errln("Error inferring version of Meta-level Knowledge Base: Cannot access Knowledge Base!");
            	metaKBVersion = "Prudens Default";
    		}

    		else {
        	
        		Rule rule = Agent.getMarkedRules().get(0);
	        	
	            String ruleHeadPredicateName = rule.getHead().getAtom().getPredicate().getName();
	            
	            if (ruleHeadPredicateName.equals("metakbinfo_data")) {
	            	
	            	String infoData = rule.getHead().getAtom().getPredicate().getVariables().get(0).getValue().getName();

	            	if (infoData.startsWith("\'") && infoData.endsWith("\'"))
			     		metaKBVersion = infoData.substring(1, infoData.length()-1);
			    	else
			    		metaKBVersion = infoData;
	            }

	            else
	            	metaKBVersion = "Prudens Default";
    		}
        }

    	catch (Exception e) {
	       	errln("Error inferring version of Meta-level Knowledge Base. Version not found or cannot access KB!");
	       	errln(e.getMessage());
	       	metaKBVersion = "Prudens Default";   
	    }
     
        return metaKBVersion;

	}


        
        
	public void runSentenceContext(String knowledgeBaseString, NLSentence nlSentence) throws NLRGMetaKBException {

		PrudensContext sentenceContext;
		NLRGRule extractedRule;
		PrudensDeduction ruleDeduction = new PrudensDeduction();

		if (!(nlSentence.getContext() instanceof PrudensContext)) {
    		errln("Error inferring from Meta-level Knowledge Base: Context object is invalid!");
        	throw new NLRGMetaKBException("Error inferring from Meta-level Knowledge Base: Context object is invalid!");
		}
		sentenceContext = (PrudensContext) nlSentence.getContext();

		
		if (!sentenceContext.isContextReady()) {
    		errln("Error inferring from Meta-level Knowledge Base: Context is not ready!");
        	throw new NLRGMetaKBException("Error inferring from Meta-level Knowledge Base: Context is not ready!");
		}

		
    	if (inferFacts(sentenceContext.getContextTextData(), knowledgeBaseString) == 0) {
    		errln("Error inferring from Meta-level Knowledge Base!");
        	throw new NLRGMetaKBException("Error inferring from Meta-level Knowledge Base!");
    	}

    	// Sort Rules by priority as defined by Rule Name
    	ArrayList<Rule> markedRules = Agent.getMarkedRules();
    	markedRules.sort(Comparator.comparing(Rule::getName).reversed());

    	// Save marked rules for sentence
    	ruleDeduction.setPrudensMarkedRules(markedRules);

		
    	int metaRuleLayerId = 0;
    	boolean isObjectRuleHead = true;
    	int metaRuleLayerRuleIndex[] = new int[MAX_LAYERS];
    	int metaRuleLayerPredicatesAdded[] = new int[MAX_LAYERS];
    	int metaRuleVariableIndexBase = 0;
    	int metaRuleVariableIndexBaseForNextRule = 0;
  	  	
		extractedRule = new PrudensRule("S" + String.format("%07d", nlSentence.getIndexInDocument()));

		// Iterate through all marked rules
		for (Rule rule : markedRules) {
        	
        	outln(TL, "");
            outln(TL, "MetaRule Name=[" + rule.getName() + "]");
	        outln(TL, "MetaRule=[" + rule.toString() + "]");

            String ruleHeadPredicateName = rule.getHead().getAtom().getPredicate().getName();

            // If this is a rule that has extraction meta-predicate in its head, then it is extraction candidate
            if (ruleHeadPredicateName.equals(NLRGParameterLib.NLRGMetaKB_ExtractionMetaPredicate)) {

                outln(TL, "MetaRule candidate for extraction!");
            	
            	int position = 1;
            	int PredicateAdded = 0;
            	boolean isPredicate = true;
            	RulePart metaRuleMode = RulePart.UNDEFINED;
        		ArrayList<String> predicateIdentifier = new ArrayList<String>();
        		ArrayList<String> argumentIdentifiers = new ArrayList<String>();
        		
        		// Iterate through all variables (arguments) of this rule
	            for (Variable var : rule.getHead().getAtom().getPredicate().getVariables()) {
	            	
	            	String varName = var.getValue().getName();
	            	
	            	// CID - SHOULD ADD A LOT OF CHECKS IN THE FOLLOWING IF STRUCTURE
	            	// TO MAKE SURE META-KB IS DEVELOPED CORRECTLY
	            	if (position == 1) {
		            	if (varName.startsWith("head")) {
		            		metaRuleMode = RulePart.HEAD;
			            	outln(TL, "MetaRule Type: Head");
		            	}
		            	else if (varName.startsWith("body")) {
		            		metaRuleMode = RulePart.BODY;
			            	outln(TL, "MetaRule Type: Body");
		            	}
		            	else if (varName.startsWith("conflict")) {
		            		metaRuleMode = RulePart.HEAD;
		            		extractedRule.setConflict(true);
			            	outln(TL, "MetaRule Type: Conflict");
		            	}
		            	else {
			            	errln("\tMetaRule Type: Undefined");
			            	throw new NLRGMetaKBException(
			            			ruleHeadPredicateName + ": " +
			            			"Undefined Metarule Type: " + varName);
		            	}
	            	}

	            	else if (position == 2) {
	            		try {
		            		metaRuleLayerId = Integer.parseInt(varName);
		            		
		            		if (metaRuleLayerId >= MAX_LAYERS)
				            	throw new NLRGMetaKBException("Layer ID greater than maximum number of layers");

		            		metaRuleLayerRuleIndex[metaRuleLayerId]++;
			            	outln(TL, "MetaRule Layer: " + metaRuleLayerId);
			            	outln(TL, "MetaRule Layer Rule Index: " + metaRuleLayerRuleIndex[metaRuleLayerId]);
	            		}
	            		catch (Exception e) {
			            	throw new NLRGMetaKBException(
			            			ruleHeadPredicateName + ": " +
			            			"Invalid MetaRule Layer: " + varName +
			            			"(" + e.getMessage() + ")");
	            		}
	            			
		            }		            	

	            	else if (metaRuleLayerRuleIndex[metaRuleLayerId] == 1 || metaRuleLayerPredicatesAdded[metaRuleLayerId] == 0) {

	            		outln(TL, "MetaArgument=[" + varName + "]");

	            		if (position == 3 || isPredicate) {
	            			if (varName.equals(NLRGParameterLib.NLRGMetaKB_ArgumentSeparator)) 
			            		isPredicate = false;
	            			// If variable is not empty or placeholder
	            			else if (	!varName.isBlank() &&
	            						!varName.equals("_") &&
	            						!varName.equals("\'\'") &&
	            						!varName.equals("")
	            					)
	            				predicateIdentifier.add(varName);
		            	}

	            		else if (varName.equals(NLRGParameterLib.NLRGMetaKB_PredicateSeparator)) {
			            	RulePart objectRulePart = (isObjectRuleHead && metaRuleMode == RulePart.HEAD ?
			            								RulePart.HEAD :
			            								RulePart.BODY
			            							);

			            	if (objectRulePart == RulePart.HEAD)
						        isObjectRuleHead = false; // Only once the rule head is added

			            	PredicateAdded = extractedRule.addPredicateWithArgumentsFromLists(
			            							predicateIdentifier,
			            							argumentIdentifiers,
			            							objectRulePart,
			            							ruleDeduction.getMarkedRulesChain(rule));

			            	metaRuleLayerPredicatesAdded[metaRuleLayerId] += PredicateAdded;

			            	ruleDeduction.addToUsedMetaRules(rule);

			            	predicateIdentifier = new ArrayList<String>();
		            		argumentIdentifiers = new ArrayList<String>();
		            		isPredicate = true;
		            	}

	            		else if (varName.startsWith(NLRGParameterLib.NLRGMetaKB_VarPlaceholder)) {
	            			try {
	            				int varIndexStart = NLRGParameterLib.NLRGMetaKB_VarPlaceholder.length();
			            		int localVarIndex = Integer.parseInt(varName.substring(varIndexStart)) + metaRuleVariableIndexBase;

			            		argumentIdentifiers.add(NLRGParameterLib.NLRGRule_VariableName + localVarIndex);

			            		metaRuleVariableIndexBaseForNextRule =
			            				(localVarIndex > metaRuleVariableIndexBase ?
			            					localVarIndex :
			            					metaRuleVariableIndexBase);
				            	
			            		outln(TL,	"Local MetaVariable Index: " + localVarIndex +
			            					", MetaVariable Index Base: " + metaRuleVariableIndexBase +
			            					", MetaVariable Index Base For Next Rule: " + metaRuleVariableIndexBaseForNextRule
			            				);
		            		}
		            		catch (Exception e) {
				            	throw new NLRGMetaKBException(
				            			ruleHeadPredicateName + ": " +
				            			"Invalid MetaRule Variable PlaceHolder: " + varName +
				            			"(" + e.getMessage() + ")");
		            		}
		            	}

	            		else if (!varName.equals("_")) {
		            		argumentIdentifiers.add(varName);
		            	}
		            }
	            	
	            	position++;
	            }
	            
	            if (metaRuleLayerRuleIndex[metaRuleLayerId] == 1 || metaRuleLayerPredicatesAdded[metaRuleLayerId] == 0) {

	            	RulePart objectRulePart = (isObjectRuleHead && metaRuleMode == RulePart.HEAD ?
							RulePart.HEAD :
							RulePart.BODY
						);

	            	if (objectRulePart == RulePart.HEAD)
				        isObjectRuleHead = false; // Only once the rule head is added

	            	PredicateAdded += extractedRule.addPredicateWithArgumentsFromLists(
	            							predicateIdentifier,
	            							argumentIdentifiers,
	            							objectRulePart,
	            							ruleDeduction.getMarkedRulesChain(rule));

	            	metaRuleLayerPredicatesAdded[metaRuleLayerId] += PredicateAdded;

	            	ruleDeduction.addToUsedMetaRules(rule);

	            	predicateIdentifier = new ArrayList<String>();
            		argumentIdentifiers = new ArrayList<String>();
	            }
	            
	            metaRuleVariableIndexBase = metaRuleVariableIndexBaseForNextRule;

        	}
                       
        }

       
		extractedRule.setComplete(true);
		nlSentence.setExtractedRule(extractedRule);
		nlSentence.setRuleDeduction(ruleDeduction);
		outln(TL, "Rule Successfully Extracted");
	    outln(TL, "");
	    outln(TL, "Extraction Marked Rules Chain");
	    outln(TL, ruleDeduction.getMarkedRulesChainText());
	    outln(TL, "Extraction Marked Rules");
	    outln(TL, ruleDeduction.getMarkedRulesText());
//	    outln(TL, "Extraction Deduction JSON objects");
//	    outln(TL, ruleDeduction.toJSONString());
	    
	}
   	        
	
} // End PrudensKnowledgeBase Class
