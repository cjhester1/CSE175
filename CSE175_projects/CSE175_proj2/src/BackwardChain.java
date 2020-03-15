//
// BackwardChain
//
// This class implements a backward chaining inference procedure. The
// implementation is very skeletal, and the resulting reasoning process is
// not particularly efficient. Knowledge is restricted to the form of
// definite clauses, grouped into a list of positive literals (facts) and
// a list of Horn clause implications (rules). The inference procedure
// maintains a list of goals. On each step, a proof is sought for the
// first goal in this list, starting by an attempt to unify the goal with
// any known fact in the knowledge base. If this fails, the rules are
// examined in the order in which they appear in the knowledge base, searching
// for a consequent that unifies with the goal. Upon successful unification,
// a proof is sought for the conjunction of the rule antecedents. If this
// fails, further rules are considered. Note that this is a strictly
// depth-first approach, so it is incomplete. Note, also, that there is
// no backtracking with regard to matches to facts -- the first match is
// always taken and other potential matches are ignored. This can make
// the algorithm incomplete in another way. In short, the order in which
// facts and rules appear in the knowledge base can have a large influence
// on the behavior of this inference procedure.
//
// In order to use this inference engine, the knowledge base must be
// initialized by a call to "initKB". Queries are then submitted using the
// "ask" method. The "ask" function returns a binding list which includes
// bindings for intermediate variables.
//
// David Noelle -- Tue Oct  9 18:48:57 PDT 2018
//


import java.util.*;


public class BackwardChain {

    public KnowledgeBase kb;

	// Default constructor ...
	public BackwardChain() {
		this.kb = new KnowledgeBase();
	}

	// initKB -- Initialize the knowledge base by interactively requesting
	// file names and reading those files. Return false on error.
	public boolean initKB() {
		return (kb.readKB());
	}

	// unify -- Return the most general unifier for the two provided literals,
	// or null if no unification is possible. The returned binding list
	// should be freshly allocated.
	public BindingList unify(Literal lit1, Literal lit2, BindingList bl) 
	{
			
			if (lit1.pred.equals(lit2.pred))//here we are comparing if the predicates are equal to one another
			
				return unify(lit1.args,lit2.args,bl);// if pred1 = pred2 then the literals may be unified 
				
				else//if pred1 and pred2 arent equal we cannot unify them...return null
			
			return null;
			
			
		
			
		}

		

	// unify -- Return the most general unifier for the two provided terms,
	// or null if no unification is possible. The returned binding list
	// should be freshly allocated.
	public BindingList unify(Term t1, Term t2, BindingList bl)
	{
		
		if(bl == null) //If the binding list is empty return null because a bl cant be created when null
    		return null;
    	
    	//I received help writing the unification and binding procedure from https://www-users.cs.umn.edu/~gini/4511/unification.html this cite gave very helpful 
		//information on how the unification algorithm works with a different combination of arguments
		//
		else if(t1.f != null && t2.c != null) //here we are comparing the first term (a function) with a second term (a constant) and this cannot be done
    	{
    		 return null;//cannot unify a function with a constant
    	}
		else if(t1.f != null && t2.v != null)
		{
			return unify(t2,t1,bl);// unify method is not allowed in Backward chain for arguments (variable,function,bl) so I left the .v and .f out
		}
		else if(t1.f != null && t2.f != null) 
		{
			return unify(t1.f,t2.f,bl);// unification of a function and a function
		}
		
		
		else if(t1.c != null && t2.c != null) 
		{
			if (t1.equals(t2)) //success if and only if 2 constants are the same, fail otherwise; if equal return new bl
				
			{
			
				return new BindingList();//we must create a new binding list if they are equal
			}
			else 
			{
			
				return null;//null will be returned if these constants aren't the same
			}
		
		}
		
		else if(t1.c != null && t2.v != null) 
		{
			return unify(t2,t1,bl);	//unify method is not allowed in Backward chain for arguments (variable,constant,bl) so I left the .v and .c subparts out
		}
		
		else if(t1.c != null && t2.f != null) 
		{
			return null;// a function cannot be unified with a constant
		}
		
		
		else if(t1.v != null && t2.c != null) 
		{
			bl.addBinding(t1.v,t2);//method addBinding in the type BindingList is not applicable for arguments (variable,constant)& is not applicable for arguments (term,term) so I simply included t1.v and t2
			//BindingList bl1 = new BindingList();
			return new BindingList(bl);
		}
			
		else if (t1.v != null && t2.v != null) 
		{
			bl.addBinding(t1.v,t2);//method addBinding in the type BindingList is not applicable for arguments (variable,variable) & is not applicable for arguments (term,term) so I simply included t1.v and t2
			//BindingList bl1 = new BindingList();
			return new BindingList(bl);
			
		}
		else if (t1.v != null && t2.f != null)
		{
			if (!(t2.subst(bl).allVariables().contains(t1.v))) 
															
			{													//I had trouble writing this part of the code and requested help from my tutor Isaiah
				bl.addBinding(t1.v,t2);							//Spearman on Varsitytutors.com He explained to me that our code is simply reading variables from other java files
				return new BindingList(bl);						//files, and we are checking 
																// to see if 'variable' occurs in the  binding list,
																//if it does we must return null, if not we can bind these two together
			}				
			else 
			{
			
				return null;
			}
		}
		else 
			return null;//if none of the terms can be unified to the first return null .. this is a safety net for all of the comparisons 
			
	}

