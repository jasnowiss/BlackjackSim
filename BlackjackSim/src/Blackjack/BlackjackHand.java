/* 
   A subclass of the Hand class that represents a hand of cards
   in the game of Blackjack.  To the methods inherited form Hand,
   it adds the method getBlackjackHand(), which returns the value
   of the hand for the game of Blackjack. 
*/

package Blackjack;

public class BlackjackHand extends Hand {
	
    private boolean ace11 = false;
 
     public int getBlackjackValue() {
            // Returns the value of this hand for the
            // game of Blackjack.
    	 
    	 ace11 = false;

         int val;      // The value computed for the hand.
         boolean ace;  // This will be set to true if the
                       //   hand contains an ace.
         int cards;    // Number of cards in the hand.

         val = 0;
         ace = false;
         cards = getCardCount();

         for ( int i = 0;  i < cards;  i++ ) {
                 // Add the value of the i-th card in the hand.
             Card card;    // The i-th card; 
             int cardVal;  // The blackjack value of the i-th card.
             card = getCard(i);
             cardVal = card.getValue();  // The normal value, 1 to 13.
             if (cardVal > 10) {
                 cardVal = 10;   // For a Jack, Queen, or King.
             }
             if (cardVal == 1) {
                 ace = true;     // There is at least one ace.
             }
             val = val + cardVal;
          }

             // Now, val is the value of the hand, counting any ace as 1.
             // If there is an ace, and if changing its value from 1 to 
             // 11 would leave the score less than or equal to 21,
             // then do so by adding the extra 10 points to val. 

          if ( ace == true  &&  val + 10 <= Blackjack.b.bestNum ) {
              val = val + 10;
              ace11 = true;
          }

          return val;

     }  // end getBlackjackValue()
     
     public boolean isAce11() { // getBlackjackValue() must be called directly before calling this method. kludge
    	 return ace11;
     }
     
     public boolean softHit() { // check if hand is a soft 17 or soft hit number depending on the situation
    	 if (Blackjack.b.options.contains("NHS")) {
    		 return false;
    	 }
    	 
    	 int val;      // The value computed for the hand.
         boolean ace;  // This will be set to true if the
                       //   hand contains an ace.
         int cards;    // Number of cards in the hand.

         val = 0;
         ace = false;
         cards = getCardCount();

         for ( int i = 0;  i < cards;  i++ ) {
                 // Add the value of the i-th card in the hand.
             Card card;    // The i-th card; 
             int cardVal;  // The blackjack value of the i-th card.
             card = getCard(i);
             cardVal = card.getValue();  // The normal value, 1 to 13.
             if (cardVal > 10) {
                 cardVal = 10;   // For a Jack, Queen, or King.
             }
             if (cardVal == 1) {
                 ace = true;     // There is at least one ace.
             }
             val = val + cardVal;
          }

             // Now, val is the value of the hand, counting any ace as 1.
             // If there is an ace, and if changing its value from 1 to 
             // 11 would leave the score less than or equal to 21,
             // then do so by adding the extra 10 points to val. 

          if ( ace == true  &&  val + 10 <= Blackjack.b.bestNum ) {
              val = val + 10;
              return (val == (Blackjack.b.hitNum + 1));
          }
          return false;
     }
     
     public int getDealerValue() { // applies to dealer's first card only. returns blackjack value
         int val = getCard(0).getValue();
    	 switch ( val ) {
 	        case 1:   return 11;
 	        case 11:  return 10;
 	        case 12:  return 10;
 	        case 13:  return 10;
 	        default:  return val;
         }
     }

 
} // end class BlackjackHand
