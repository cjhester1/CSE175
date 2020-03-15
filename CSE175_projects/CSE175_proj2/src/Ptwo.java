//
// Ptwo
//
// This class provides a "main" method that acts as a driver program for
// a very simple backward chaining inference engine.  The user is prompted
// for the names of two files -- a file containing a collection of positive
// literal ground term "facts" and a file containing a collection of Horn
// clause "rules" -- and these files are read into an internal knowledge
// base.  The user is then prompted for a positive literal query, and a
// backward chaining inference procedure is used to search for a proof for
// that query.  The result of that inference process is then reported,
// along with an indication of the variable bindings necessary for the
// discovered proof to hold.
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


import java.io.*;
import java.util.*;


public class Ptwo {

    public static void main(String[] args) {
	try {
	    BackwardChain engine = new BackwardChain();
	    InputStreamReader converter = new InputStreamReader(System.in);
	    BufferedReader in = new BufferedReader(converter);
	    String queryLine;
	    Literal query = new Literal();
	    BindingList resultBL;

	    System.out.println("BACKWARD CHAINING INFERENCE PROCEDURE");
	    // Read knowledge base ...
	    if (!(engine.initKB())) {
		System.err.println("Error:  Unable to read knowledge base.");
		return;
	    }
	    // Echo knowledge base for debugging purposes ...
	    System.out.println("FACTS:");
	    for (Literal fact : engine.kb.facts) {
		fact.write(System.out);
		System.out.println("");
	    }
	    System.out.println("RULES:");
	    for (Rule r : engine.kb.rules) {
		r.write(System.out);
		System.out.println("");
	    }
	    // Get query ...
	    System.out.println("Enter a (positive literal) query:");
	    queryLine = in.readLine();
	    Scanner queryScanner = new Scanner(queryLine);
	    if (!(query.read(queryScanner))) {
		System.err.println("Error:  Unable to read query.");
		return;
	    }
	    // Run inference procedure ...
	    System.out.println("Running inference procedure ...");
	    resultBL = engine.ask(query);
	    // Report results ...
	    if (resultBL == null) {
		System.out.println("NO PROOF FOUND.");
	    } else {
		// Filter the resulting binding list to only contain
		// variables that we care about ...
		resultBL = resultBL.filterBindings(query.allVariables());
		// Report the results ...
		if (resultBL.pairs.size() == 0) {
		    System.out.println("QUERY IS TRUE.");
		} else {
		    System.out.println("QUERY IS TRUE UNDER THE BINDINGS:");
		    resultBL.write(System.out);
		}
	    }
	    // Done ...
	    System.out.println("INFERENCE PROCEDURE COMPLETE");
	} catch (IOException e) {
	    // Something went wrong ...
	}
    }
    
}
