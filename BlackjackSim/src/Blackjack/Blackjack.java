 /*
   This program lets the user play or simulate Blackjack. The computer
   acts as the dealer. The user has a stake of $1000, and
   makes a bet on each game. The user can leave at any time,
   or will be kicked out when he cannot make a minimum bet.
   House rules: By default, the dealer hits on a total of 16 or less,
   as well as soft 17, and stands on a total of 17 or more. No double downs
   after splits. Double deck is used and shuffled when over half the cards have been dealt.
   Only one card given for each hand after splitting aces, and aces cannot be resplit. Splits to max 4 hands.
   In play mode, money is rounded down to the nearest 50 cents to mimic what the casinos do.
   Ideally you should play with money increments that are multiples of the denominator of the blackjack payout.
   In simulation mode, the user has infinite money.
   The simulation calculates its percentage with multiple sittings and utilizes the dealer's money and player's money.
   Simulator max variance is roughly 0.2% right now. Average is around 0.12%.
   The lower side of simulator percentages are roughly equal to the ones found online. Average percentages are about 0.1% above online ones.
   Online percentages seem to be based on simulating a billion or more rounds. This program caps at 100,000,000, which takes a couple minutes to run. 
   A billion is possible though. Would take 15 or more minutes to finish running.
*/

// Completed: basic blackjack + many rule variations. check the AskOption methods to see additions
// TODO: Maybe disallow option duplicates even though only the last will be applied
// TODO: Options: European no hole card?
// TODO: Special non-standard options:

package Blackjack;

import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Math;

public class Blackjack {

    private Scanner scanner = new Scanner(System.in);
    
    private final int UNDECIDED = -1;
    private final int WIN = 1;
    private final int LOSE = 2;
    private final int PUSH = 3;
    private final int SURRENDER = 4;
    private final int COMPLETED = 5;
    
    private double money; // Amount of money user has.
    private double bet;   // Amount user bets on a game.
    private double minbet; // Minimum bet. Lowest minimum bet will be $1
    private double blackjackMult; // Blackjack multiplier (3 to 2: 1.5, 6 to 5: 1.2)
    private boolean didSplit; // Has the user split?
    private int splitCount; // How many times the user has split
    private int splitLimit; // number of splits allowed (0 to 4); standard is 1-3; 3 is default
    // private boolean didSplitAces; // Has the user split aces?
    private boolean evenMoney; // Did the user opt for even money?
    private boolean insured; // Did the user take insurance?
    private Deck deck; // A double deck of cards
    private int simSittings; // Number of sittings to average in a simulation
    private int simRounds; // Number of rounds to run in a sitting
    public ArrayList<String> availableOptions; // List of all available options
    public ArrayList<String> options; // User options to change odds of the game
    private int llDouble; // low limit for doubling
    private int ulDouble; // high limit for doubling
    private int charlieCount; // number of cards to win by Charlie rule
    private String betStratOpt; // string to identify the betting strategy to used based on the true count
    private double baseBet; // base bet for simulations
    
    public int bestNum; // ideal number for the game
    public int hitNum; // highest number dealer still has to hit
    public int surrendNum; // surrender flag (-1 for not set, 1 for early surrender, 2 for late surrender)
    
    private boolean stratWindow; // flag for whether to display the strategy window to the user for optional simulation strategy changes
    private int percentComplete; // for testing purposes to see how far the simulation is to completing
    
    public static Blackjack b; // The way to access the blackjack program from other files
    
	
    public static void main(String[] args) {
    	b = new Blackjack();
    	b.startBlackjack();
    } // end main()
    
    
/*** The method for the class that gets everything started. ***/
    
    public Blackjack() {
    	setDefaults();
    	setAvailableOptions();
    }
    
    void startBlackjack() {
    	
    	String sps = "";
    	char ps = 'a';
    	
    	System.out.println("Welcome to the game of blackjack.");
        System.out.println();
        System.out.print("Play (P) or Simulate (S)? ");
        
        while (ps != 'P' && ps != 'S') {
        	sps = scanner.nextLine();
            if (!isSingleChar(sps)) {
                System.out.print("Please respond P or S: ");
            } else {
                sps = sps.replaceAll("\\s","");
                ps = Character.toUpperCase( sps.charAt(0) );
                if (ps != 'P' && ps != 'S') {
                    System.out.print("Please respond P or S: ");
                }
            }
        }
        
        if (ps == 'P') {
        	
        	askAndSetOptions();
            
            System.out.println();
   
	        String sbet;		// String input for bet before conversion
	        ArrayList<BlackjackHand> outcomeHands; // Hands of the user after the round
	   
	        while (true) {
	        	money = Math.floor(money * 10) / 10; // floors to one decimal place
	            System.out.println("You have " + money + " dollars.");
	            sbet = "";
	            bet = -1;
	            while (bet < 0 || bet > money || (bet > 0 && bet < minbet)) {
	                System.out.println("How much money do you want to bet? (Minimum bet: " + minbet + ". Enter 0 to end.)");
	                System.out.print("? ");
	                sbet = scanner.nextLine();
	                if (!isValidBet(sbet)) {
	            	   System.out.println("Your answer must be between " + minbet + " and " + money + ". (50 cent increments only)");
	                } else {
	            	    sbet = sbet.replaceAll("\\s","");
		                bet = Double.parseDouble(sbet);
		                if (bet < 0 || bet > money || (bet > 0 && bet < minbet)) {
		                    System.out.println("Your answer must be between " + minbet + " and " + money + ". (50 cent increments only)");
		                }
	                }
	            }
	            if (bet == 0) {
	                break;
	            }
	            money -= bet;
	            outcomeHands = playBlackjack();
	            if (outcomeHands.size() == 1) { // means an insurance hand completed and everything was already taken care of
	            	System.out.println();
	            	continue;
	            } else if (outcomeHands.get(0).getOutcome() == SURRENDER) { // user surrender and hand completes
	            	money = money + (bet / 2.0) - ((bet / 2.0) % 0.5);
	            	System.out.println();
	            	continue;
	            }
	            if (!(outcomeHands.get(0).hasBlackjack() || outcomeHands.get(1).hasBlackjack()) && (outcomeHands.get(outcomeHands.size() - 1).getOutcome() != COMPLETED)) { // COMPLETED is Charlie rule kludge
	            	if ((outcomeHands.size() - 1) == 1) { // -1 to exclude delaer's hand at the end
		            	System.out.println();
		            	if (outcomeHands.get(0).getOutcome() == PUSH) {
		            		System.out.println("Dealer ties. You push.");
		            	} else if (outcomeHands.get(0).getOutcome() == LOSE) {
		            		if (outcomeHands.get(0).didBust()) { // Already stated user busts, so no need to display user's total
		            			System.out.println("You lose.");
		            		} else {
		            			System.out.println("Dealer wins, " + outcomeHands.get(1).getBlackjackValue() + " points to " + outcomeHands.get(0).getBlackjackValue() + ".");
		            		}
		            	} else {
		 	           		System.out.println("You win, " + outcomeHands.get(0).getBlackjackValue() + " points to " + outcomeHands.get(1).getBlackjackValue() + ".");
		 	           	}
		            } else {
		            	for (int i=0; i < outcomeHands.size() - 1; i++) {
		            		System.out.println();
		            		if (outcomeHands.get(i).getOutcome() == PUSH) {
			            		System.out.println("Dealer ties on hand " + (i+1) + ". You push.");
			            	} else if (outcomeHands.get(i).getOutcome() == LOSE) {
			            		if (outcomeHands.get(i).didBust()) { // Already stated user busts, so no need to display user's total
			            			System.out.println("You busted on hand " + (i+1) + ".");
			            		} else {
			            			System.out.println("Dealer wins on hand " + (i+1) + ", " + outcomeHands.get(outcomeHands.size() - 1).getBlackjackValue() + " points to " + outcomeHands.get(i).getBlackjackValue() + ".");
			            		}
			            	} else {
			            		if (outcomeHands.get(i).didCharlie()) {
			            			System.out.println("You won by the " + charlieCount + "-card Charlie rule on hand " + (i+1) + ".");
			            		} else {
			 	           			System.out.println("You win on hand " + (i+1) + ", " + outcomeHands.get(i).getBlackjackValue() + " points to " + outcomeHands.get(outcomeHands.size() - 1).getBlackjackValue() + ".");
			            		}
			 	           	}
		            	}
		            }
	            }
	            for (BlackjackHand b : outcomeHands) {
	            	if (b.getOutcome() == WIN) {
	            		if (b.hasBlackjack()) { // need to subtract the remainder of blackjacks if they are not 0.5 increments
	            			money = money + bet + (blackjackMult * bet) - ((blackjackMult * bet) % 0.5);
	            		} else {
	            			money = money + 2.0 * bet * b.getNumBets();
	            		}
		            } else if (b.getOutcome() == PUSH) {
		                money = money + bet * b.getNumBets();
		            }
	            }
	            
	            /*  Shuffle the deck if needed. */
	            if (deck.shouldShuffle()) {
	            	deck.shuffle();
	            	System.out.println();
	                if (deck.getNumDecks() == 1) {
	                	System.out.println("The deck has been reshuffled.");
	                } else {
	                	System.out.println("The decks have been reshuffled.");
	                }
	            }
	            
	            System.out.println();
	            if (money < minbet) {
	                System.out.println("Looks like you don't have enough money for another bet!");
	                break;
	            }
	        }
	        
	        money = Math.floor(money * 10) / 10; // floors to one decimal place
	        System.out.println();
	        System.out.println("You leave with $" + money + '.');
        } else {
        	simulateBlackjack();
        }
    }
    
/*** End starting method. ***/

    
/*** All the methods for playing blackjack are in this section. ***/
   
