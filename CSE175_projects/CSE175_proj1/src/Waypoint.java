//
// Waypoint
//
// This class implements a "node" in a search tree being constructed to find
// a shortest path from one location to another on a map.  Each node includes
// a reference to the corresponding location "state", a reference to its parent
// in the search tree (i.e., the "previous" node), and a collection of children
// nodes (i.e., the "options" for the next step) which remains empty until the
// node is expanded.  Each Waypoint node also records the depth of the node in
// the search tree, the partial path cost from the initial node in the search
// tree to this one, and a heuristic evaluation value for this node.  This
// class provides two noteworthy methods.  First, the "expand" method fills 
// in the "options" list of children of this node, using information embedded
// in this node's Location object.  Second, the "reportSolution" recursive 
// method uses the "previous" references of nodes in the search tree in order
// to output the path from the initial node of the search tree to this node.
//
// David Noelle -- Sun Feb 11 18:26:42 PST 2007
//


import java.io.*;
import java.util.*;


public class Waypoint {
    public Location loc;
    public Waypoint previous;
    public List<Waypoint> options;
    public int depth = 0;
    public double partialPathCost = 0.0;
    public double heuristicValue = 0.0;

    // Default constructor ...
    public Waypoint() {
	this.options = new ArrayList<Waypoint>();
    }

    // Constructor with Location object specified ...
    public Waypoint(Location loc) {
	this();
	this.loc = loc;
    }

    // Constructor with Location object and parent node specified ...
    public Waypoint(Location loc, Waypoint previous) {
	this(loc);
	this.previous = previous;
    }

    // expand -- Fill in the collection of children of this node, stored in
    // it's "options" variable.  Make sure that each child is appropriately
    // linked into the search tree, and make sure that it's partial path cost
    // is correctly calculated.  This version of this method, which takes no
    // arguments, always sets the heuristic values of nodes to zero.
    public void expand() {
	options.removeAll(options);
	for (Road r : loc.roads) {
	    Waypoint option = new Waypoint(r.toLocation, this);
	    option.depth = this.depth + 1;
	    option.partialPathCost = this.partialPathCost + r.cost;
	    option.heuristicValue = 0.0;
	    options.add(option);
	}
    }

    // expand -- Fill in the collection of children of this node, stored in
    // it's "options" variable.  Make sure that each child is appropriately
    // linked into the search tree, and make sure that it's partial path cost
    // is correctly calculated.  This version of this method, which takes a
    // heuristic function object as an argument, uses the given heuristic
    // function to fill in the heuristic values of the children nodes.
    public void expand(Heuristic h) {
	options.removeAll(options);
	for (Road r : loc.roads) {
	    Waypoint option = new Waypoint(r.toLocation, this);
	    option.depth = this.depth + 1;
	    option.partialPathCost = this.partialPathCost + r.cost;
	    option.heuristicValue = h.heuristicFunction(option);
	    options.add(option);
	}
    }

    // isFinalDestination -- Return true if and only if the name of the
    // location corresponding to this node matches the provided argument.
    public boolean isFinalDestination(String destinationName) {
	return (loc.name.equals(destinationName));
    }

    // reportSolution -- Output a textual description of the path from the 
    // root of the search tree (i.e., the initial node) to this node, sending
    // the description to the given stream.  Note that this method is
    // recursive; a recursive call outputs the path up to the parent of this
    // node before the final road segment in the path is described.
    public void reportSolution(OutputStream str) {
	PrintWriter out = new PrintWriter(str, true);
	if (previous == null) {
	    // This is the starting point ...
	    out.printf("START AT ");
	    loc.write(str, false);
	    out.printf(".\n");
	} else {
	    // First provide the solution up until this point ...
	    previous.reportSolution(str);
	    // Now report the road segment to this point ...
	    out.printf("TAKE ");
	    (previous.loc.findRoad(loc)).write(str, true);
	    out.printf(".\n");
	}
    }

}