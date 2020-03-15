//
// Variable
//
// This class implements a logical variable.  This is simply a symbol.  By
// convention, all variable names must start with a question mark.
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


public class Variable extends Symbol {

    // For making arbitrary novel symbols ...
    static String gensymPrefix = "?VAR-";
    static int gensymCounter = 1;

    // Default constructor ...
    public Variable() {
	this.name = "?NULL";
    }

    // Constructor with symbol name specified ...
    public Variable(String name) {
	if (name.length() == 0) {
	    // This is a request for a novel variable ...
	    this.name = 
		String.format("%s%04d", gensymPrefix, gensymCounter);
	    gensymCounter++;
	} else {
	    if (name.charAt(0) != '?') {
		this.name = "?" + name;
	    } else {
		this.name = name;
	    }
	}
    }

	


}

