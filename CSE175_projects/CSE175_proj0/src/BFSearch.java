//
// BFSearch
//
// This class implements a simple breadth-first search over the locations in
// a map.  The search is depth-limited -- if a depth limit is reached, the 
// search will terminate and return failure.  Also, repeated state checking
// may be turned on or off using an argument to the main "search" method.
// Finally, objects of this class contain a public variable that maintains
// the total number of nodes expanded during the last call to "search".
//
// David Noelle -- Created Sun Feb 11 18:57:20 PST 2007
//                 Modified Tue Sep 14 23:35:47 PDT 2010
//                   (Do repeated state checking on insertion into the
//                    the frontier, rather than at the time of expansion, as 
//                    described in the 3rd edition of Russell & Norvig.)
//


import java.util.*;


public class BFSearch {
    Map stateSpace;
    public String startName;
    public String finishName;
    int depthLimit = 10000;
    public int expansionCount = 0;

    // Default constructor ...
    public BFSearch() {
	this.stateSpace = null;
	this.startName = "";
	this.finishName = "";
	this.depthLimit = 10000;
	this.expansionCount = 0;
    }

    // Constructor with state space and search parameters specified ...
    public BFSearch(Map stateSpace, String startName, String finishName, 
		    int depthLimit) {
	this();
	this.stateSpace = stateSpace;
	this.startName = startName;
	this.finishName = finishName;
	this.depthLimit = depthLimit;
    }

    public Node search(boolean useRepeatedStateChecking) {
	// Find initial location ...
	Location initialLoc = stateSpace.findLocation(startName);
	if (initialLoc == null)
	    // Invalid starting location name, so return failure ...
	    return (null);
	// Make the initial node ...
	Node initialNode = new Node(initialLoc, null);
	// Create a frontier object ...
	Frontier fringe = new Frontier();
	// Add the initial node to the frontier ...
	fringe.addToBottom(initialNode);
	// If we are checking for repeated states, we're going to need an
	// "explored set", also called a "closed list".  This is optimally 
	// implemented as a "set" of location names, using a hashtable under 
	// the hood.
	Set<String> closedList = null;
	if (useRepeatedStateChecking) {
	    closedList = new HashSet<String>();
	}
	// Initialize the expansion count ...
	expansionCount = 0;
	// Start searching, and keep searching until the fringe is empty ...
	Node node;
	while (!(fringe.isEmpty())) {
	    // Get the next node on the frontier ...
	    node = fringe.removeTop();
	    // Check to see if we have reached the depth limit ...
	    if (node.depth >= depthLimit) {
		// Failure to find a solution within the allowed depth ...
		return (null);
	    }
	    // Check to see if it is a solution ...
	    if (node.isFinalDestination(finishName)) {
		// We have found a goal node, so return it from this method ...
		return (node);
	    } else {
		// We are about to expand this node, so add it to the
		// "explored set" (i.e., the "closed list") if we are doing
		// repeated state checking ...
		if (useRepeatedStateChecking) {
		    closedList.add(node.loc.name);
		}
		// This is not a goal node, so we need to expand it ...
		node.expand();
		expansionCount++;
		// Add the children of this node to the frontier.  If we
		// are doing repeated state checking, only add a node if
		// its state is not already in the frontier and its state
		// is not in the "explored set".
		for (Node child : node.options) {
		    if (!useRepeatedStateChecking ||
			(!(fringe.contains(child)) &&
			 !(closedList.contains(child.loc.name))))
			fringe.addToBottom(child);
		}
		// We're now ready to loop back and try the next node ...
	    }
	}
	// The frontier is empty, so we have failed to find a solution ...
	return (null);
    }

}

