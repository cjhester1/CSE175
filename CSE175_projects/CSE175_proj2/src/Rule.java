
//
// Rule
//
// This class implements a Horn clause rule.  Each rule has a textual name,
// a positive literal consequent ("head"), and a list of positive literal
// antecedents ("tail").  All of the antecedents must be established as
// true, under some binding list, in order to use the rule to justify the
// truth of the consequent, under the same bindings.  Methods are provided 
// for identifying all of the variables in a given rule (including those
// appearing deep within its components) and for substituting variables 
// with their corresponding values, according to a given binding list.  This
// substitution procedure is also used by a method that replaces all variables
// with novel variables, effectively standardizing the rule apart.
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


import java.io.*;
import java.util.*;


public class Rule {

    public String name;
    public Literal consequent;            // the "head" of the rule
    public List<Literal> antecedents;     // the "tail" of the rule

    // Default constructor ...
    public Rule() {
	this.name = "NULL";
	this.consequent = null;
	this.antecedents = new ArrayList<Literal>();
    }

    // equals -- Return true if and only if this rule is the same as the 
    // given rule argument.
    public boolean equals(Rule r) {
	if (!(name.equals(r.name)))
	    return (false);
	if (!(consequent.equals(r.consequent)))
	    return (false);
	if (antecedents.size() != r.antecedents.size())
	    return (false);
	for (int i = 0; i < antecedents.size(); i++)
	    if (!(antecedents.get(i).equals(r.antecedents.get(i))))
		return (false);
	return (true);
    }

    // allVariables -- Return a set of all the variables in this function.
    public Set<Variable> allVariables() {
	Set<Variable> allVs = new HashSet<Variable>();
	allVs.addAll(consequent.allVariables());
	for (Literal ante : antecedents) {
	    allVs.addAll(ante.allVariables());
	}
	return (allVs);
    }

    // subst -- Return a new Rule object that is the result of applying
    // the given binding list to all of the literals in this rule.  Return 
    // null on error.
    public Rule subst(BindingList bl) {
	Rule result = new Rule();
	result.name = name;
	result.consequent = consequent.subst(bl);
	for (Literal ante : antecedents) {
	    result.antecedents.add(ante.subst(bl));
	}
	return (result);
    }

    // standardizeApart -- Return a new Rule object that is a copy of this
    // rule with all of the variable names changed.  Return null on error.
    public Rule standardizeApart() {
	Set<Variable> initialVars = allVariables();
	BindingList bl = new BindingList();
	for (Variable v : initialVars) {
	    // Add a binding from this initial variable to a new, novel,
	    // variable (converted into a term) ...
	    bl.addBinding(v, new Term(new Variable("")));
	}
	return (subst(bl));
    }

    // read -- Read a rule from the given scanner, filling in this object with 
    // the results.  Return false on error.
    public boolean read(Scanner inScanner) {
	inScanner.useDelimiter("[\\s]+");
	// Find and discard opening parenthesis ...
	try {
	    inScanner.skip("[\\s]*\\(");
	} catch (NoSuchElementException e) {
	    return (false);
	}
	// Read the "DEFRULE" keyword ...
	if (inScanner.hasNext()) {
	    String keyword = inScanner.next();
	    if (!(keyword.toUpperCase().equals("DEFRULE")))
		return (false);
	} else {
	    // There is nothing to read ...
	    return (false);
	}
	// Read the rule name ...
	if (inScanner.hasNext()) {
	    name = inScanner.next();
	} else {
	    // There is nothering to read ...
	    return (false);
	}
	// Read antecedents ...
	inScanner.useDelimiter("[\\s]+");
	while (inScanner.hasNext() && !(inScanner.hasNext("=>.*"))) {
	    Literal ante = new Literal();
	    if (!(ante.read(inScanner)))
		return (false);
	    inScanner.useDelimiter("[\\s]+");
	    antecedents.add(ante);
	}
	// Skip arrow ...
	try {
	    inScanner.skip("[\\s]*=>");
	} catch (NoSuchElementException e) {
	    return (false);
	}
	// Read consequent ...
	if (consequent == null)
	    consequent = new Literal();
	if (!(consequent.read(inScanner)))
	    return (false);
	// Hunt for closing parenthesis and read it ...
	try {
	    inScanner.skip("[\\s]*\\)");
	} catch (NoSuchElementException e) {
	    return (false);
	}
	// Done!
	return (true);
    }

    // write -- Write this rule to the given stream.
    public void write(OutputStream str) {
	PrintWriter out = new PrintWriter(str, true);
	out.printf("(DEFRULE %s\n", name);
	for (Literal antecedent : antecedents) {
	    out.printf("  ");
	    antecedent.write(str);
	    out.printf("\n");
	}
	out.printf("=>\n");
	out.printf("  ");
	consequent.write(str);
	out.printf("\n");
	out.printf(")\n");
    }


}

