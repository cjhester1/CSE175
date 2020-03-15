//
// Knowledge Base
//
// This class implements a knowledge base of definite clauses.  A list of
// positive literals with only ground terms is maintained as a database of
// "facts".  Horn clause "rules" are also kept in an ordered list.  Typically,
// these facts and rules are read from plain text files, and the names of
// these files are maintained by the KnowledgeBase object.  Indeed, this
// class includes methods for prompting the user for these file names, storing
// them in the object, and reading collections of facts and rules from
// the corresponding files.  The top-level function for reading a knowledge
// base from user-specified files is called "readKB".
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


import java.io.*;
import java.util.*;


public class KnowledgeBase {

    String factsFilename = "facts.dat";
    String rulesFilename = "rules.dat";
    public List<Literal> facts;
    public List<Rule> rules;

    // Default constructor ...
    public KnowledgeBase() {
	this.facts = new ArrayList<Literal>();
	this.rules = new ArrayList<Rule>();
    }

    // Constructor with filenames specified ...
    public KnowledgeBase(String factsFilename, String rulesFilename) {
	this();
	this.factsFilename = factsFilename;
	this. rulesFilename = rulesFilename;
    }

    // setFactsFilename -- Record the given pathname of the facts file for
    // later use during knowledge base reading.
    public void setFactsFilename(String filename) {
	factsFilename = filename;
    }

    // setRulesFilename -- Record the given pathname of the rules file for
    // later use during knowledge base reading.
    public void setRulesFilename(String filename) {
	rulesFilename = filename;
    }

    // promptForFilenames -- Using the standard output stream and the standard
    // input stream, prompt the user to input the pathnames for a facts file
    // and for a rules file.  Record the input pathnames in this object for
    // later use during knowledge base reading.  Return false on error.
    public boolean promptForFilenames() {
	try {
	    InputStreamReader converter = new InputStreamReader(System.in);
	    BufferedReader in = new BufferedReader(converter);
	    String buffer;

	    System.out.println("Enter the name of the facts file:");
	    buffer = in.readLine();
	    setFactsFilename(buffer);
	    System.out.println("Enter the name of the rules file:");
	    buffer = in.readLine();
	    setRulesFilename(buffer);
	} catch (IOException e) {
	    // Something went wrong ...
	    return (false);
	}
	return (true);
    }

    // readFacts -- Attempt to open the facts file specified by the
    // appropriate pathname stored in this KnowledgeBase object.  If this
    // file can be opened for reading, read a collection of facts from this
    // file into the knowledge base.  Return false on error.
    public boolean readFacts() {
	try {
	    File factFile = new File(factsFilename);
	    if (factFile.exists() && factFile.canRead()) {
		FileInputStream factFileIn = new FileInputStream(factFile);
		InputStreamReader factISReader
		    = new InputStreamReader(factFileIn);
		BufferedReader factBufferedReader

		    = new BufferedReader(factISReader);
		Scanner factScanner
		    = new Scanner(factBufferedReader);
		// Allocate storage for the first fact to be read ...
		Literal fact = new Literal();
		while (fact.read(factScanner)) {
		    // Record the fact in the knowledge base ...
		    facts.add(fact);
		    // Allocate storage for the next fact ...
		    fact = new Literal();
		}
		return (true);
	    } else {
		// The file cannot be read ...
		return (false);
	    }
	} catch (IOException e) {
	    // Something went wrong ...
	    return (false);
	}
    }

    // readRules -- Attempt to open the rules file specified by the
    // appropriate pathname stored in this KnowledgeBase object.  If this
    // file can be opened for reading, read a collection of rules from this
    // file into the knowledge base.  Return false on error.
    public boolean readRules() {
	try {
	    File ruleFile = new File(rulesFilename);
	    if (ruleFile.exists() && ruleFile.canRead()) {
		FileInputStream ruleFileIn = new FileInputStream(ruleFile);
		InputStreamReader ruleISReader
		    = new InputStreamReader(ruleFileIn);
		BufferedReader ruleBufferedReader

		    = new BufferedReader(ruleISReader);
		Scanner ruleScanner
		    = new Scanner(ruleBufferedReader);
		// Allocate storage for the first rule to be read ...
		Rule r = new Rule();
		while (r.read(ruleScanner)) {
		    // Record the rule in the knowledge base ...
		    rules.add(r);
		    // Allocate storage for the next rule ...
		    r = new Rule();
		}
		return (true);
	    } else {
		// The file cannot be read ...
		return (false);
	    }
	} catch (IOException e) {
	    // Something went wrong ...
	    return (false);
	}
    }

    // readKB -- Prompt the user for the pathnames of a facts file and a
    // rules file, and then read those files into this KnowledgeBase object.
    // Return false on error.
    public boolean readKB() {
	return (promptForFilenames() && readFacts() && readRules());
    }


}
