//
// Heuristic
//
// This class implements a heuristic function for use when performing a map
// search for a shortest path from one location to another.  The heuristic
// function object records the destination location for use when calculating
// the heuristic value of a given search tree node (i.e., Waypoint).  This
// basic heuristic function assigns a value of zero to all nodes, but classes
// that inherit from this one can override this behavior to do something
// more reasonable.
//
// David Noelle -- Wed Feb 21 14:55:23 PST 2007
//


public class Heuristic {
    Location destination;

    // Default constructor ...
    public Heuristic() {
    	this.destination = null;
    }
    
    // Constructor with desination Location object specified ...
    public Heuristic(Location destination) {
	this.destination = destination;
    }

    // getDestination -- Return the destination being used by this heuristic
    // function.
    public Location getDestination() {
	return (destination);
    }

    // setDestination -- Set the destination location to be used by this
    // heuristic function to the given location.
    public void setDestination(Location destination) {
	this.destination = destination;
    }

    // heuristicFunction -- Return the appropriate heuristic values for the
    // given search tree node.  Note that the given Waypoint should not be
    // modified within the body of this function.  For this skeletal class,
    // a value of zero is returned for every node.  Classes that inherit
    // from this one should override this method to do something more
    // reasonable.
    public double heuristicFunction(Waypoint wp) {
	return (0.0);
    }

}