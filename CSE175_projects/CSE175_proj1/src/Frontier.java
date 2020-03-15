//
// Frontier
//
// This class implements either a FIFO list (a queue) or a LIFO list (a stack)
// of Waypoint objects, with the kind of list determined by which method is
// used to insert new Waypoint nodes into the list.  In either case, the
// "removeTop" method extracts and returns the next node to be ejected from
// the list.  If the "addToBottom" method is used to insert nodes, then the 
// Frontier object will act as a queue.  If the "addToTop" method is used to
// insert nodes, then the Frontier object will act as a stack.  Both of these
// insertion methods are overloaded to accept either individual Waypoint
// objects or lists of multiple Waypoint objects.  This class is intended to
// to be used to implement the frontier (i.e., the "fringe" or "open list") of
// of nodes in a search tree.
//
// David Noelle -- Created Sun Feb 11 18:39:40 PST 2007
//                 Modified Wed Sep 15 00:09:35 PDT 2010
//                   (Implemented overloaded "contains" function.)
//


import java.util.*;


public class Frontier {
    List<Waypoint> fringe;

    // Default constructor ...
    public Frontier() {
	fringe = new LinkedList<Waypoint>();
    }

    // isEmpty -- Return true if and only if there are currently no nodes in 
    // the frontier.
    public boolean isEmpty() {
	return (fringe.isEmpty());
    }

    // removeTop -- Return the Waypoint object at the top of the frontier
    // list.  Also, remove this node from the frontier.  Return null if the
    // frontier is empty.
    public Waypoint removeTop() {
	if (fringe.isEmpty()) {
	    return (null);
	} else {
	    Waypoint top = fringe.get(0);
	    fringe.remove(0);
	    return (top);
	}
    }

    // addToTop -- Add the given Waypoint object to the top of the frontier
    // list.
    public void addToTop(Waypoint wp) {
	fringe.add(0, wp);
    }

    // addToTop -- Add the given list of Waypoint objects to the top of the 
    // frontier list.
    public void addToTop(List<Waypoint> points) {
	for (Waypoint wp : points) {
	    addToTop(wp);
	}
    }

    // addToBottom -- Add the given Waypoint object to the bottom of the 
    // frontier list.
    public void addToBottom(Waypoint wp) {
	fringe.add(wp);
    }

    // addToBottom -- Add the given list of Waypoint objects to the bottom of
    // the frontier list.
    public void addToBottom(List<Waypoint> points) {
	for (Waypoint wp : points) {
	    addToBottom(wp);
	}
    }

    // contains -- Return true if and only if the frontier contains a
    // Waypoint with the given Location name.
    public boolean contains(String name) {
	// This linear search is very inefficient, but it cannot be avoided
	// without maintaining a parallel data structure containing the 
	// fringe members (e.g., a HashSet).
	for (Waypoint element : fringe)
	    if (name.equals(element.loc.name))
		return (true);
	// The location was not found in the fringe ...
	return (false);
    }

    // contains -- Return true if and only if the frontier contains a
    // Waypoint with the given Location object as its state.
    public boolean contains(Location loc) {
	return (contains(loc.name));
    }

    // contains -- Return true if and only if the frontier contains an
    // equivalent Waypoint (with regard to the Location) to the one provided
    // as an argument.
    public boolean contains(Waypoint wp) {
	return (contains(wp.loc));
    }

}