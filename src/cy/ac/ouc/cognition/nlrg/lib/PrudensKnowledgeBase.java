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

    private String			KnowledgeBaseFilePath;
    private KnowledgeBase	TheKnowledgeBase;
    private KnowledgeBase	DefaultKnowledgeBase;
    private Prudens			Agent;


    
    PrudensKnowledgeBase(String knowledgeBaseFile) {
		KnowledgeBaseFilePath = knowledgeBaseFile;
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



    public int inferFacts(String contextText, String knowledgeBaseString) {

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


        
        
	public void runSentenceContext(String knowledgeBaseString, NLRGContext sentenceContextGneric, NLRGRule extractedRule) throws NLRGMetaKBException {

		PrudensContext sentenceContext = (PrudensContext) sentenceContextGneric;
		
		if (!sentenceContext.isContextReady()) {
    		errln("Error inferring from Meta-level Knowledge Base: Context is not ready!");
        	throw new NLRGMetaKBException("Error inferring from Meta-level Knowledge Base: Context is not ready!");
		}

		else {
			
			String	sentenceContextText = sentenceContext.getContextTextData();

        	if (inferFacts(sentenceContextText, knowledgeBaseString) == 0) {
        		errln("Error inferring from Meta-level Knowledge Base!");
            	throw new NLRGMetaKBException("Error inferring from Meta-level Knowledge Base!");
	    	}

        	else {
	        	
            	int metaRuleLayerId = 0;
            	int metaRuleLayerRuleIndex[] = new int[1000];
            	int metaRuleLayerPredicatesAdded[] = new int[1000];
            	int metaRuleLayerVariableIndex[] = new int[1000];
            	boolean isObjectRuleHead = true;

	        	// Sort Rules by priority as defined by Rule Name
	        	ArrayList<Rule> markedRules = Agent.getMarkedRules();
	        	markedRules.sort(Comparator.comparing(Rule::getName));
	        	
            	for (Rule rule : markedRules) {
		        	
		        	// CID - 20210427 At the time being multiple words for a single term are not supported
		        	// but the design is kept for compatibility with Prolog version and for future use
	            	int position = 1;
	            	int PredicateAdded = 0;
	            	RulePart metaRuleMode = RulePart.UNDEFINED;
		            String ruleHeadPredicateName = rule.getHead().getAtom().getPredicate().getName();
		            
		            // Print just for tracing reasons
		        	outln(TL, "");
		            outln(TL, "MetaRule Name=[" + rule.getName() + "]");
			        outln(TL, "MetaRule=[" + rule.toString() + "]");

			        // The following condition should be somehow more generic and elegant
		            if (ruleHeadPredicateName.equals("ruleterms")) {

		            	outln(TL, "MetaRule candidate for extraction!");
		            	
		            	boolean isPredicate = true;
	            		ArrayList<String> predicateIdentifier = new ArrayList<String>();
	            		ArrayList<String> argumentIdentifiers = new ArrayList<String>();
	            		
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
			            			if (varName.equals("args")) 
					            		isPredicate = false;
			            			// If variable is not empty or placeholder
			            			else if (	!varName.isBlank() &&
			            						!varName.equals("_") &&
			            						!varName.equals("\'\'") &&
			            						!varName.equals("")
			            					)
			            				predicateIdentifier.add(varName);
				            	}

			            		else if (varName.equals("next")) {
					            	RulePart objectRulePart = (isObjectRuleHead && metaRuleMode == RulePart.HEAD ?
					            								RulePart.HEAD :
					            								RulePart.BODY
					            							);
					            	PredicateAdded = extractedRule.addPredicateWithArgumentsFromLists(
					            							predicateIdentifier,
					            							argumentIdentifiers,
					            							objectRulePart);
					            	predicateIdentifier = new ArrayList<String>();
				            		argumentIdentifiers = new ArrayList<String>();
							        isObjectRuleHead = false; // Only once the rule head is added
				            		isPredicate = true;
				            	}

			            		else if (varName.startsWith("vph-")) {
			            			try {
					            		int localVarIndex = Integer.parseInt(varName.substring(4)) + metaRuleLayerVariableIndex[metaRuleLayerId];

					            		argumentIdentifiers.add("X" + localVarIndex);

					            		metaRuleLayerVariableIndex[metaRuleLayerId+1] =
					            				(localVarIndex > metaRuleLayerVariableIndex[metaRuleLayerId+1] ?
					            					localVarIndex :
					            					metaRuleLayerVariableIndex[metaRuleLayerId+1]);
						            	
					            		outln(TL,	"MetaArgument Index[" + metaRuleLayerId +"]: " +
					            				metaRuleLayerVariableIndex[metaRuleLayerId+1] +
					            				", Local MetaVariable Index: " + localVarIndex);
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

			            	PredicateAdded += extractedRule.addPredicateWithArgumentsFromLists(
			            							predicateIdentifier,
			            							argumentIdentifiers,
			            							objectRulePart);
			            	metaRuleLayerPredicatesAdded[metaRuleLayerId] += PredicateAdded;

			            	predicateIdentifier = new ArrayList<String>();
		            		argumentIdentifiers = new ArrayList<String>();
			            	isObjectRuleHead = false; // Only once the rule head is added
			            }

		        	}
		            
		        }

	        }
		        
        	extractedRule.setComplete(true);
        	outln(TL, "Rule Successfully Extracted");
            outln(TL, "");		        
        }

	}
   	        
	
} // End PrudensKnowledgeBase Class
