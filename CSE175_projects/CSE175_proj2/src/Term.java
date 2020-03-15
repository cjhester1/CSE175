
//
// Term
//
// This class implements a logical term:  a constant, variable, or function.
// This implementation is a little wasteful, providing separate variables
// for each of the three types of terms.  Methods are provided for identifying
// all of the variables in a given term (including those appearing deep 
// within function arguments) and for substituting variables in a term  with
// their corresponding values, according to a given binding list.
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


import java.io.*;
import java.util.*;


public class Term {

    public Constant c;
    public Variable v;
    public Function f;

    // Default constructor ...
    public Term() {
	this.c = null;
	this.v = null;
	this.f = null;
    }

    // Copy constructor ...
    public Term(Term trm) {
	this.c = trm.c;
	this.v = trm.v;
	this.f = trm.f;
    }

    // Constructor for constants ...
    public Term(Constant c) {
	this.c = c;
	this.v = null;
	this.f = null;
    }

    // Constructor for variables ...
    public Term(Variable v) {
	this.c = null;
	this.v = v;
	this.f = null;
    }

    // Constructor for function invocations ...
    public Term(Function f) {
	this.c = null;
	this.v = null;
	this.f = f;
    }

    // equals -- Return true if and only if this term is the same as the
    // given term.
    public boolean equals(Term trm) {
	if (((c == null) && (trm.c != null)) ||
	    ((c != null) && (trm.c == null)) ||
	    ((v == null) && (trm.v != null)) ||
	    ((v != null) && (trm.v == null)) ||
	    ((f == null) && (trm.f != null)) ||
	    ((f != null) && (trm.f == null))) {
	    // Terms are of different types ...
	    return (false);
	} else {
	    // Terms are of the same type ...
	    return (((c != null) && c.equals(trm.c)) ||
		    ((v != null) && v.equals(trm.v)) ||
		    ((f != null) && f.equals(trm.f)));
	}
    }

    // allVariables -- Return a set of all the variables in this term.
    public Set<Variable> allVariables() {
	Set<Variable> allVs = new HashSet<Variable>();
	if (v != null) {
	    allVs.add(v);
	} else {
	    if (f != null) {
		return (f.allVariables());
	    }
	}
	return (allVs);
    }

    // subst -- Return a new Term object that is the result of applying the
    // given binding list to this term.  Return null on error.
    public Term subst(BindingList bl) {
	Term result;
	if (c != null) {
	    result = new Term(c);
	} else {
	    if (v != null) {
		result = bl.boundValue(v);
		if (result == null)
		    result = new Term(v);
		else
		    // Make a copy, and make sure that the resulting
		    // term also has the binding list applied to it ...
		    result = result.subst(bl);
	    } else {
		if (f != null) {
		    result = new Term(f.subst(bl));
		} else {
		    return (null);
		}
	    }
	}
	return (result);
    }

    // read -- Read a logical term from the given scanner, filling
    // in this object with the results.  Return false on error.
    public boolean read(Scanner inScanner) {
	inScanner.useDelimiter("[\\s]+");
	if (inScanner.hasNext("\\(.*")) {
	    // The next item is a function invocation ...
	    c = null;
	    v = null;
	    f = new Function();
	    return (f.read(inScanner));
	}
	if (inScanner.hasNext("\\?.*")) {
	    // The next item is a variable ...
	    c = null;
	    v = new Variable();
	    f = null;
	    return (v.read(inScanner));
	}
	// Assume that the next item is a constant ...
	c = new Constant();
	v = null;
	f = null;
	return (c.read(inScanner));
    }

    // write -- Write this term to the given stream.
    public void write(OutputStream str) {
	if (c != null) {
	    c.write(str);
	} else {
	    if (v != null) {
		v.write(str);
	    } else {
		if (f != null) {
		    f.write(str);
		} else {
		    try {
			// Internal error ...
			str.write('#');
			str.write('#');
			str.write('#');
		    } catch (IOException e) {
			// Something went wrong ...
		    }
		}
	    }
	}
    }


}
