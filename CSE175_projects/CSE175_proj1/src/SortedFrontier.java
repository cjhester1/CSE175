//
// SortedFrontier
//
// This class implements a priority queue of Waypoint objects, with the next
// object to be removed from the queue being determined by a sorting of
// the contained Waypoint objects.  There are three ways in which the
// contents might be sorted, with the sorting strategy determined at the
// time that the priority queue is created.  The contents of the priority
// queue can be sorted by partial path cost, by heuristic value, or by the
// sum of these two statistics.  The contained object with the lowest value
// is the first to be removed.  Note that the insertion method is overloaded
// to accept either an individual Waypoint or a list of multiple Waypoint
// objects.  This class is intended to to be used to implement the frontier
// (i.e., the "fringe" or "open list") of nodes in a search tree.
//
// David Noelle -- Created Tue Feb 27 11:25:05 PST 2007
//                 Modified Wed Oct  6 02:32:34 PDT 2010
//                   (Implemented overloaded "contains" and "find" functions.)
//


import java.util.*;
import java.io.*;


enum SortBy { g, h, f }


class WaypointComparator implements Comparator<Waypoint>, Serializable {
    static final long serialVersionUID = 1;  // Version 1
    SortBy statistic;

    // Default constructor ...
    public WaypointComparator() {
	this.statistic = SortBy.g;
    }

    // Constructor with sorting criterion argument ...
    public WaypointComparator(SortBy strategy) {
	this.statistic = strategy;
    }

    // compare -- Determine which of two Waypoints is "larger", according
    // to the Comparator protocol.
    public int compare(Waypoint wp1, Waypoint wp2) {
	// Extract the appropriate statistics ...
	double val1 = 0.0;
	double val2 = 0.0;
	switch (statistic) {
	case g:
	    val1 = wp1.partialPathCost;
	    val2 = wp2.partialPathCost;
	    break;
	case h:
	    val1 = wp1.heuristicValue;
 	    val2 = wp2.heuristicValue;
	    break;
	case f:
	    val1 = wp1.partialPathCost + wp1.heuristicValue;
	    val2 = wp2.partialPathCost + wp2.heuristicValue;
	    break;
	}
	// Compare values ...
	if (val1 < val2)
	    return (-1);
	if (val1 > val2)
	    return (1);
	if (wp1.equals(wp2)) {
	    // This is the exact same Waypoint ...
	    return (0);
	} else {
	    // These are two Waypoint objects with the same value, but we 
	    // still need to put them in some order.  Otherwise, two nodes
	    // with the same value will "overwrite" each other ...
	    if (wp1.loc.equals(wp2.loc)) {
		// Even the locations are the same, so order the two nodes
		// based on the ordering of their parents in the search 
		// tree ...
		return (this.compare(wp1.previous, wp2.previous));
	    } else {
		// The locations differ, so we can use the alphabetical
		// ordering of their names to order the nodes ...
		return (wp1.loc.name.compareTo(wp2.loc.name));
	    }
	}
    }

}
	

public class SortedFrontier {
    SortBy sortingStrategy;
    SortedSet<Waypoint> fringe;

    // Default constructor ...
    public SortedFrontier() {
	this.sortingStrategy = SortBy.g;
	Comparator<Waypoint> sortingComparator 
	    = new WaypointComparator(this.sortingStrategy);
	this.fringe = new TreeSet<Waypoint>(sortingComparator);
    }

    // Constructor with sorting strategy specified ...
    public SortedFrontier(SortBy strategy) {
	this.sortingStrategy = strategy;
	Comparator<Waypoint> sortingComparator 
	    = new WaypointComparator(this.sortingStrategy);
	this.fringe = new TreeSet<Waypoint>(sortingComparator);
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
	    Waypoint top = fringe.first();
	    fringe.remove(top);
	    return (top);
	}
    }

    // addSorted -- Add the given Waypoint object to the frontier in the
    // appropriate position, given its sorting statistics.
    public void addSorted(Waypoint wp) {
	fringe.add(wp);
    }
    
    // addSorted -- Add the given list of Waypoint objects to the frontier
    // in the appropriate positions, given their sorting statistics.
    public void addSorted(List<Waypoint> points) {
	for (Waypoint wp : points) {
	    addSorted(wp);
	}
    }

    // remove -- Remove a specified Waypoint object from the frontier.
    public void remove(Waypoint wp) {
	fringe.remove(wp);
    }

    // remove -- Remove all of the Waypoint objects in the given list from
    // the frontier.
    public void remove(List<Waypoint> points) {
	for (Waypoint wp : points) {
	    remove(wp);
	}
    }

    // contains -- Return true if and only if the frontier contains a
    // Waypoint with the given Location name.
    public boolean contains(String name) {
	// This linear search is very inefficient, but it cannot be avoided
	// without maintaining a parallel data structure containing the 
	// fringe members (e.g., a HashSet) indexed by location name.
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

    // find -- Return a Waypoint in the frontier with the given location
    // name, or null if there is no such Waypoint.
    public Waypoint find(String name) {
	// This linear search is very inefficient, but it cannot be avoided
	// without maintaining a parallel data structure containing the 
	// fringe members (e.g., a HashSet) indexed by location name.
	for (Waypoint element : fringe)
	    if (name.equals(element.loc.name))
		return (element);
	// The location was not found in the fringe ...
	return (null);
    }

    // find -- Return a Waypoint in the frontier with the given location
    // name, or null if there is no such Waypoint.
    public Waypoint find(Location loc) {
	return (find(loc.name));
    }

    // find -- Return a Waypoint in the frontier with the same location
    // name as the provided Waypoint, or null if there is no such Waypoint.
    public Waypoint find(Waypoint wp) {
	return (find(wp.loc));
    }


}
