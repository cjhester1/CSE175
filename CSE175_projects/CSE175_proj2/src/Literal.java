
//
// Literal
//
// This class implements a non-negated literal.  Each literal consists of
// a symbolic predicate and a list of arguments, each of which is a term.
// Methods are provided for identifying all of the variables in a given
// literal (including those appearing deep within function arguments) and 
// for substituting variables with their corresponding values, according to
// a given binding list.
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


import java.io.*;
import java.util.*;


public class Literal {

    public Predicate pred;
    public List<Term> args;

    // Default constructor ...
    public Literal() {
	this.pred = new Predicate();
	this.args = new ArrayList<Term>();
    }

    // equals -- Return true if and only if this literal is the same as the 
    // given literal argument.
    public boolean equals(Literal lit) {
	if (!(pred.equals(lit.pred)))
	    return (false);
	if (args.size() != lit.args.size())
	    return (false);
	for (int i = 0; i < args.size(); i++)
	    if (!(args.get(i).equals(lit.args.get(i))))
		return (false);
	return (true);
    }

    // allVariables -- Return a set of all the variables in this function.
    public Set<Variable> allVariables() {
	Set<Variable> allVs = new HashSet<Variable>();
	for (Term arg : args) {
	    allVs.addAll(arg.allVariables());
	}
	return (allVs);
    }

    // subst -- Return a new Literal object that is the result of applying
    // the given binding list to this literal.  Return null on error.
    public Literal subst(BindingList bl) {
	Literal result = new Literal();
	result.pred = pred;
	for (Term arg : args) {
	    result.args.add(arg.subst(bl));
	}
	return (result);
    }

    // read -- Read a literal from the given scanner, filling in this object
    // with the results.  Return false on error.
    public boolean read(Scanner inScanner) {
	inScanner.useDelimiter("[\\s]+");
	// Find and discard opening parenthesis ...
	try {
	    inScanner.skip("[\\s]*\\(");
	} catch (NoSuchElementException e) {
	    return (false);
	}
	// Read predicate name ...
	if (!(pred.read(inScanner)))
	    // Failed to read predicate name ...
	    return (false);
	// Read each argument term, checking for the end of the literal
	// expression ...
	inScanner.useDelimiter("[\\s]*");
	while (!(inScanner.hasNext("[\\s]*\\)"))) {
	    Term thisArg = new Term();
	    inScanner.useDelimiter("[\\s]+");
	    if (!(thisArg.read(inScanner)))
		// Failed to read argument term ...
		return (false);
	    inScanner.useDelimiter("[\\s]*");
	    args.add(thisArg);
	}
	// Read closing parenthesis ...
	try {
	    inScanner.skip("[\\s]*\\)");
	} catch (NoSuchElementException e) {
	    return (false);
	}
	// Reading was successful ...
	return (true);
    }

    // write -- Write this literal to the given stream.
    public void write(OutputStream str) {
	try {
	    str.write('(');
	    pred.write(str);
	    for (Term arg : args) {
		str.write(' ');
		arg.write(str);
	    }
	    str.write(')');
	} catch (IOException e) {
	    // Something went wrong ...
	}
    }


}

