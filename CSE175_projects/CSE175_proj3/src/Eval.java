//
// Eval
//
// This class implements tools for evaluating game states for the Zombie
// Dice game.  These tools include a static function for calculating
// the expected utility value of a game state using look-ahead to a 
// fixed depth and a static heuristic evaluation function for estimating
// game payoff values for non-terminal states.  Since these tools are
// all static functions, no objects of this class need to be allocated in
// order to use them.  In general, both heuristic evaluation function
// values and expected utility values should be between plus and minus
// "State.win_payoff".
//
// Zombie Dice is a trademark of Steve Jackson Games.  For more information
// about this game, see "zombiedice.sjgames.com".
//
// David Noelle -- Sat Nov  3 16:37:10 PDT 2012
//


public class Eval {

    // Non-terminal states at this limit should be evaluated using
    // the given heuristic evaluation function ...
    static public int depth_limit = 3;

    // value -- This public function returns the payoff value of 
    //          terminal states or the expected utility value of 
    //          non-terminal states, backing up heuristic evaluation 
    //          values once the given depth has reached the depth limit.
    static public double value (State s, int depth) {
	// Stop searching once either a terminal state is reached or the
	// depth limit is reached ...
	if ((s.terminal()) || (depth >= depth_limit)) {
	    return (s.payoff());
	}
	// Keep searching ...
	switch (s.current_choice) {
	case roll:
	    return (Eval.value_roll(s, depth));
	case stop:
	    return (Eval.value_stop(s, depth));
	case undecided:
	    return (Eval.value_choose(s, depth));
	default:
	    // We should never get here ...
	    return (0.0);
	}
    }

    // value -- This public function returns the payoff value of 
    //          terminal states or the expected utility value of 
    //          non-terminal states, backing up heuristic evaluation 
    //          values once the given depth has reached the depth limit.
    static public double value (State s) {
	return (Eval.value(s, 0));
    }

    // value_rolled_hand -- Compute the expected utility value of this 
    //                      state, given that the hand has just been 
    //                      rolled to the specified dice faces.
    static double value_rolled_hand (State rolled_s, int depth) {
	State s = new State(rolled_s);
	double val = 0.0;                // return value

	// Collect brain and blast dice from the hand ...
	s.collectHand();
	// Check to see if the current player has been shotgunned ...
	if (s.shotgunned()) {
	    // This turn is over, so force the choice to stop ...
	    s.current_choice = Choice.stop;
	    // Calculate the expected utility value of the resulting
	    // state by processing the "stop" action ...
	    val = Eval.value(s, depth);
	} else {
	    // The roll is done, but the turn is not, so set the 
	    // choice to undecided ...
	    s.current_choice = Choice.undecided;
	    // Calculate the expected utility value of the resulting 
	    // state.  Note that this is one of the two places where 
	    // the "depth" is incremented ...
	    val = Eval.value(s, (depth + 1));
	}
	// Deallocate storage ...
	s = null;
	// Return the expected value ...
	return (val);
    }

    // value_roll_hand -- Compute the expected utility value of this 
    //                    state, given that the hand is full.  Note that
    //                    this function assumes that there are three dice
    //                    in a hand (i.e., that the value of "have_size" 
    //                    is three).
    static double value_roll_hand (State s, int depth) {
	double val = 0.0;        

	
	for (DieFace d1 : DieFace.values()) 
	{
	    if (d1 != DieFace.invalid) 
	    {													//generate all possible
	    for (DieFace d2 : DieFace.values()) 
		{													// outcome rolls for the dice. We don't want Dieface to be invalid so if it is we 
		    if (d2 != DieFace.invalid) 
		    {												//continue to skip it.
		    	for (DieFace d3 : DieFace.values()) 
		    	{//
		    		if (d3 != DieFace.invalid) 
		    		{
		    			double prob = 
		    			s.rollProb(d1, d2, d3);				// Calculate the prob
		    			s.roll(d1, d2, d3);					// Update the state of the roll
		    			double r_val = 
		    			Eval.value_rolled_hand(s, depth);
		    												// Update the expected utility value over
		    												// all possible rolls
		    			val = val + (r_val * prob);
		    		}
		    	}
		    }
		}
	    }
	}
	return (val);
    }