	// unify -- Return the most general unifier for the two provided functions,
	// or null if no unification is possible. The returned binding list
	// should be freshly allocated.
	public BindingList unify(Function f1, Function f2, BindingList bl) {

		    {
		    	if(f1.func.equals(f2.func)) //if the functions are equal then 
		    		return unify(f1.args, f2.args, bl); //both terms can be unified with the binding list
		    	else 
		    		return null;//functions aren't equal so return null
		    }
		}
	

	// unify -- Return the most general unifier for the two provided lists of
	// terms, or null if no unification is possible. The returned binding
	// list should be freshly allocated.
	public BindingList unify(List<Term> ts1, List<Term> ts2, BindingList bl) {
		if (bl == null)
			return (null);
		if ((ts1.size() == 0) && (ts2.size() == 0))
			// Empty lists match other empty lists ...
			return (new BindingList(bl));
		if ((ts1.size() == 0) || (ts2.size() == 0))
			// Ran out of arguments in one list before reaching the
			// end of the other list, which means the two argument lists
			// can't match ...
			return (null);
		List<Term> terms1 = new LinkedList<Term>();
		List<Term> terms2 = new LinkedList<Term>();
		terms1.addAll(ts1);
		terms2.addAll(ts2);
		Term t1 = terms1.get(0);
		Term t2 = terms2.get(0);
		terms1.remove(0);
		terms2.remove(0);
		return (unify(terms1, terms2, unify(t1, t2, bl)));
	}

	// askFacts -- Examine all of the facts in the knowledge base to
	// determine if any of them unify with the given literal, under the
	// given binding list. If a unification is found, return the
	// corresponding most general unifier. If none is found, return null
	// to indicate failure.
	BindingList askFacts(Literal lit, BindingList bl) {
		BindingList mgu = null; // Most General Unifier
		for (Literal fact : kb.facts) {
			mgu = unify(lit, fact, bl);
			if (mgu != null)
				return (mgu);
		}
		return (null);
	}

	// askFacts -- Examine all of the facts in the knowledge base to
	// determine if any of them unify with the given literal. If a
	// unification is found, return the corresponding most general unifier.
	// If none is found, return null to indicate failure.
	BindingList askFacts(Literal lit) {
		return (askFacts(lit, new BindingList()));
	}

	// ask -- Try to prove the given goal literal, under the constraints of
	// the given binding list, using both the list of known facts and the
	// collection of known rules. Terminate as soon as a proof is found,
	// returning the resulting binding list for that proof. Return null if
	// no proof can be found. The returned binding list should be freshly
	// allocated.
	BindingList ask(Literal goal, BindingList bl) {
		BindingList result = askFacts(goal, bl);
		if (result != null) {
			// The literal can be unified with a known fact ...
			return (result);
		}
		// Need to look at rules ...
		for (Rule candidateRule : kb.rules) {
			if (candidateRule.consequent.pred.equals(goal.pred)) {
				// The rule head uses the same predicate as the goal ...
				// Standardize apart ...
				Rule r = candidateRule.standardizeApart();
				// Check to see if the consequent unifies with the goal ...
				result = unify(goal, r.consequent, bl);
				if (result != null) {
					// This rule might be part of a proof, if we can prove
					// the rule's antecedents ...
					result = ask(r.antecedents, result);
					if (result != null) {
						// The antecedents have been proven, so the goal
						// is proven ...
						return (result);
					}
				}
			}
		}
		// No rule that matches has antecedents that can be proven. Thus,
		// the search fails ...
		return (null);
	}

	// ask -- Try to prove the given goal literal using both the list of
	// known facts and the collection of known rules. Terminate as soon as
	// a proof is found, returning the resulting binding list for that proof.
	// Return null if no proof can be found. The returned binding list
	// should be freshly allocated.
	BindingList ask(Literal goal) {
		return (ask(goal, new BindingList()));
	}

	// ask -- Try to prove the given list of goal literals, under the
	// constraints of the given binding list, using both the list of known
	// facts and the collection of known rules. Terminate as soon as a proof
	// is found, returning the resulting binding list for that proof. Return
	// null if no proof can be found. The returned binding list should be
	// freshly allocated.
	BindingList ask(List<Literal> goals, BindingList bl) {
		if (goals.size() == 0) {
			// All goals have been satisfied ...
			return (bl);
		} else {
			List<Literal> newGoals = new LinkedList<Literal>();
			newGoals.addAll(goals);
			Literal goal = newGoals.get(0);
			newGoals.remove(0);
			BindingList firstBL = ask(goal, bl);
			if (firstBL == null) {
				// Failure to prove one of the goals ...
				return (null);
			} else {
				// Try to prove the remaining goals ...
				return (ask(newGoals, firstBL));
			}
		}
	}

}
