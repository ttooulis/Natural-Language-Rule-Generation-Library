package cy.ac.ouc.cognition.nlrg.lib;

import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.outln;
import static cy.ac.ouc.cognition.nlrg.lib.NLRGTrace.errln;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class NLRGParameter extends NLRGThing {
	
	private static Document document;

	public static void Initialize(String settingsFile) {

		outln(NLRGTrace.TraceLevel.IMPORTANT, "Loading settings from file " + settingsFile + "...");
		File file = new File(settingsFile);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(file);
			outln(NLRGTrace.TraceLevel.IMPORTANT, settingsFile + " loaded!");
		} catch (ParserConfigurationException | IOException | SAXException e) {
			errln("Cannot open settings file (" + settingsFile + "): " + e.getMessage() + "\n");
			outln(NLRGTrace.TraceLevel.IMPORTANT, "Using Default Settings...");
		}
	}
	
	protected static String ReadParameter(String tagName, String defaultValue) {
		try {
			String parameterValue = document.getElementsByTagName(tagName).item(0).getTextContent();
			
			if (parameterValue == null || parameterValue == "")
				return defaultValue;

			return parameterValue;
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

	
	protected static int ReadIntParameter(String tagName, int defaultValue) {
		try {
			Integer parameterValue = Integer.parseInt(ReadParameter(tagName, Integer.toString(defaultValue)));
			return parameterValue.intValue();
		}
		catch (Exception e) {
			return defaultValue;
		}
	}


	protected static boolean ReadBooleanParameter(String tagName, boolean defaultValue) {
		try {
			String StringValue = ReadParameter(tagName, Boolean.toString(defaultValue));
			if (StringValue.equals("true") || StringValue.equals("True"))
				return true;
			else
				return false;
		}
		catch (Exception e) {
			return defaultValue;
		}
	}


	protected static String ReadParameterTrace(String tagName, String defaultValue) {
		String returnValue =	ReadParameter(tagName, defaultValue);
		outln(NLRGTrace.TraceLevel.IMPORTANT, tagName + "=[" + returnValue + "]");
		return returnValue;
	}


	protected static int ReadIntParameterTrace(String tagName, int defaultValue) {
		int returnValue =	ReadIntParameter(tagName, defaultValue);
		outln(NLRGTrace.TraceLevel.IMPORTANT, tagName + "=[" + returnValue + "]");
		return returnValue;
	}

	protected static boolean ReadBooleanParameterTrace(String tagName, boolean defaultValue) {
		boolean returnValue =	ReadBooleanParameter(tagName, defaultValue);
		outln(NLRGTrace.TraceLevel.IMPORTANT, tagName + "=[" + Boolean.toString(returnValue) + "]");
		return returnValue;
	}

}