    // value_roll -- Compute the expected utility value of this state, 
    //               given that the current player will be immediately 
    //               drawing dice and rolling.
    static double value_roll (State s, int depth) {
	double val = 0.0;           // return value

	if (s.numDiceInHand() == State.hand_size) {
	    // No need to draw more dice, so we need to consider all
	    // possible results of rolling the dice in hand ...
	    val = Eval.value_roll_hand(s, depth);
	} else {
	    // Need to draw a die ...
	    if (s.cupIsEmpty()) {
		// The cup is empty.  According to the official rules,
		// we should reuse collected brain dice at this point ...
		State refilled_state = new State(s);
		refilled_state.reuseBrains();
		val = Eval.value_roll(refilled_state, depth);
		refilled_state = null;
	    } else {
		// Iterate over all possible colors for the next die ...
		for (DieColor c : DieColor.values()) {
		    if (c != DieColor.invalid) {
			double this_draw_prob = s.drawProb(c);
			// Draw die of this color ...
			Die d = s.draw(c);
			if (d != null) {
			    // Recursive call ...
			    double draw_val = Eval.value_roll(s, depth);
			    // Update the expected utility value over all 
			    // colors for this die ...
			    val = val + (draw_val * this_draw_prob);
			    // Replace the drawn die in the cup ...
			    s.replace(d);
			}
		    }
		}
	    }
	}
	// Return expected value ...
	return (val);
    }

    // value_stop -- Compute the expected utility value of this state, 
    //               given that the current player will not continue 
    //               to roll at this point.
    static double value_stop (State stop_s, int depth) {
	State s = new State(stop_s);
	double val = 0.0;                // return value

	// Update scores ...
	s.endTurn();
	// Check for end of game ...
	if (s.terminal()) {
	    val = s.payoff();
	} else {
	    // Move to next player ...
	    s.nextPlayer();
	    // Recursively calculate the expected utility value of the
	    // next player's choice node.  Note that this is one of the 
	    // two places where "depth" is incremented.
	    val = Eval.value(s, (depth + 1));
	}
	// Deallocate storage ...
	s = null;
	// Return value ...
	return (val);
    }

    // value_choose -- Compute the expected utility value of each of two 
    //                 actions:  rolling and stopping.  Return the greater 
    //                 of these two values if the computer is the current 
    //                 player, and return the lesser of these two values 
    //                 if the user is the current player.
    static double value_choose (State s, int depth) {
	double eu_roll;  // expected utility value of rolling
	double eu_stop;  // expected utility value of stoping

	// Always roll if no brains have been collected ...
	if (s.brains_collected == 0) {
	    s.current_choice = Choice.roll;
	    eu_roll = Eval.value(s, depth);
	    // Revert the state ...
	    s.current_choice = Choice.undecided;
	    // Return value of rolling ...
	    return (eu_roll);
	}
	// First, calculate the case of choosing to roll ...
	s.current_choice = Choice.roll;
	eu_roll = Eval.value(s, depth);
	// Now, calculate the case of choosing to stop ...
	s.current_choice = Choice.stop;
	eu_stop = Eval.value(s, depth);
	// Revert the state ...
	s.current_choice = Choice.undecided;
	// Which one is better depends on whose turn it is ...
	if (s.current_player == Turn.computer) {
	    // MAX node -- Looking for high values ...
	    if (eu_roll >= eu_stop) {
		return (eu_roll);
	    } else {
		return (eu_stop);
	    }
	} else {
	    // MIN node -- Looking for low values ...
	    if (eu_roll <= eu_stop) {
		return (eu_roll);
	    } else {
		return (eu_stop);
	    }
	}
    }

