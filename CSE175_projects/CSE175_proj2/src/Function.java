//
// Function
//
// This class implements a logical function.  Each function has a symbolic
// name and a list of arguments, with each argument being a term.  Methods
// are provided for identifying all of the variables in a function (including
// those appearing deep within arguments) and for substituting variables with
// their corresponding values, according to a given binding list.
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


import java.io.*;
import java.util.*;


public class Function {

    public FunctionName func;
    public List<Term> args;

    // Default constructor ...
    public Function() {
	this.func = new FunctionName();
	this.args = new ArrayList<Term>();
    }

    // equals -- Return true if and only if this function instance is the
    // same as the given argument.
    public boolean equals(Function f) {
	if (!(func.equals(f.func)))
	    return (false);
	if (args.size() != f.args.size())
	    return (false);
	for (int i = 0; i < args.size(); i++)
	    if (!(args.get(i).equals(f.args.get(i))))
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

    // subst -- Return a new Function object that is the result of applying
    // the given binding list to this function invocation.  Return null on 
    // error.
    public Function subst(BindingList bl) {
	Function result = new Function();
	result.func = func;
	for (Term arg : args) {
	    result.args.add(arg.subst(bl));
	}
	return (result);
    }

    // read -- Read a function invocation from the given scanner, filling
    // in this Function object with the results.  Return false on error.
    public boolean read(Scanner inScanner) {
	inScanner.useDelimiter("[\\s]+");
	// Find and discard opening parenthesis ...
	try {
	    inScanner.skip("[\\s]*\\(");
	} catch (NoSuchElementException e) {
	    return (false);
	}
	// Read function name ...
	if (!(func.read(inScanner)))
	    // Failed to read function name ...
	    return (false);
	// Read each argument term, checking for the end of the function
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

    // write -- Write this function invocation to the given stream.
    public void write(OutputStream str) {
	try {
	    str.write('(');
	    func.write(str);
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

