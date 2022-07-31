package cy.ac.ouc.cognition.nlrg.lib;

public class NLRGTrace extends NLRGThing {
	

	static enum StreamType {
		OUT, ERR;
	}
	

	public static enum TraceLevel {
		CRITICAL, IMPORTANT, NORMAL, LOW, INFO;
	}
	
	
	public static TraceLevel TL = TraceLevel.valueOf(NLRGParameterLib.NLRGThing_DefaultTraceLevel);
	

	public static void println(StreamType stream, String textToPrint) {
		
		if (stream == StreamType.ERR)
			System.err.println(textToPrint);
		else
			System.out.println(textToPrint);
	}

	
	public static void outln(TraceLevel traceLevel, String textToPrint) {
		if (traceLevel.ordinal() <= NLRGParameterLib.NLRGThing_TraceLevel)
		NLRGTrace.println(StreamType.OUT, textToPrint);
	}

	public static void errln(String textToPrint) {
		NLRGTrace.println(StreamType.ERR, textToPrint);
	}

}