    // heuristic -- Compute a heuristic evaluation function value for the
    //              specified State object.  This function must be
    //              calculated quickly, with no look-ahead search, and it
    //              should be bounded between plus and minus the value of
    //              "State.win_payoff".  The heuristic evaluation value
    //              is returned.
    static public double heuristic(State s) 
    {

	// Heuristic value to be returned ...
	double value = 0.0;
	double comp_closeness_to_winning = 0;//Here is where I initialized my variables for computer closeness to winning and user closeness to winning
	double user_closeness_to_winning = 0;//the way I came up with these values is based on me playing the game a few times and noticing trends within certain scenarios

	if (s.current_player == Turn.computer)//computers turn
	{
		if (s.brains_to_win- s.comp_brains_eaten <= 2 ) //brains to win = 13 .. if computer brains eaten is 11 or 12  there is an extremely high chance of winning
		{
			if (s.num_terminal_blasts-s.blasts_collected == 1)//terminal blasts = 3..if 2 blasts have been collected in the scenario comp_brains = 11 or 12 I calculated theres a 55% chance of winning the game 
			{
				comp_closeness_to_winning = 0.55;// 55% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected == 2)//if one blast is collected chance is slightly higher
			{
				comp_closeness_to_winning = 0.85;// 85% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected > 2)//no blasts collected chance is at its peak 
			{
				comp_closeness_to_winning = 0.95;//95% chance of winning
			}
			
		}
		
		if (s.brains_to_win- s.comp_brains_eaten <= 9 && s.brains_to_win- s.comp_brains_eaten >= 7) //brains to win = 13 .. if computer brains eaten is 9,8 or 7 there is still a high chance of winning
		{
			if (s.num_terminal_blasts-s.blasts_collected == 1)//terminal blasts = 3..if 2 blasts have been collected in the scenario comp_brains = 7,8,9 I calculated theres a 15% chance of winning the game
			{
				comp_closeness_to_winning = 0.15;//15% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected == 2)//1 blast collected chance is slightly higher
			{
				comp_closeness_to_winning = 0.45;//45% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected > 2)//no blasts collected chance is very good to win even with a score at 7
			{
				comp_closeness_to_winning = 0.65;//65% chance of winning
			}
			
		}
		
		if (s.brains_to_win- s.comp_brains_eaten <=6 && s.brains_to_win- s.comp_brains_eaten >= 5) //brains to win = 13 .. if computer brains eaten is 5 or 6 there is about half chance of winning
		{
			if (s.num_terminal_blasts-s.blasts_collected == 1) ////terminal blasts = 3..if 2 blasts have been collected in the scenario comp_brains = 5 or 6 I calculated theres a 10% chance of winning the game
			{
				comp_closeness_to_winning = 0.10;//10% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected == 2)//1 blast collected the odds of winning aren't too significant
			{
				comp_closeness_to_winning = 0.25;//25% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected > 2)// no blasts collected the odds of winning are slightly higher but not too favorable
			{
				comp_closeness_to_winning = 0.40;//40% chance of winning  
			}
			
		}
		
		if (s.brains_to_win- s.comp_brains_eaten <=4 && s.brains_to_win- s.comp_brains_eaten >= 3) //brains to win = 13 .. if computer brains eaten is 3 or 4 there isnt a high chance of winning
		{
			if (s.num_terminal_blasts-s.blasts_collected == 1)//terminal blasts = 3..if 2 blasts have been collected in the scenario comp_brains = 3 or 4 I calculated theres a .005% chance of winning the game
			{
				comp_closeness_to_winning = 0.005;//.05% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected == 2)//1 blast collected the odds of winning aren't high at all in fact they are extremely low
			{
				comp_closeness_to_winning = 0.07;//7% chance of winning 
			}
			if (s.num_terminal_blasts-s.blasts_collected > 2)//no blasts collected  there is a very very low chance of winning
			{
				comp_closeness_to_winning = 0.10;//10% chance of winning
			}
			
		}
		
		if (s.brains_to_win- s.comp_brains_eaten <=2 && s.brains_to_win- s.comp_brains_eaten >= 1)//brains to win = 13 .. if computer brains eaten is 1 or 2 there is an extremely low chance of winning
		{
			if (s.num_terminal_blasts-s.blasts_collected == 1)//if 2 blasts have been collected there is very little to no chance of winning
			{
				comp_closeness_to_winning = 0.0001;//.001 chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected == 2)//if 1 blast has been collected there is still very little to no chance of winning
			{
				comp_closeness_to_winning = 0.005;//.05% chance of winning
			}
			if (s.num_terminal_blasts-s.blasts_collected > 2)//if no blasts then the chances remain extremely low
			{
				comp_closeness_to_winning = 0.05;//5% chance of winning
			}
			
		}
	}
		if (s.current_player == Turn.user)//users turn
		{
			if (s.brains_to_win- s.user_brains_eaten <= 2 ) 			//For this code I implemented the same exact 'if' statements as I did for my comp_brains eaten
			
			{															//I also gave the user_closeness_to_winning values the same exact percentages because the user and computer face different scenarios 
				if (s.num_terminal_blasts-s.blasts_collected == 1)		//but there will ALWAYS be a scenario in which the user/computer has 1. no blasts 2. one blast 3. two blasts  
				{
					user_closeness_to_winning = 0.55;					//so by measuring the amount of brains collected along with the amount of blasts collected.. this significantly impacts 
				}
				if (s.num_terminal_blasts-s.blasts_collected == 2)		//the closeness to winning percentage of that user/computer and lets them know how close/far they are from winning (+ or - payoff)
				{
					user_closeness_to_winning = 0.85;					//
				}
				if (s.num_terminal_blasts-s.blasts_collected > 2)		//
				{
					user_closeness_to_winning = 0.95;					//
				}
				
			}
			
			if (s.brains_to_win- s.user_brains_eaten <= 9 && s.brains_to_win- s.user_brains_eaten >= 7) 
			{
				if (s.num_terminal_blasts-s.blasts_collected == 1)
				{
					user_closeness_to_winning = 0.15;
				}
				if (s.num_terminal_blasts-s.blasts_collected == 2)
				{
					user_closeness_to_winning = 0.45;
				}
				if (s.num_terminal_blasts-s.blasts_collected > 2)
				{
					user_closeness_to_winning = 0.65;
				}
				
			}
			
			if (s.brains_to_win- s.user_brains_eaten <=6 && s.brains_to_win- s.user_brains_eaten >= 5) 
			{
				if (s.num_terminal_blasts-s.blasts_collected == 1)
				{
					user_closeness_to_winning = 0.10;
				}
				if (s.num_terminal_blasts-s.blasts_collected == 2)
				{
					user_closeness_to_winning = 0.25;
				}
				if (s.num_terminal_blasts-s.blasts_collected > 2)
				{
					user_closeness_to_winning = 0.40;
				}
				
			}
			
			if (s.brains_to_win- s.user_brains_eaten <=4 && s.brains_to_win- s.user_brains_eaten >= 3) 
			{
				if (s.num_terminal_blasts-s.blasts_collected == 1)
				{
					user_closeness_to_winning = 0.005;
				}
				if (s.num_terminal_blasts-s.blasts_collected == 2)
				{
					user_closeness_to_winning = 0.07;
				}
				if (s.num_terminal_blasts-s.blasts_collected > 2)
				{
					user_closeness_to_winning = 0.10;
				}
				
			}
			
			if (s.brains_to_win- s.user_brains_eaten <=2 && s.brains_to_win- s.user_brains_eaten >= 1) 
			{
				if (s.num_terminal_blasts-s.blasts_collected == 1)
				{
					user_closeness_to_winning = 0.0001;
				}
				if (s.num_terminal_blasts-s.blasts_collected == 2)
				{
					user_closeness_to_winning = 0.005;
				}
				if (s.num_terminal_blasts-s.blasts_collected > 2)
				{
					user_closeness_to_winning = 0.05;
				}
				
			}
		}
	
	value = ((s.win_payoff)* (1/(1-Math.exp(comp_closeness_to_winning*6-user_closeness_to_winning*6))))-//Here I use a sigmoid function that I played around with using values on https://www.desmos.com/calculator
			((s.win_payoff)* (1/(1-Math.exp(user_closeness_to_winning*6-comp_closeness_to_winning*6))))	;//I utilized the sigmoid function because it will measure the computers values with the users values and give a value based
	return value;//return s.win_payout*s(x)-s.win_payout*s(y)											//on who is winning
					//x = 6*comp_chance_to_win ... y = 6*user_chance_to_win
    

}
}