    ArrayList<BlackjackHand> playBlackjack() {
        // Let the user play one game of Blackjack.
        // Return true if the user wins, false if the user loses.

        BlackjackHand dealerHand;   // The dealer's hand.
        BlackjackHand userHand;     // The user's hand.
        ArrayList<BlackjackHand> gameHands; // All the hands in the game. Dealer's will be added in last before return.
        
        // deck = new Deck(); // Currently a new deck for each game
        dealerHand = new BlackjackHand();
        userHand = new BlackjackHand();
        gameHands = new ArrayList<BlackjackHand>();
        gameHands.add(userHand);
        
        evenMoney = false; // setting default insurance constraints
        insured = false;
        int handsPlayed = 0; // For multiple hands due to splits
        int handsBusted = 0; // To deal with split situations
        didSplit = false; // Set true if a split has occurred
        splitCount = 0; // User has yet to split
        // didSplitAces = false;
    
        /*  Signify new round to deck, and deal two cards to each player. */
        deck.newRound();
        userHand.addCard( deck.dealCard() );
        dealerHand.addCard( deck.dealCard() );
        userHand.addCard( deck.dealCard() );
        dealerHand.addCard( deck.dealCard() );

        // System.out.println();
        // System.out.println();
        
        /* Check for early surrender. */
        if (canEarlySurrender()) {
        	System.out.println();
        	System.out.println("Your cards are:");
	        for ( int i = 0; i < userHand.getCardCount(); i++ ) {
	            System.out.println("    " + userHand.getCard(i));
	        }
	        System.out.println("Your total is " + userHand.getBlackjackValue());
	        System.out.println();
	        System.out.println("Dealer is showing the " + dealerHand.getCard(0));
        	System.out.println();
        	System.out.print("Early surrender? Yes (Y) or No (N): ");
        	String surrendTempStr = ""; // User's response as String.
            char surrendTempChar = 'a';  // User's response, 'Y' or 'N'.
            while ((surrendTempChar != 'Y') && (surrendTempChar != 'N')) {
                surrendTempStr = scanner.nextLine();
                if (!isSingleChar(surrendTempStr)) {
                    System.out.print("Please respond Y or N: ");
                } else {
                    surrendTempStr = surrendTempStr.replaceAll("\\s","");
                    surrendTempChar = Character.toUpperCase( surrendTempStr.charAt(0) );
                    if (surrendTempChar != 'Y' && surrendTempChar != 'N') {
                        System.out.print("Please respond Y or N: ");
                    }
                }
            }
            if (surrendTempChar == 'Y') {
            	userHand.setOutcome(SURRENDER);
            	gameHands.add(dealerHand);
            	System.out.println();
                System.out.println("User surrenders.");
                return gameHands;
            }	
        }
        
        if (insuranceCheck(userHand, dealerHand)) {
        	return gameHands;
        }

        /* Check if one of the players has Blackjack (two cards totaling to 21).
         The player with Blackjack wins the game.
        */
        
        if (dealerHand.getBlackjackValue() == bestNum && userHand.getBlackjackValue() == bestNum) {
        	System.out.println();
            System.out.println("Dealer has the " + dealerHand.getCard(0)
                                   + " and the " + dealerHand.getCard(1) + ".");
            System.out.println("User has the " + userHand.getCard(0)
                                     + " and the " + userHand.getCard(1) + ".");
            System.out.println();
            System.out.println("Dealer has Blackjack and you have Blackjack. You push.");
            userHand.blackjackHand();
            dealerHand.blackjackHand();
            userHand.setOutcome(PUSH);
            gameHands.add(dealerHand);
            return gameHands;
        }
      
        if (dealerHand.getBlackjackValue() == bestNum) {
        	System.out.println();
            System.out.println("Dealer has the " + dealerHand.getCard(0)
                                   + " and the " + dealerHand.getCard(1) + ".");
            System.out.println("User has the " + userHand.getCard(0)
                                     + " and the " + userHand.getCard(1) + ".");
            System.out.println();
            System.out.println("Dealer has Blackjack. Dealer wins.");
            dealerHand.blackjackHand();
            userHand.setOutcome(LOSE);
            gameHands.add(dealerHand);
            return gameHands;
        }
      
        if (userHand.getBlackjackValue() == bestNum) {
        	System.out.println();
            System.out.println("Dealer has the " + dealerHand.getCard(0)
                                   + " and the " + dealerHand.getCard(1) + ".");
            System.out.println("User has the " + userHand.getCard(0)
                                     + " and the " + userHand.getCard(1) + ".");
            System.out.println();
            System.out.println("You have Blackjack. You win.");
            userHand.blackjackHand();
            userHand.setOutcome(WIN);
            gameHands.add(dealerHand);
            return gameHands;
        }
      
        /*  If neither player has Blackjack, play the game.  First the user 
          gets a chance to draw cards (i.e., to "Hit").  The while loop ends 
          when the user chooses to "Stand".  If the user goes over 21,
          the user loses immediately.
        */
        
        while (gameHands.size() > handsPlayed) {
        	userHand = gameHands.get(handsPlayed);
	        while (true) {
	        	
	        	if (userHand.getCardCount() == charlieCount) { // Charlie rule automatic win
	        		userHand.charlied();
	        		userHand.setOutcome(WIN);
	        		System.out.println();
	                System.out.println("User wins by the " + charlieCount + "-card Charlie rule.");
	                break;
	        	}
	        	
	            /* Display user's cards, and let user decide to Hit, Stand, Double, or Split. */
	        	
	        	if (userHand.getCardCount() == 1) { // split situation
	        		Card newCard = deck.dealCard();
	                userHand.addCard(newCard);
	                System.out.println();
	                System.out.println("You receive the " + newCard);
	                if (userHand.getCard(0).getValueAsString().equals("Ace")) { // only one card after splitting aces
		                if (!options.contains("DSA") && !options.contains("RSA")) {
	                		System.out.println();
		                	System.out.println("Your cards for hand " + (handsPlayed + 1) + " are:");
		                	for ( int i = 0; i < userHand.getCardCount(); i++ ) {
		    	                System.out.println("    " + userHand.getCard(i));
		    	            }
		                	System.out.println("Your total is " + userHand.getBlackjackValue());
		                	break;
		                } else if (options.contains("DSA") && !options.contains("RSA")) {
		                	splitCount = 100; // kludge
		                } else if (!options.contains("DSA") && options.contains("RSA")) {
		                	// if second card is ace, give option to split if possible then continue, else break
		                	System.out.println();
		                	System.out.println("Your cards for hand " + (handsPlayed + 1) + " are:");
		                	for ( int i = 0; i < userHand.getCardCount(); i++ ) {
		    	                System.out.println("    " + userHand.getCard(i));
		    	            }
		                	System.out.println("Your total is " + userHand.getBlackjackValue());
		                	if (canSplit(userHand)) { // means the user was dealt another Ace for possible resplitting options
		                		System.out.println();
		                		System.out.print("Would you like to resplit your Aces? Yes (Y) or No (N): ");
		                    	String aceSplitStr = ""; // User's response as String.
		                        char aceSplitChar = 'a';  // User's response, 'Y' or 'N'.
		                        while ((aceSplitChar != 'Y') && (aceSplitChar != 'N')) {
		                        	aceSplitStr = scanner.nextLine();
		                            if (!isSingleChar(aceSplitStr)) {
		                                System.out.print("Please respond Y or N: ");
		                            } else {
		                            	aceSplitStr = aceSplitStr.replaceAll("\\s","");
		                            	aceSplitChar = Character.toUpperCase( aceSplitStr.charAt(0) );
		                                if (aceSplitChar != 'Y' && aceSplitChar != 'N') {
		                                    System.out.print("Please respond Y or N: ");
		                                }
		                            }
		                        }
		                        if (aceSplitChar == 'Y') {
		                        	money -= bet;
		        	            	didSplit = true;
		        	            	splitCount += 1;
		        	            	// if (userHand.getCard(0).getValueAsString().equals("Ace")) {
		        	            	//	  didSplitAces = true;
		        	            	// }
		        	            	BlackjackHand newHand = new BlackjackHand();
		        	            	gameHands.add(newHand);
		        	            	newHand.addCard(userHand.getCard(1));
		        	            	userHand.removeCard(1);
		        	            	System.out.println();
		        	                System.out.println("User splits.");
		                            continue;
		                        } else {
		                        	break;
		                        }
		                	} else {	
			                	break;
		                	}
		                }
	                }
	        	}
	            // System.out.println();
	            System.out.println();
	            if (gameHands.size() > 1) { // split situation
	            	System.out.println("Your cards for hand " + (handsPlayed + 1) + " are:");
	            } else {
	            	System.out.println("Your cards are:");
	            }
	            for ( int i = 0; i < userHand.getCardCount(); i++ ) {
	                System.out.println("    " + userHand.getCard(i));
	            }
	            System.out.println("Your total is " + userHand.getBlackjackValue());
	            System.out.println();
	            System.out.println("Dealer is showing the " + dealerHand.getCard(0));
	            System.out.println();
	            printPlayOptions(userHand);
	            
	            String stuserAction = ""; // User's response as String.
	            char userAction = 'a';  // User's response, 'H' or 'S'.
	            while ((userAction != 'H') && (userAction != 'S') && (userAction != 'D') && (userAction != 'P') && (userAction != 'R')) {
	                stuserAction = scanner.nextLine();
	                if (!isSingleChar(stuserAction)) {
	                    printResponseOptions(userHand);
	                } else {
	                    stuserAction = stuserAction.replaceAll("\\s","");
	                    userAction = Character.toUpperCase( stuserAction.charAt(0) );
	                    if (userAction == 'D') {
	                    	if (!canDouble(userHand)) {
	                    		userAction = 'a';
	                    		printResponseOptions(userHand);
	                    	}
	                    } else if (userAction == 'P') {
	                    	if (!canSplit(userHand)) {
	                    		userAction = 'a';
	                    		printResponseOptions(userHand);
	                    	}
	                    } else if (userAction == 'R') {
	                    	if (!canLateSurrender(userHand)) {
	                    		userAction = 'a';
	                    		printResponseOptions(userHand);
	                    	}
	                    } else if (userAction != 'H' && userAction != 'S' && userAction != 'D' && userAction != 'P' && userAction != 'R') {
	                    	printResponseOptions(userHand);
	                    }
	                }
	            }
	
	            /* If the user Hits, the user gets a card.  If the user Stands,
	               the loop ends (and it's the dealer's turn to draw cards).
	               If the user Doubles, the user gets one card and breaks the loop.
	            */
	
	            if ( userAction == 'S' ) {
	                // Loop ends; user is done taking cards.
	            	System.out.println();
	                System.out.println("User stands.");
	                break;
	            } else if ( userAction == 'D') {
	            	money -= bet;
	            	userHand.doubled();
	            	Card newCard = deck.dealCard();
	                userHand.addCard(newCard);
	                System.out.println();
	                System.out.println("User double downs.");
	                System.out.println("Your card is the " + newCard);
	                System.out.println("Your total is now " + userHand.getBlackjackValue());
	                if (userHand.getBlackjackValue() > bestNum) {
	                    System.out.println();
	                    System.out.println("You busted by going over " + bestNum +".");
	                    userHand.busted();
	                    userHand.setOutcome(LOSE);
	                    handsBusted += 1;
	                    if (gameHands.size() == handsBusted) { // this is the last hand to play and all others have busted
	                    	System.out.println("Dealer's other card was the " + dealerHand.getCard(1)); 
	                    	gameHands.add(dealerHand);
	                        return gameHands;
	                    }
	                }
	                break;
	            } else if ( userAction == 'P') {
	            	money -= bet;
	            	didSplit = true;
	            	splitCount += 1;
	            	// if (userHand.getCard(0).getValueAsString().equals("Ace")) {
	            	//	  didSplitAces = true;
	            	// }
	            	BlackjackHand newHand = new BlackjackHand();
	            	gameHands.add(newHand);
	            	newHand.addCard(userHand.getCard(1));
	            	userHand.removeCard(1);
	            	System.out.println();
	                System.out.println("User splits.");
	            } else if ( userAction == 'R') {
	            	userHand.setOutcome(SURRENDER);
	            	gameHands.add(dealerHand);
	            	System.out.println();
	                System.out.println("User surrenders.");
                    return gameHands;
	            } else {  // userAction is 'H'.  Give the user a card.  
	                   // If the user goes over 21, the user loses.
	                Card newCard = deck.dealCard();
	                userHand.addCard(newCard);
	                System.out.println();
	                System.out.println("User hits.");
	                System.out.println("Your card is the " + newCard);
	                System.out.println("Your total is now " + userHand.getBlackjackValue());
	                if (userHand.getBlackjackValue() > bestNum) {
	                    System.out.println();
	                    System.out.println("You busted by going over " + bestNum +".");
	                    userHand.busted();
	                    userHand.setOutcome(LOSE);
	                    handsBusted += 1;
	                    if (gameHands.size() == handsBusted) { // this is the last hand to play and all others have busted
	                    	System.out.println("Dealer's other card was the " + dealerHand.getCard(1)); 
	                    	gameHands.add(dealerHand);
	                        return gameHands;
	                    }
	                    break;
	                }
	            }
	           
	        } // end while loop
	        handsPlayed += 1;
        } // end second while loop
      
        // Check to see if all user hands were finished
        boolean finished = true;
        for (BlackjackHand b: gameHands) {
        	if (b.getOutcome() == UNDECIDED) {
        		finished = false;
        	}
        }	
        
        /* If we get to this point, the user has Stood with 21 or less.  Now, it's
         the dealer's chance to draw.  Dealer draws cards until the dealer's
         total is > 16.  If dealer goes over 21, the dealer loses.
        */
        if (!finished) {
	        System.out.println();
	        System.out.println("Dealer's cards are");
	        System.out.println("    " + dealerHand.getCard(0));
	        System.out.println("    " + dealerHand.getCard(1));
	        while (dealerHand.getBlackjackValue() <= hitNum || dealerHand.softHit()) {
	            Card newCard = deck.dealCard();
	            System.out.println("Dealer hits and gets the " + newCard);
	            dealerHand.addCard(newCard);
	            if (dealerHand.getBlackjackValue() > bestNum) {
	                System.out.println();
	                System.out.println("Dealer busted by going over " + bestNum + ".");
	                dealerHand.busted();
	                for (BlackjackHand b: gameHands) {
	                	if (b.getOutcome() == UNDECIDED) {
	                		b.setOutcome(WIN);
	                	}
	                }
	                gameHands.add(dealerHand);
	                return gameHands;
	            }
	        }
	        for (BlackjackHand b: gameHands) {
	        	if (b.getOutcome() == UNDECIDED) {
	        		if (dealerHand.getBlackjackValue() == b.getBlackjackValue()) {
	        			b.setOutcome(PUSH);
	        		} else if (dealerHand.getBlackjackValue() > b.getBlackjackValue()) {
	        			b.setOutcome(LOSE);
	        		} else {
	        			b.setOutcome(WIN);
	        		}
	        	}
	        }
	        
	        System.out.println("Dealer's total is " + dealerHand.getBlackjackValue());        
        } else {
        	dealerHand.setOutcome(COMPLETED);
        }
        gameHands.add(dealerHand);
        return gameHands;

    }  // end playBlackjack()
    
