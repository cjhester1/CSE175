//
// Symbol
//
// This class implements an atomic symbol.  Symbol names can be provided
// at creation time.  If the empty string is provided as a symbol name,
// a novel name is provided for the symbol, instead.
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


import java.io.*;
import java.util.*;


public class Symbol {

    // For making arbitrary novel symbols ...
    static String gensymPrefix = "SYM-";
    static int gensymCounter = 1;

    public String name = "";

    // Default constructor ...
    public Symbol() {
	this.name = "NULL";
    }

    // Constructor with symbol name specified ...
    public Symbol(String name) {
	if (name.length() == 0) {
	    // This is a request to generate a symbol ...
	    this.name = 
		String.format("%s%04d", gensymPrefix, gensymCounter);
	    gensymCounter++;
	} else {
	    this.name = name;
	}
    }

    // equals -- Return true if and only if this symbol has the same name
    // as the argument symbol.
    public boolean equals(Symbol s) {
	return (s.name.equals(this.name));
    }

    // read -- Read a symbol from the given scanner, changing the name of
    // this symbol to match that which was read.  Return false on error.
    public boolean read(Scanner inScanner) {
	inScanner.useDelimiter("[\\(\\)\\s]+");
	if (inScanner.hasNext()) {
	    // There is a symbol ...
	    name = inScanner.next();
	    return (true);
	} else {
	    // There is nothing to read ...
	    return (false);
	}
    }
	    
    // write -- Write the name of this symbol to the given stream.
    public void write(OutputStream str) {
	PrintWriter out = new PrintWriter(str, true);
	out.printf("%s", name);
    }


}

