//
// Binding
//
// This class implements an element of a logical binding list.  It is a
// simple pair, consisting of a variable and its (term) value.  These are
// grouped to form binding lists (substitution lists).
//
// David Noelle -- Tue Apr 10 17:08:45 PDT 2007
//


public class Binding {

    public Variable var;
    public Term val;

    // Default constructor ...
    public Binding() {
	this.var = null;
	this.val = null;
    }

    // Constructor with field values ...
    public Binding(Variable var, Term val) {
	this.var = var;
	this.val = val;
    }


}