    /* H for userhand, b for bet, m for money left. */
    boolean canDouble(BlackjackHand h) { // create overrided versions for variants
    	if (h.getCardCount() != 2 && !options.contains("DAN")) {
    		return false;
    	} else if (bet > money) {
    		return false;
    	} else if (didSplit && !options.contains("DAS")) {
    		return false;
    	} else if (llDouble == 0 || h.getBlackjackValue() < llDouble || h.getBlackjackValue() > ulDouble) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    boolean canSplit(BlackjackHand h) { // create overrided versions for variants
    	if (h.getCardCount() != 2) {
    		return false;
    	} else if (bet > money) {
    		return false;
    	} else if (splitCount >= splitLimit) {
    		return false;
    	} else if (h.getCard(0).getValue() == h.getCard(1).getValue()) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    void printPlayOptions(BlackjackHand h) {
    	if (canDouble(h) && canSplit(h) && canLateSurrender(h)) {
    		System.out.print("Hit (H), Stand (S), Double Down (D), Split (P), or Surrender (R)? ");
    	} else if (canDouble(h) && canSplit(h)) {
        	System.out.print("Hit (H), Stand (S), Double Down (D), or Split (P)? ");
    	} else if (canDouble(h) && canLateSurrender(h)) {
        	System.out.print("Hit (H), Stand (S), Double Down (D), or Surrender (R)? ");
    	} else if (canSplit(h) && canLateSurrender(h)) {
        	System.out.print("Hit (H), Stand (S), Split (P), or Surrender (R)? ");
        } else if (canDouble(h)) {
        	System.out.print("Hit (H), Stand (S), or Double Down (D)? ");
        } else if (canSplit(h)) {
        	System.out.print("Hit (H), Stand (S), or Split (P)? ");
        } else if (canLateSurrender(h)) {
        	System.out.print("Hit (H), Stand (S), or Surrender (R)? ");
        } else {
        	System.out.print("Hit (H) or Stand (S)? ");
        }
    }
    
    void printResponseOptions(BlackjackHand h) {
    	if (canDouble(h) && canSplit(h) && canLateSurrender(h)) {
    		System.out.print("Please respond H, S, D, P, or R: ");
    	} else if (canDouble(h) && canSplit(h)) {
        	System.out.print("Please respond H, S, D, or P: ");
    	} else if (canDouble(h) && canLateSurrender(h)) {
        	System.out.print("Please respond H, S, D, or R: ");
    	} else if (canSplit(h) && canLateSurrender(h)) {
        	System.out.print("Please respond H, S, P, or R: ");
        } else if (canDouble(h)) {
        	System.out.print("Please respond H, S, or D: ");
        } else if (canSplit(h)) {
        	System.out.print("Please respond H, S, or P: ");
        } else if (canLateSurrender(h)) {
        	System.out.print("Please respond H, S, or R: ");
        } else {
        	System.out.print("Please respond H or S: ");
        }
    }
    
    void askAndSetOptions() {
    	System.out.println();
        
        String opts = "0";
        do {
        	minbet = 5; // kludge set
			money = 1000; // kludge set
        	System.out.println("What game options would you like to play with?");
        	System.out.println("Press enter for a default game.");
        	System.out.println("Type 'H' for options help. Type 'R' to show default rules.");
        	System.out.println("Separate your options with commas.");
        	System.out.print("? ");
        	opts = scanner.nextLine();
        } while (!validOptions(opts) || !moneyValid());
        
        applyOptions(opts); // for normal cases
        // System.out.println(options); // Comment out when done testing
    }
    
    boolean validOptions(String opts) {
    	if (opts.matches("[ \\t]*")) {
    		return true;
    	} else if (opts.matches("[ \\t]*[Hh][ \\t]*")) {
    		System.out.println();
    		System.out.println("OPTIONS:");
    		System.out.println("DAS - Double down allowed after split.");
    		System.out.println("NHS - Dealer does not hit on soft 17.");
    		System.out.println("ESUR - Early surrender allowed.");
    		System.out.println("LSUR - Late surrender allowed.");
    		System.out.println("       Note: only one surrender type allowed.");
    		System.out.println("DAN - Double on any number of cards.");
    		System.out.println("DSA - May draw to split Aces.");
    		System.out.println("RSA - May resplit Aces.");
    		System.out.println("BFC - Burn the first card of a newly shuffled deck.");
    		System.out.println("SM[num] - Set the starting amount of money. e.g. SM2000.");
    		System.out.println("          Must be an integer. Lowest amount is $1, highest is $100000.");
    		System.out.println("MB[num] - Set the minimum bet. e.g. MB10.");
    		System.out.println("          Lowest min is $1, must be an integer, and cannot be greater than total money.");
    		System.out.println("ND[num] - Set the number of decks to play with. e.g. ND2.");
    		System.out.println("          Lowest is 1 deck, highest is 8 decks.");
    		System.out.println("BP[num]:[num] - Set the blackjack payout to a different ratio. e.g. BP6:5.");
    		System.out.println("          BP6:5 means the payout is 6 to 5 on a blackjack");
    		System.out.println("DO[num]:[num] - Set the lower and upper limits of hand totals that can be doubled. e.g. DO5:11.");
    		System.out.println("          DO5:11 means you may only double on hand totals between 5 and 11.");
    		System.out.println("          Lowest limit is 4 and highest limit is 21. Use DO0:0 to disallow doubling.");
    		System.out.println("CC[num]/[num] - Pick the fractional placement of the cut card in the deck. e.g. CC1/2.");
    		System.out.println("          CC1/2 means going through half the cards in the deck(s) before reshuffling.");
    		System.out.println("          Fraction must be between 0 and 1.");
    		System.out.println("SN[num] - Set the number of hands you can split to. e.g. SN3.");
    		System.out.println("          Lowest is 1 hand (no splitting allowed) and highest is 5 hands.");
    		System.out.println("CH[num] - Include the automatic winner Charlie rule and set the winning value. e.g. CH6.");
    		System.out.println("          CH6 means the user automatically wins if there are 6 cards in their hand without busting.");
    		System.out.println("          Lowest is 3 cards and highest is 10 cards. Doubling negates the Charlie rule.");
    		System.out.println("DT[num] - The dealer draws cards until the hand total is 'num' or more, instead of the standard 17. e.g. DT16.");
    		System.out.println("          The dealer also hits on soft totals equal to 'num', unless the 'NHS' option is also used.");
    		System.out.println("          Lowest total is 4 and highest total is 21.");
    		System.out.println();
    		return false;
    	} else if (opts.matches("[ \\t]*[Rr][ \\t]*")) {
    		System.out.println();
    		System.out.println("DEFAULT RULES:");
    		System.out.println("User has a stake of $1000, with minimum bet $5.");
    		System.out.println("Double deck is used and reshuffled when over half the cards have been dealt.");
    		System.out.println("Blackjack pays 3 to 2.");
    		System.out.println("Dealer hits on soft 17.");
    		System.out.println("No double downs after splits.");
    		System.out.println("Only one card is dealt to split Aces, and Aces cannot be resplit.");
    		System.out.println("User can only split to 4 hands.");
    		System.out.println("Surrendering is not allowed.");
    		System.out.println("Insurance is in effect.");
    		System.out.println();
    		return false;
    	}
    	ArrayList<String> temp = new ArrayList<String>(Arrays.asList(opts.replaceAll("\\s","").split(",")));
    	for (String str : temp) {
    		if (!validOption(str.toUpperCase())) {
    			System.out.println();
    			System.out.println("Invalid option(s).");
    			System.out.println();
    			return false;
    		}
    	}
    	return true;
    }
    
    boolean validOption(String str) {
    	if (availableOptions.contains(str) || str.matches("ND[1-8]")) {
    		return true;
    	} else if (str.matches("MB([1-9]\\d?\\d?\\d?\\d?|100000)")) {
    		minbet = Integer.parseInt(str.substring(2));
    		return true;
    	} else if (str.matches("SM([1-9]\\d?\\d?\\d?\\d?|100000)")) {
    		money = Integer.parseInt(str.substring(2));
    		return true;
    	} else if (str.matches("(BP0)|(BP[1-9]\\d?\\d?:[1-9]\\d?\\d?)|(BP0:[1-9]\\d?\\d?)")) {
    		return true;
    	} else if (str.matches("(CC[1-9]\\d?\\d?/[1-9]\\d?\\d?)|(CC0/[1-9]\\d?\\d?)|(CC0)|(CC1)")) {
    		String[] temp = str.split("/");
    		if (temp.length == 1) {
    			return true;
    		} else {
    			return (Integer.parseInt(temp[0].substring(2)) <= Integer.parseInt(temp[1]));
    		}
    	} else if (str.matches("SN[1-5]")) {
    		return true;
    	} else if (str.matches("DO[1-2]?[0-9]:[1-2]?[0-9]")) {
    		String[] temp = str.split(":");
    		int temp1 = Integer.parseInt(temp[0].substring(2));
    		int temp2 = Integer.parseInt(temp[1]);
    		if (temp1 == 0 && temp2 == 0) {
    			return true;
    		} else if (temp1 >= 4 && temp1 <= 21 && temp2 >= 4 && temp2 <= 21 && temp1 <= temp2) {
    			return true;
    		} else {
    			return false;
    		}
    	} else if (str.matches("CH([3-9]|10)")) {
    		return true;
    	} else if (str.matches("DT[1-2]?[0-9]")) {
    		int temp1 = Integer.parseInt(str.substring(2));
    		if (temp1 >= 4 && temp1 <= 21) {
    			return true;
    		} else {
    			return false;
    		}
    	}
    	return false;
    }
    
    void applyOptions(String opts) {
    	options = new ArrayList<String>();
    	ArrayList<String> temp = new ArrayList<String>(Arrays.asList(opts.replaceAll("\\s","").split(",")));
    	for (String str : temp) {
    		applyOption(str.toUpperCase());
    		options.add(str.toUpperCase());
    	}
    	// now for special cases
    	for (String str : temp) {
    		applySpecialOption(str.toUpperCase());
    	}
    }
    
    void applyOption(String str) {
    	if (str.contains("MB")) {
    		minbet = Integer.parseInt(str.substring(2));
    	} else if (str.contains("SM")) {
    		money = Integer.parseInt(str.substring(2));
    	} else if (str.contains("ND")) {
    		deck = new Deck(Integer.parseInt(str.substring(2)));
    	} else if (str.contains("BP")) {
    		String[] temp = str.split(":");
    		if (temp.length == 1) {
    			blackjackMult = 0.0;
    		} else {
    			blackjackMult = (1.0 * Integer.parseInt(temp[0].substring(2))) / (1.0 * Integer.parseInt(temp[1]));
    		}
    	} else if (str.equals("ESUR")) {
    		surrendNum = 1;
    	} else if (str.equals("LSUR")) {
    		surrendNum = 2;
    	} else if (str.contains("SN")) {
    		splitLimit = Integer.parseInt(str.substring(2)) - 1;
    	} else if (str.contains("DO")) {
    		String[] temp = str.split(":");
    		llDouble = Integer.parseInt(temp[0].substring(2));
    		ulDouble = Integer.parseInt(temp[1]);
    	} else if (str.contains("CH")) {
    		charlieCount = Integer.parseInt(str.substring(2));
    	} else if (str.contains("DT")) {
    		hitNum = Integer.parseInt(str.substring(2)) - 1;
    	}
    }
    
    void applySpecialOption(String str) {
    	if (str.contains("CC")) {
    		String[] temp = str.split("/");
    		if (temp.length == 1) {
    			deck.setCutCard(Integer.parseInt(temp[0].substring(2)), 1);
    		} else {
    			deck.setCutCard(Integer.parseInt(temp[0].substring(2)), Integer.parseInt(temp[1]));
    		}
    	} else if (str.equals("BFC")) {
    		deck.burnFirst();
    	}
    }
    
    boolean moneyValid() {
    	if (minbet > money) {
    		System.out.println();
    		System.out.println("Invalid option(s).");
			System.out.println();
			return false;
    	}
    	return true;
    }
    
    boolean insuranceCheck(BlackjackHand uh, BlackjackHand dh) { // returns true if the round has completed
    	if (dh.getCard(0).getValueAsString().equals("Ace")) {
    		System.out.println();
    		System.out.println("Your cards are:");
    	    for ( int i = 0; i < uh.getCardCount(); i++ ) {
    	        System.out.println("    " + uh.getCard(i));
    	    }
    	    System.out.println("Your total is " + uh.getBlackjackValue());
    	    System.out.println();
    	    System.out.println("Dealer is showing the " + dh.getCard(0));
    	    System.out.println();
    	    if (uh.getBlackjackValue() == bestNum) {
    	    	System.out.println("You have blackjack. Would you like even money?");
    	    	System.out.print("Yes (Y) or No (N)? ");
    	    	String inp = "";
    	    	char inpChar = 'a';
    	    	while ((inpChar != 'Y') && (inpChar != 'N')) {
	                inp = scanner.nextLine();
	                if (!isSingleChar(inp)) {
	                    System.out.print("Please respond with Y or N: ");
	                } else {
	                	inp = inp.replaceAll("\\s","");
	                    inpChar = Character.toUpperCase( inp.charAt(0) );
	                    if ((inpChar != 'Y') && (inpChar != 'N')) {
	                    	System.out.print("Please respond with Y or N: ");
	                    } else if (inpChar == 'Y') {
	                    	evenMoney = true;
	                    }
	                }
    	    	}
    	    	System.out.println();
    	    	if (evenMoney) {
    	    		System.out.println("You get even money.");
    	    		System.out.println("Dealer's other card was the " + dh.getCard(1));
    	    		money += 2.0 * bet;
    	    	} else {
    	    		if (dh.getBlackjackValue() == bestNum) {
    	    			dh.blackjackHand();
    	    			System.out.println("Dealer's other card is the " + dh.getCard(1));
    	    			System.out.println("Dealer has Blackjack. You push.");
    	    			money += bet;
    	    		} else {
    	    			System.out.println("Dealer's other card is the " + dh.getCard(1));
    	    			System.out.println("You win.");
    	    			money += (2.0 * bet) + (blackjackMult * bet) - (blackjackMult * bet % 0.5);
    	    		}
    	    	}
    	    	return true;
    	    } else {
    	    	if (money < 0.50) {
    	    		return false;
    	    	}
    	    	System.out.println("Would you like insurance?");
    	    	System.out.print("Yes (Y) or No (N)? ");
    	    	String inp = "";
    	    	char inpChar = 'a';
    	    	while ((inpChar != 'Y') && (inpChar != 'N')) {
	                inp = scanner.nextLine();
	                if (!isSingleChar(inp)) {
	                    System.out.print("Please respond with Y or N? ");
	                } else {
	                	inp = inp.replaceAll("\\s","");
	                    inpChar = Character.toUpperCase( inp.charAt(0) );
	                    if ((inpChar != 'Y') && (inpChar != 'N')) {
	                    	System.out.print("Please respond with Y or N? ");
	                    } else if (inpChar == 'Y') {
	                    	insured = true;
	                    }
	                }
    	    	}
    	    	System.out.println();
    	    	if (insured) {
    	    		double maxInsurance = bet / 2.0;
    	    		if (maxInsurance % 0.5 != 0.0) {
    	    			maxInsurance -= 0.25;
    	    		}
    	    		if (maxInsurance > money) {
    	    			maxInsurance = money;
    	    		}
    	    		System.out.println("How much insurance would you like? Enter between 0.50 and " + maxInsurance + ".");
    	    		System.out.print("? ");
    	    		double ins = maxInsurance + 1;
    	    		do {
    	                inp = scanner.nextLine();
    	                if (!isValidIns(inp)) {
    	                    System.out.print("Please enter between 0.50 and " + maxInsurance + " (50 cent increments only): ");
    	                } else {
    	                	inp = inp.replaceAll("\\s","");
    	                    ins = Double.parseDouble(inp);
    	                    if (ins > maxInsurance) {
    	                    	System.out.print("Please enter between 0.50 and " + maxInsurance + " (50 cent increments only): ");
    	                    }
    	                }
        	    	} while (!isValidIns(inp) || ins > maxInsurance);
    	    		System.out.println();
    	    		if (dh.getBlackjackValue() == bestNum) {
    	    			System.out.println("Dealer's other card is the " + dh.getCard(1));
    	    			System.out.println("Dealer has Blackjack. You win your insurance bet.");
    	    			money += 2.0 * ins;
    	    			return true;
    	    		} else {
    	    			money -= ins;
    	    			System.out.println("The dealer does not have blackjack. You lose your insurance bet.");
    	    			return false;
    	    		}
    	    	} else {
    	    		if (dh.getBlackjackValue() == bestNum) {
    	    			System.out.println("Dealer's other card is the " + dh.getCard(1));
    	    			System.out.println("Dealer has Blackjack. Dealer wins.");
    	    			return true;
    	    		} else {
    	    			System.out.println("The dealer does not have blackjack.");
    	    			return false;
    	    		}
    	    	}
    	    }
    	    
        }
    	return false;
    }
    
/*** End methods for playing blackjack. ***/
    
    
/*** All the methods for simulating blackjack are in this section. ***/
    
    void simulateBlackjack() { // optional: simInsuranceCheck
    	double moneyWon;
    	double moneyLost;
    	double totalMoneyPlayed;
    	double favor;
    	double moneyPlayedOverall;
    	double netMoney;
    	Strategy strat = new Strategy();
    	
    	while (true) {
	    	simResetDefaults();
	    	simAskAndSetOptions();
	    	if (options.get(0).equals("Q")) {
	    		System.out.println();
	    		System.out.println("You left the blackjack simulator.");
	    		break;
	    	}
	    	strat.setStrategy(deck.getNumDecks(), !options.contains("NHS"), hitNum, surrendNum, charlieCount, betStratOpt);
	    	strategyCheck(strat);
	    	favor = 0.0;
	    	moneyPlayedOverall = 0.0;
	    	netMoney = 0.0;
	    	// percentComplete = 0; // comment out when done testing
	    	System.out.println();
	    	System.out.println("Simulating...");
	    	
	    	for (int i = 0; i < simSittings; i++) {
	    		moneyWon = 0.0;
	    		moneyLost = 0.0;
	    		totalMoneyPlayed = 0.0;
		    	for (int j = 0; j < simRounds; j++) {
		    		
		    		// percentCheck(i, simSittings, j, simRounds); // comment out when done testing
		    		
		    		bet = strat.getTrueCountBet(baseBet, deck.getTrueCount());
		    	
			    	BlackjackHand dealerHand;   // The dealer's hand.
			        BlackjackHand userHand;     // The user's hand.
			        ArrayList<BlackjackHand> gameHands; // All the hands in the game. Dealer's will be added in last before return.
			        
			        // deck = new Deck(); // Currently a new deck for each game
			        dealerHand = new BlackjackHand();
			        userHand = new BlackjackHand();
			        gameHands = new ArrayList<BlackjackHand>();
			        gameHands.add(userHand);
			        
			        evenMoney = false; // setting default insurance constraints
			        insured = false;
			        int handsPlayed = 0; // For multiple hands due to splits
			        int handsBusted = 0; // To deal with split situations
			        didSplit = false; // Set true if a split has occurred
			        splitCount = 0; // user has yet to split
			        // didSplitAces = false; // Set true if Aces have been split
			    
			        /*  Signify new round to deck, and deal two cards to each player. */
			        deck.newRound();
			        userHand.addCard( deck.dealCard() );
			        dealerHand.addCard( deck.dealCard() );
			        userHand.addCard( deck.dealCard() );
			        dealerHand.addCard( deck.dealCard() );
			        
			        /* Check for early surrender. */
			        if (canEarlySurrender()) {
			        	String surMove = strat.getMove(userHand, dealerHand, simCanSplit(userHand));
			        	if (surMove.equals("Rh") || surMove.equals("Rs") || surMove.equals("Rp")) {
			        		moneyLost += bet / 2.0;
			        		totalMoneyPlayed += bet;
		            		continue;
			        	}
			        }
		        	
		//	        if (simInsuranceCheck(userHand, dealerHand)) { // not needed in basic strategy, because insurance is never taken
		//	        	continue;
		//	        }
		
			        /* Check if one of the players has Blackjack (two cards totaling to 21).
			         The player with Blackjack wins the game.
			        */
			        if (dealerHand.getBlackjackValue() == bestNum && userHand.getBlackjackValue() == bestNum) {
			            // PUSH
			        	// userHand.setOutcome(PUSH);
			        	// userWinnings += bet / 2.0; // can comment out
			        	// dealerWinnings += bet / 2.0; // can comment out
			        	totalMoneyPlayed += bet;
			            continue;
			        }
			        if (dealerHand.getBlackjackValue() == bestNum) {
			        	// LOSE
			            // userHand.setOutcome(LOSE);
			            moneyLost += bet;
			            totalMoneyPlayed += bet;
			            continue;
			        }
			        if (userHand.getBlackjackValue() == bestNum) {
			        	// WIN
			            // userHand.setOutcome(WIN);
			            moneyWon += bet * blackjackMult;
			            totalMoneyPlayed += bet; // should blackjackMult apply?
			            continue;
			        }
			      
			        /*  If neither player has Blackjack, play the game.  First the user 
			          gets a chance to draw cards (i.e., to "Hit").  The while loop ends 
			          when the user chooses to "Stand".  If the user goes over 21,
			          the user loses immediately.
			        */
			        
			        while (gameHands.size() > handsPlayed) {
			        	userHand = gameHands.get(handsPlayed);
				        while (true) {
				        	
				        	if (userHand.getCardCount() == charlieCount) {
				        		userHand.charlied();
			                    userHand.setOutcome(WIN);
			                    break;
				        	}
				        	
				            /* Display user's cards, and let user decide to Hit, Stand, Double, or Split. */
				        	
				        	if (userHand.getCardCount() == 1) { // split situation
				        		Card newCard = deck.dealCard();
				                userHand.addCard(newCard);
				                if (userHand.getCard(0).getValueAsString().equals("Ace")) { // only one card after splitting aces default
				                	if (!options.contains("DSA") && !options.contains("RSA")) {
				                		break;
				                	} else if (options.contains("DSA") && !options.contains("RSA")) {
				                		splitCount = 100; // kludge
				                	} else if (!options.contains("DSA") && options.contains("RSA")) {
				                		if (simCanSplit(userHand)) { // dealt another ace
				                			String tempAceMove = strat.getMove(userHand, dealerHand, true);
				                			boolean tempSplit = false;
				                			if (tempAceMove.equals("P")) {
				                				tempSplit = true;
				                			} else if (tempAceMove.equals("Ph") || tempAceMove.equals("Pd") || tempAceMove.equals("Ps")) {
				                				if (options.contains("DAS")) {
				                					tempSplit = true;
				                				}
				                			} else if (tempAceMove.equals("Rp")) {
				                				if (!canLateSurrender(userHand)) { // should always be true anyway
				                					tempSplit = true;
				                				}
				                			} else {
				                				break;
				                			}
				                			if (tempSplit) {
				                				didSplit = true;
								            	splitCount += 1;
								            	BlackjackHand newHand = new BlackjackHand();
								            	gameHands.add(newHand);
								            	newHand.addCard(userHand.getCard(1));
								            	userHand.removeCard(1);
								            	continue;
				                			} else {
				                				break;
				                			}
				                		} else {
				                			break;
				                		}
				                	}
				                }
				        	}
	
				        	/* Get the optimal move given the situation. */
				        	String move = strat.getMove(userHand, dealerHand, simCanSplit(userHand));
				
				        	/* "H" hit; "S" stand; "Dh" double if allowed, otherwise hit; "Ds" double if allowed, otherwise stand */
				        	/* "P" split; "Ph" split if double after split is allowed, otherwise hit */
				        	/* "Rh" surrender if allowed, otherwise hit; "Rs" surrender if allowed, otherwise stand; "Rp" surrender if allowed, otherwise split */
				            if (move.equals("H")) {
				            	Card newCard = deck.dealCard();
				                userHand.addCard(newCard);
				                if (userHand.getBlackjackValue() > bestNum) {
				                    userHand.busted();
				                    userHand.setOutcome(LOSE);
				                    handsBusted += 1;
				                    break;
				                }
				            } else if (move.equals("S")) {
				            	break;
				            } else if (move.equals("Dh")) {
				            	if (simCanDouble(userHand)) {
					            	userHand.doubled();
					            	Card newCard = deck.dealCard();
					                userHand.addCard(newCard);
					                if (userHand.getBlackjackValue() > bestNum) {
					                    userHand.busted();
					                    userHand.setOutcome(LOSE);
					                    handsBusted += 1;
					                }
					                break;
				            	} else {
				            		Card newCard = deck.dealCard();
					                userHand.addCard(newCard);
					                if (userHand.getBlackjackValue() > bestNum) {
					                    userHand.busted();
					                    userHand.setOutcome(LOSE);
					                    handsBusted += 1;
					                    break;
					                }
				            	}
				            } else if (move.equals("Ds")) {
				            	if (simCanDouble(userHand)) {
					            	userHand.doubled();
					            	Card newCard = deck.dealCard();
					                userHand.addCard(newCard);
					                if (userHand.getBlackjackValue() > bestNum) {
					                    userHand.busted();
					                    userHand.setOutcome(LOSE);
					                    handsBusted += 1;
					                }
					                break;
				            	} else {
				            		break;
				            	}
				            } else if (move.equals("P")) {
				            	didSplit = true;
				            	splitCount += 1;
				            	// if (userHand.getCard(0).getValueAsString().equals("Ace")) {
				            	//  	didSplitAces = true;
				            	// }
				            	BlackjackHand newHand = new BlackjackHand();
				            	gameHands.add(newHand);
				            	newHand.addCard(userHand.getCard(1));
				            	userHand.removeCard(1);
				            } else if (move.equals("Ph")) {
				            	if (options.contains("DAS")) {
				            		didSplit = true;
				            		splitCount += 1;
					            	// if (userHand.getCard(0).getValueAsString().equals("Ace")) {
					            	// 	didSplitAces = true;
					            	// }
					            	BlackjackHand newHand = new BlackjackHand();
					            	gameHands.add(newHand);
					            	newHand.addCard(userHand.getCard(1));
					            	userHand.removeCard(1);
				            	} else {
				            		Card newCard = deck.dealCard();
					                userHand.addCard(newCard);
					                if (userHand.getBlackjackValue() > bestNum) {
					                    userHand.busted();
					                    userHand.setOutcome(LOSE);
					                    handsBusted += 1;
					                    break;
					                }
				            	}
				            } else if (move.equals("Pd")) {
				            	if (options.contains("DAS")) {
				            		didSplit = true;
				            		splitCount += 1;
					            	// if (userHand.getCard(0).getValueAsString().equals("Ace")) {
					            	// 	didSplitAces = true;
					            	// }
					            	BlackjackHand newHand = new BlackjackHand();
					            	gameHands.add(newHand);
					            	newHand.addCard(userHand.getCard(1));
					            	userHand.removeCard(1);
				            	} else {
				            		if (simCanDouble(userHand)) {
						            	userHand.doubled();
						            	Card newCard = deck.dealCard();
						                userHand.addCard(newCard);
						                if (userHand.getBlackjackValue() > bestNum) {
						                    userHand.busted();
						                    userHand.setOutcome(LOSE);
						                    handsBusted += 1;
						                }
						                break;
					            	} else {
					            		Card newCard = deck.dealCard();
						                userHand.addCard(newCard);
						                if (userHand.getBlackjackValue() > bestNum) {
						                    userHand.busted();
						                    userHand.setOutcome(LOSE);
						                    handsBusted += 1;
						                    break;
						                }
					            	}
				            	}
				            } else if (move.equals("Ps")) {
				            	if (options.contains("DAS")) {
				            		didSplit = true;
				            		splitCount += 1;
					            	// if (userHand.getCard(0).getValueAsString().equals("Ace")) {
					            	// 	didSplitAces = true;
					            	// }
					            	BlackjackHand newHand = new BlackjackHand();
					            	gameHands.add(newHand);
					            	newHand.addCard(userHand.getCard(1));
					            	userHand.removeCard(1);
				            	} else {
				            		break;
				            	}
				            } else if (move.equals("Rh")) {
				            	if (canLateSurrender(userHand)) {
				            		userHand.setOutcome(SURRENDER);
				            		break;
				            	}  else {
					            	Card newCard = deck.dealCard();
					                userHand.addCard(newCard);
					                if (userHand.getBlackjackValue() > bestNum) {
					                    userHand.busted();
					                    userHand.setOutcome(LOSE);
					                    handsBusted += 1;
					                    break;
					                }
				            	}
				            } else if (move.equals("Rs")) {
				            	if (canLateSurrender(userHand)) {
				            		userHand.setOutcome(SURRENDER);
				            	}
				            	break;
				            } else if (move.equals("Rp")) {
				            	if (canLateSurrender(userHand)) {
				            		userHand.setOutcome(SURRENDER);
				            		break;
				            	} else {
					            	didSplit = true;
					            	splitCount += 1;
					            	// if (userHand.getCard(0).getValueAsString().equals("Ace")) {
					            	//  	didSplitAces = true;
					            	// }
					            	BlackjackHand newHand = new BlackjackHand();
					            	gameHands.add(newHand);
					            	newHand.addCard(userHand.getCard(1));
					            	userHand.removeCard(1);
				            	}
				            } else {
				            	System.out.println("Invalid move.");
				            }
				           
				        } // end while loop
				        handsPlayed += 1;
			        } // end second while loop
			      	        
			        // Check to see if all user hands were finished
			        boolean finished = true;
			        for (BlackjackHand b: gameHands) {
			        	if (b.getOutcome() == UNDECIDED) {
			        		finished = false;
			        	}
			        }	        
			        
			        /* If we get to this point, the user has Stood with 21 or less.  Now, it's
			         the dealer's chance to draw.  Dealer draws cards until the dealer's
			         total is > 16.  If dealer goes over 21, the dealer loses.
			        */
			        if (!finished) {
				        while (dealerHand.getBlackjackValue() <= hitNum || dealerHand.softHit()) {
				            Card newCard = deck.dealCard();
				            dealerHand.addCard(newCard);
				            if (dealerHand.getBlackjackValue() > bestNum) {
				                dealerHand.busted();
				                for (BlackjackHand b: gameHands) {
				                	if (b.getOutcome() == UNDECIDED) {
				                		b.setOutcome(WIN);
				                	}
				                }
				                // gameHands.add(dealerHand);
				            }
				        }
				        for (BlackjackHand b: gameHands) {
				        	if (b.getOutcome() == UNDECIDED) {
				        		if (dealerHand.getBlackjackValue() > b.getBlackjackValue()) {
				        			b.setOutcome(LOSE);
				        		} else if (b.getBlackjackValue() > dealerHand.getBlackjackValue()) {
				        			b.setOutcome(WIN);
				        		} else {
				        			b.setOutcome(PUSH);
				        		}
				        	}
				        }
				        // gameHands.add(dealerHand);
			        }
			        
			        // complete hand and deal with user/dealer winnings
			        
			    	for (BlackjackHand b : gameHands) {
			        	if (b.getOutcome() == WIN) {
			        		moneyWon += bet * b.getNumBets();
			            } else if (b.getOutcome() == LOSE) {
			                moneyLost += bet * b.getNumBets();
			            } else if (b.getOutcome() == SURRENDER) {
			            	// surrenderCount += 1;
			            	moneyLost += bet / 2.0;
			            } else { // PUSH
			            	// either do nothing or add equals amounts of money to both user and dealer
			            	// userWinnings += (bet * b.getNumBets()) / 2.0; // can comment out
				        	// dealerWinnings += (bet * b.getNumBets()) / 2.0; // can comment out
			            }
			        	totalMoneyPlayed += bet * b.getNumBets();
			        }
	        
			    	/*  Shuffle the deck if needed. */
			        if (deck.shouldShuffle()) {
			        	deck.shuffle();
			        }
		    	}
		    	
		    	// Sitting finished. Calculate results and add to favor.
		    	
		    	// System.out.println("User winnings: " + userWinnings);
		    	// System.out.println("Dealer winnings: " + dealerWinnings);
		    	// System.out.println();
		    	
		    	/* Current formula for calculating percentage favor. */
		    	favor -= 100.0 - (((totalMoneyPlayed + (moneyWon - moneyLost)) / (totalMoneyPlayed)) * 100.0);
		    	moneyPlayedOverall += totalMoneyPlayed;
		    	netMoney += (moneyWon - moneyLost);

	    	}
	    	
	    	// All sittings finished. Calculate final results.
	    	favor = favor / simSittings;
	    	favor = (double) Math.round(favor * 100) / 100;
	    	netMoney = (double) Math.round(netMoney * 100) / 100;
	    	moneyPlayedOverall = (double) Math.round(moneyPlayedOverall * 100) / 100;
	    	strat.addStats(favor, netMoney, moneyPlayedOverall);
	    	if (favor > 0.0) {
	    		System.out.println();
	    		System.out.println("Player edge: " + favor + "%");
	    	} else {
	    		favor = favor * -1.0;
	    		System.out.println();
	    		System.out.println("House edge: " + favor + "%");
	    	}
	    	if (netMoney > 0.0) {
	    		System.out.println("Net winnings: $" + netMoney);
	    	} else {
	    		netMoney = netMoney * -1.0;
	    		System.out.println("Net loss: $" + netMoney);
	    	}
	    	System.out.println("Total money played: $" + moneyPlayedOverall);
	    	// System.out.println(surrenderCount);
    	}
    }
    
    void simResetDefaults() {
    	bet = 5; // default
    	blackjackMult = 1.5; // default
    	deck = new Deck(2); // A double deck of cards
    	bestNum = 21; // ideal number for the game
    	hitNum = 16; // highest number dealer still has to hit
    	simSittings = 100; // default number of sittings to average
    	simRounds = 100000; // default number of rounds to simulate
    	surrendNum = -1; // surrender flag
    	// splitCount = 0; // user has yet to split
    	splitLimit = 3; // may split 3 times by default
    	llDouble = 4; // lower limit for doubling
    	ulDouble = 21; // upper limit for doubling
    	charlieCount = -1; // Charlie rule not in effect by default
    	betStratOpt = ""; //  Empty to show no true count strategy by default
    	baseBet = 5.0; // default;
    }
    
    boolean simCanDouble(BlackjackHand h) {
    	if (h.getCardCount() != 2 && !options.contains("DAN")) {
    		return false;
    	} else if (didSplit && !options.contains("DAS")) {
    		return false;
    	} else if (llDouble == 0 || h.getBlackjackValue() < llDouble || h.getBlackjackValue() > ulDouble) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    boolean simCanSplit(BlackjackHand h) {
    	if (h.getCardCount() != 2) {
    		return false;
    	} else if (splitCount >= splitLimit) {
    		return false;
    	} else if (h.getCard(0).getValue() == h.getCard(1).getValue()) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    void simAskAndSetOptions() {
    	System.out.println();
        
        String opts = "0";
        do {
        	System.out.println("What simulation options would you like to use?");
        	System.out.println("Press enter for a default simulation. Type 'Q' to quit.");
        	System.out.println("Type 'H' for options help. Type 'R' to show default simulation settings.");
        	System.out.println("Type 'T' to turn the Strategy Window on and off.");
        	System.out.println("Separate your options with commas.");
        	System.out.print("? ");
        	opts = scanner.nextLine();
        } while (!simValidOptions(opts));
        
        simApplyOptions(opts); // for normal cases
        // System.out.println(options); // Comment out when done testing
    }
    
    boolean simValidOptions(String opts) { // only use bet increments of $5
    	if (opts.matches("[ \\t]*")) {
    		return true;
    	} else if (opts.matches("[ \\t]*[Qq][ \\t]*")) {
    		return true;
    	} else if (opts.matches("[ \\t]*[Hh][ \\t]*")) {
    		System.out.println();
    		System.out.println("OPTIONS:");
    		System.out.println("DAS - Double down allowed after split.");
    		System.out.println("NHS - Dealer does not hit on soft 17.");
    		System.out.println("ESUR - Early surrender allowed.");
    		System.out.println("LSUR - Late surrender allowed.");
    		System.out.println("       Note: only one surrender type allowed.");
    		System.out.println("DAN - Double on any number of cards.");
    		System.out.println("DSA - May draw to split Aces.");
    		System.out.println("RSA - May resplit Aces.");	
    		System.out.println("BFC - Burn the first card of a newly shuffled deck.");
    		System.out.println("NS[num] - Set the number of sittings to simulate. e.g. NS200.");
    		System.out.println("        - Lowest is 1 sitting, highest is 1000.");
    		System.out.println("NR[num] - Set the number of rounds to simulate per sitting. e.g. NR50000.");
    		System.out.println("        - Lowest is 1 round, highest is 100000.");
    		System.out.println("ND[num] - Set the number of decks to simulate with. e.g. ND2.");
    		System.out.println("          Lowest is 1 deck, highest is 8 decks.");
    		System.out.println("BB[num] - Set the base bet to an amount between $1 and $100000. e.g. BB50.");
    		System.out.println("BP[num]:[num] - Set the blackjack payout to a different ratio. e.g. BP6:5");
    		System.out.println("          BP6:5 means the payout is 6 to 5 on a blackjack");
    		System.out.println("DO[num]:[num] - Set the lower and upper limits of hand totals that can be doubled. e.g. DO5:11");
    		System.out.println("          DO5:11 means you may only double on hand totals between 5 and 11.");
    		System.out.println("          Lowest limit is 4 and highest limit is 21. Use DO0:0 to disallow doubling.");
    		System.out.println("CC[num]/[num] - Pick the fractional placement of the cut card in the deck. e.g. CC1/2.");
    		System.out.println("          CC1/2 means going through half the cards in the deck(s) before reshuffling.");
    		System.out.println("          Fraction must be between 0 and 1.");
    		System.out.println("SN[num] - Set the number of hands you can split to. e.g. SN3.");
    		System.out.println("          Lowest is 1 hand (no splitting allowed) and highest is 5 hands.");
    		System.out.println("CH[num] - Include the automatic winner Charlie rule and set the winning value. e.g. CH6");
    		System.out.println("          CH6 means the user automatically wins if there are 6 cards in their hand without busting.");
    		System.out.println("          Lowest is 3 cards and highest is 10 cards. Doubling negates the Charlie rule.");
    		System.out.println("DT[num] - The dealer draws cards until the hand total is 'num' or more, instead of the standard 17. e.g. DT16.");
    		System.out.println("          The dealer also hits on soft totals equal to 'num', unless the 'NHS' option is also used.");
    		System.out.println("          Lowest total is 4 and highest total is 21.");
    		System.out.println("TC[opt] - Have the simulation use a true count betting strategy. e.g. TCBASIC");
    		System.out.println("          TCBASIC for a basic strategy, TCMIT for an MIT strategy,");
    		System.out.println("          TCIMIT for an improved MIT strategy, TCRILLA for a guerrilla betting strategy.");
    		System.out.println();
    		return false;
    	} else if (opts.matches("[ \\t]*[Rr][ \\t]*")) {
    		System.out.println();
    		System.out.println("DEFAULT SIMULATION:");
    		System.out.println("100 sittings of blackjack are averaged.");
    		System.out.println("100000 rounds of blackjack are simulated per sitting.");
    		System.out.println("Base bet is $5.");
    		System.out.println("Double deck is used and reshuffled when over half the cards have been dealt.");
    		System.out.println("Blackjack pays 3 to 2.");
    		System.out.println("Dealer hits on soft 17.");
    		System.out.println("No double downs after splits.");
    		System.out.println("Only one card is dealt to split Aces, and Aces cannot be resplit.");
    		System.out.println("User can only split to 4 hands.");
    		System.out.println("Surrendering is not allowed.");	
    		System.out.println("Insurance is in effect.");
    		System.out.println();
    		return false;
    	} else if (opts.matches("[ \\t]*[Tt][ \\t]*")) {
    		System.out.println();
    		if (!stratWindow) {
    			stratWindow = true;
    			System.out.println("The Strategy Window has been turned on.");
    			System.out.println("This window will be prompted after your options are set.");
    			System.out.println("You will have the ability to change how the computer plays.");
    		} else {
    			stratWindow = false;
    			System.out.println("The Strategy Window has been turned off.");
    		}
    		System.out.println();
    		return false;
    	}
    	ArrayList<String> temp = new ArrayList<String>(Arrays.asList(opts.replaceAll("\\s","").split(",")));
    	for (String str : temp) {
    		if (!simValidOption(str.toUpperCase())) {
    			System.out.println();
    			System.out.println("Invalid option(s).");
    			System.out.println();
    			return false;
    		}
    	}
    	return true;
    }
    
    boolean simValidOption(String str) {
    	if (availableOptions.contains(str) || str.matches("(NS[1-9]\\d?\\d?)|(1000)")
    			|| str.matches("(NR[1-9]\\d?\\d?\\d?\\d?)|(100000)") || str.matches("ND[1-8]")) {
    		return true;
    	} else if (str.matches("(BP0)|(BP[1-9]\\d?\\d?:[1-9]\\d?\\d?)|(BP0:[1-9]\\d?\\d?)")) {
    		return true;
    	} else if (str.matches("(CC[1-9]\\d?\\d?/[1-9]\\d?\\d?)|(CC0/[1-9]\\d?\\d?)|(CC0)|(CC1)")) {
    		String[] temp = str.split("/");
    		if (temp.length == 1) {
    			return true;
    		} else {
    			return (Integer.parseInt(temp[0].substring(2)) <= Integer.parseInt(temp[1]));
    		}
    	} else if (str.matches("SN[1-5]")) {
    		return true;
    	} else if (str.matches("DO[1-2]?[0-9]:[1-2]?[0-9]")) {
    		String[] temp = str.split(":");
    		int temp1 = Integer.parseInt(temp[0].substring(2));
    		int temp2 = Integer.parseInt(temp[1]);
    		if (temp1 == 0 && temp2 == 0) {
    			return true;
    		} else if (temp1 >= 4 && temp1 <= 21 && temp2 >= 4 && temp2 <= 21 && temp1 <= temp2) {
    			return true;
    		} else {
    			return false;
    		}
    	} else if (str.matches("CH([3-9]|10)")) {
    		return true;
    	} else if (str.matches("TC(BASIC|MIT|IMIT|RILLA)")) {
    		return true;
    	} else if (str.matches("DT[1-2]?[0-9]")) {
    		int temp1 = Integer.parseInt(str.substring(2));
    		if (temp1 >= 4 && temp1 <= 21) {
    			return true;
    		} else {
    			return false;
    		}
    	} else if (str.matches("BB([1-9]\\d?\\d?\\d?\\d?|100000)")) {
    		return true;
    	}
    	return false;
    }
    
    void simApplyOptions(String opts) {
    	options = new ArrayList<String>();
    	ArrayList<String> temp = new ArrayList<String>(Arrays.asList(opts.replaceAll("\\s","").split(",")));
    	for (String str : temp) {
    		simApplyOption(str.toUpperCase());
    		options.add(str.toUpperCase());
    	}
    	// now for special cases
    	for (String str : temp) {
    		simApplySpecialOption(str.toUpperCase());
    	}
    }
    
    void simApplyOption(String str) {
    	if (str.contains("BP")) {
    		String[] temp = str.split(":");
    		if (temp.length == 1) {
    			blackjackMult = 0.0;
    		} else {
    			blackjackMult = (1.0 * Integer.parseInt(temp[0].substring(2))) / (1.0 * Integer.parseInt(temp[1]));
    		}
    	} else if (str.contains("NS")) {
    		simSittings = Integer.parseInt(str.substring(2));
    	} else if (str.contains("NR")) {
    		simRounds = Integer.parseInt(str.substring(2));
    	} else if (str.contains("ND")) {
    		deck = new Deck(Integer.parseInt(str.substring(2)));
    	} else if (str.equals("ESUR")) {
    		surrendNum = 1;
    	} else if (str.equals("LSUR")) {
    		surrendNum = 2;
    	} else if (str.contains("SN")) {
    		splitLimit = Integer.parseInt(str.substring(2)) - 1;
    	} else if (str.contains("DO")) {
    		String[] temp = str.split(":");
    		llDouble = Integer.parseInt(temp[0].substring(2));
    		ulDouble = Integer.parseInt(temp[1]);
    	} else if (str.contains("CH")) {
    		charlieCount = Integer.parseInt(str.substring(2));
    	} else if (str.contains("TC")) {
    		betStratOpt = str.substring(2);
    	} else if (str.contains("DT")) {
    		hitNum = Integer.parseInt(str.substring(2)) - 1;
    	} else if (str.contains("BB")) {
    		baseBet = Integer.parseInt(str.substring(2));
    	}
    }
    
    void simApplySpecialOption(String str) {
    	if (str.contains("CC")) {
    		String[] temp = str.split("/");
    		if (temp.length == 1) {
    			deck.setCutCard(Integer.parseInt(temp[0].substring(2)), 1);
    		} else {
    			deck.setCutCard(Integer.parseInt(temp[0].substring(2)), Integer.parseInt(temp[1]));
    		}
    	} else if (str.equals("BFC")) {
    		deck.burnFirst();
    	}
    }
    
    void strategyCheck(Strategy s) {
    	if (stratWindow) {
    		s.promptStrategyWindow();
    	}
    	s.addStrats();
    }
    
    // not completed
    boolean simInsuranceCheck(BlackjackHand uh, BlackjackHand dh) { // returns true if the round has completed 
    	return false;
    }

    
/*** End methods for simulating blackjack. ***/
    
    
/*** All multi-purpose methods are in this section. ***/
    
    void setDefaults() {
    	minbet = 5; // default
    	blackjackMult = 1.5; // default
    	money = 1000;  // User starts with $1000.
    	deck = new Deck(2); // A double deck of cards
    	bestNum = 21; // ideal number for the game
    	hitNum = 16; // highest number dealer still has to hit
    	surrendNum = -1; // surrender flag
    	splitCount = 0; // user has yet to split
    	splitLimit = 3; // may split 3 times by default
    	llDouble = 4; // lower limit for doubling
    	ulDouble = 21; // upper limit for doubling
    	charlieCount = -1; // Charlie rule not in effect by default
    	betStratOpt = ""; //  Empty to show no true count strategy by default
    	stratWindow = false;
    }
    
    void setAvailableOptions() {
    	availableOptions = new ArrayList<String>();
    	availableOptions.add("DAS"); // double after split
    	availableOptions.add("NHS"); // no soft hit for dealer
    	availableOptions.add("ESUR"); // early surrender allowed 
    	availableOptions.add("LSUR"); // late surrender allowed 
    	availableOptions.add("DAN"); // double on any number of cards
    	availableOptions.add("DSA"); // may draw to split aces
    	availableOptions.add("RSA"); // resplitting aces allowed
    	availableOptions.add("BFC"); // burn first card of newly shuffled deck
    }
    
    boolean isNumeric(String s) {
        return s.matches("[ \\t]*[-+]?\\d*\\.?\\d+[ \\t]*");  
    }  
    
    boolean isValidBet(String s) { // 50 cent increments from 0 to infinity
        return s.matches("([ \\t]*\\d+(\\.[50]0?)?[ \\t]*)|([ \\t]*\\d*(\\.[50]0?)[ \\t]*)");  
    }  
    
    boolean isValidIns(String s) { // 50 cent increments from .50 to infinity
        return s.matches("([ \\t]*[1-9]\\d*(\\.[50]0?)?[ \\t]*)|([ \\t]*\\d*(\\.50?)[ \\t]*)");  
    }  

    boolean isNumericInt(String s) {  
        return s.matches("[ \\t]*\\d+[ \\t]*");  
    }  

    boolean isSingleChar(String s) {
       	return s.matches("[ \\t]*[A-Za-z][ \\t]*"); 
    }
    
    boolean canEarlySurrender() {
    	return (surrendNum == 1);
    }
    
    boolean canLateSurrender(Hand h) {
    	return ((surrendNum == 2) && h.getCardCount() == 2 && !didSplit);
    }
    
    void percentCheck(int sc, int st, int rc, int rt) { // sc: sittings completed, st: sittings total, rc: rounds completed, rt: rounds total
    	double temp1 = 1.0 * sc;
    	double temp2 = 1.0 * st;
    	double temp3 = 1.0 * rc;
    	double temp4 = 1.0 * rt;
    	double temp5 = (temp1 * temp4) + temp3;
    	double temp6 = temp2 * temp4;
    	double res = (temp5 * 100) / temp6;
    	if (Math.floor(res) > percentComplete) {
    		percentComplete += 1;
    		System.out.println("" + percentComplete + "% complete.");
    	}
    }
    
    public Scanner getScanner() {
    	return scanner;
    }
    
/*** End multi-purpose methods. ***/

} // end class Blackjack