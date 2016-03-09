package Blackjack;

import static org.junit.Assert.*;

import org.junit.Test;

//import java.lang.reflect.Field;
//import java.lang.reflect.Method;

public class CardTests {

	@Test
	public void getSuitGetValueTest() {
		Card[] cards = new Card[52];
		for ( int suit = 0; suit <= 3; suit++ ) {
			for ( int value = 1; value <= 13; value++ ) {
				cards[(suit * 13) + (value - 1)] = new Card(value, suit);
		    }
		}
		int sval;
		int vval;
		for ( int s = 0; s <= 3; s++ ) {
			for ( int v = 1; v <= 13; v++ ) {
				sval = cards[(s * 13) + (v - 1)].getSuit();
				vval = cards[(s * 13) + (v - 1)].getValue();
				assertEquals(s, sval);
				assertEquals(v, vval);
		    }
		}
		// fail("Not yet implemented");
	}
	
	@Test
	public void getSuitAsStringTest() {
		String[] res = {new Card(1,0).getSuitAsString(),new Card(1,1).getSuitAsString(),
				new Card(1,2).getSuitAsString(),new Card(1,3).getSuitAsString(),new Card(1,4).getSuitAsString()};
		String[] exp = {"Spades","Hearts","Diamonds","Clubs","??"};
		assertArrayEquals(exp, res);
		res[0] = new Card(1, Card.SPADES).getSuitAsString();
		res[1] = new Card(1, Card.HEARTS).getSuitAsString();
		res[2] = new Card(1, Card.DIAMONDS).getSuitAsString();
		res[3] = new Card(1, Card.CLUBS).getSuitAsString();
		assertArrayEquals(exp, res);
	}
	
	@Test
	public void getValueAsStringTest() {
		String[] res = {new Card(1,0).getValueAsString(),new Card(2,0).getValueAsString(),new Card(3,0).getValueAsString()
				,new Card(4,0).getValueAsString(),new Card(5,0).getValueAsString(),new Card(6,0).getValueAsString()
				,new Card(7,0).getValueAsString(),new Card(8,0).getValueAsString(),new Card(9,0).getValueAsString()
				,new Card(10,0).getValueAsString(),new Card(11,0).getValueAsString(),new Card(12,0).getValueAsString()
				,new Card(13,0).getValueAsString(),new Card(0,0).getValueAsString(),new Card(14,0).getValueAsString()};
		String[] exp = {"Ace","2","3","4","5","6","7","8","9","10","Jack","Queen","King","??","??"};
		assertArrayEquals(exp, res);
		res[0] = new Card(Card.ACE, 0).getValueAsString();
		res[10] = new Card(Card.JACK, 0).getValueAsString();
		res[11] = new Card(Card.QUEEN, 0).getValueAsString();
		res[12] = new Card(Card.KING, 0).getValueAsString();
		assertArrayEquals(exp, res);
	}
	
	@Test
	public void toStringTest() {
		String[] res = {new Card(Card.ACE,0).toString(),new Card(Card.QUEEN,2).toString(),
				new Card(8,1).toString(),new Card(5,3).toString(),new Card(0,4).toString()};
		String[] exp = {"Ace of Spades","Queen of Diamonds","8 of Hearts","5 of Clubs","?? of ??"};
		assertArrayEquals(exp, res);
	}

}
