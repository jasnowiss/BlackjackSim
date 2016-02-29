
/* 
    An object of type Deck represents an ordinary deck of 52 playing cards.
    The deck can be shuffled, and cards can be dealt from the deck.
*/

public class Deck {

    private Card[] deck;   // An array of 52 Cards, representing the deck.
    private int cardsUsed; // How many cards have been dealt from the deck.
    private int numDecks; // Number of decks used.
    private int cutCard = -1; // Number of cards before deck is reshuffled
    private int roundCards; // Number of cards used for a particular round
    private boolean discardUsed = false; // Flag that is set if the deck has run out of cards and discarded cards were used
    private int cardCount = 0; // The count of the current deck
    private boolean burnFirstCard = false; // True if playing with the option to burn the first card of a newly shuffled deck
    
    public Deck() {
	    // Create an unshuffled deck of cards, then shuffle them.
    	numDecks = 1;
	    deck = new Card[52];
	    int cardCt = 0; // How many cards have been created so far.
	    for ( int suit = 0; suit <= 3; suit++ ) {
	        for ( int value = 1; value <= 13; value++ ) {
	            deck[cardCt] = new Card(value,suit);
	            cardCt++;
	        }
	    }
	    cardsUsed = 0;
	    setDefaultCutCard();
	    shuffle();
	}
    
    public Deck(int n) {
        // Create an unshuffled deck of cards, then shuffle them. Can specify number of decks.
    	numDecks = n;
	    deck = new Card[52 * n];
	    int cardCt = 0; // How many cards have been created so far.
	    for (int i = 0; i < n; i++) {
		    for ( int suit = 0; suit <= 3; suit++ ) {
		       for ( int value = 1; value <= 13; value++ ) {
		          deck[cardCt] = new Card(value,suit);
		          cardCt++;
		       }
		    }
	    }
	    cardsUsed = 0;
	    setDefaultCutCard();
	    shuffle();
    }
    
    public void shuffle() {
    	// Put all the used cards back into the deck, and shuffle it into
        // a random order.
        for ( int i = (52 * numDecks) - 1; i > 0; i-- ) {
            int rand = (int)(Math.random()*(i+1));
            Card temp = deck[i];
            deck[i] = deck[rand];
            deck[rand] = temp;
        }
        cardsUsed = 0;
        if (burnFirstCard) {
        	cardsUsed += 1;
        }
        discardUsed = false;
        cardCount = 0;
        
    }
    
    public int cardsLeft() {
    	// As cards are dealt from the deck, the number of cards left
        // decreases.  This function returns the number of cards that
        // are still left in the deck.
        return (52 * numDecks) - cardsUsed;
    }
    
    public Card dealCard() {
        // Deals one card from the deck and returns it.
        if (cardsUsed == (52 * numDecks)) {
        	shuffleDiscard();
        }
        cardsUsed++;
        roundCards++;
        changeCount(deck[cardsUsed - 1]);
        // System.out.println("Card Count: " + getCount());
        // System.out.println("True Count: " + getTrueCount());
        return deck[cardsUsed - 1];
    }
    
    public int getNumDecks() {
    	return numDecks;
    }
    
    public boolean shouldShuffle() {
    	if (discardUsed) {
    		return true;
    	} else {
    		return (cardsUsed >= cutCard);
    	}
    }
    
    public void newRound() {
    	roundCards = 0;
    }
    
    public void shuffleDiscard() {
    	// Put all the used cards that are not in play back into the deck, and shuffle it into
        // a random order.
        for ( int i = (52 * numDecks) - (roundCards + 1); i > 0; i-- ) {
            int rand = (int)(Math.random()*(i+1));
            Card temp = deck[i];
            deck[i] = deck[rand];
            deck[rand] = temp;
        }
        cardsUsed = 0;
        if (burnFirstCard) {
        	cardsUsed += 1;
        }
        discardUsed = true;
    }
    
    public void setDefaultCutCard() {
		if (numDecks < 4) {
	    	cutCard = cardsLeft() / 2;
	    } else {
	    	cutCard = (cardsLeft() * 2) / 3;
	    }
    }
    
    public void setCutCard(int c1, int c2) { // takes in a numerator and a denominator and sets the cut card based on this fraction
    	cutCard = (cardsLeft() * c1) / c2;
    }
    
    public void changeCount(Card c) {
    	if (c.getValue() == 1 || c.getValue() > 9) {
    		cardCount -= 1;
    	} else if (c.getValue() < 7) {
    		cardCount += 1;
    	}
    }
    
    public int getCount() {
    	return cardCount;
    }
    
    public int getTrueCount() {
    	double temp1 = 1.0 * cardCount;
    	double temp2 = (1.0 * cardsLeft()) / 52.0;
    	Double temp3 = temp1 / temp2;
    	return temp3.intValue();
    }
    
    public void burnFirst() {
    	burnFirstCard = true;
    }

} // end class Deck