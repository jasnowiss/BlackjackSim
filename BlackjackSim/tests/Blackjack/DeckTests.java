package Blackjack;

import static org.junit.Assert.*;

import org.junit.Test;

//import java.lang.reflect.Field;
//import java.lang.reflect.Method;

public class DeckTests {
	
	@Test
	public void getNumDecksTest() {
		Deck d;
		d = new Deck();
		assertEquals(1, d.getNumDecks());
		d = new Deck(1);
		assertEquals(1, d.getNumDecks());
		d = new Deck(2);
		assertEquals(2, d.getNumDecks());
		d = new Deck(6);
		assertEquals(6, d.getNumDecks());
		// fail("Not yet implemented");
	}
	
	@Test
	public void cardsLeftAndDealCardAndBurnTest() {
		Deck d;
		d = new Deck();
		assertEquals(52, d.cardsLeft());
		d.dealCard();
		d.dealCard();
		assertEquals(50, d.cardsLeft());
		d.burnFirst();
		d.shuffle();
		assertEquals(51, d.cardsLeft());
		d.dealCard();
		assertEquals(50, d.cardsLeft());
		d = new Deck(2);
		assertEquals(104, d.cardsLeft());
		d.dealCard();
		d.dealCard();
		assertEquals(102, d.cardsLeft());
		d.burnFirst();
		d.shuffle();
		assertEquals(103, d.cardsLeft());
		d.dealCard();
		assertEquals(102, d.cardsLeft());
	}
	
	@Test
	public void cutCardTest() {
		Deck d;
		d = new Deck();
		assertEquals(26, d.getCutCard());
		d.setCutCard(3, 4);
		assertEquals(39, d.getCutCard());
		d = new Deck(4);
		assertEquals(138, d.getCutCard());
		d.setCutCard(1, 3);
		assertEquals(69, d.getCutCard());
	}
	
	@Test
	public void countingTestOne() {
		Deck d;
		d = new Deck(2);
		d.changeCount(new Card(1,0));
		assertEquals(-1, d.getCount());
		assertEquals(0, d.getTrueCount());
		d.changeCount(new Card(10,0));
		assertEquals(-2, d.getCount());
		assertEquals(-1, d.getTrueCount());
		d.changeCount(new Card(2,0));
		assertEquals(-1, d.getCount());
		assertEquals(0, d.getTrueCount());
		d.changeCount(new Card(7,0));
		assertEquals(-1, d.getCount());
		assertEquals(0, d.getTrueCount());
	}
	
	@Test
	public void countingTestTwo() {
		Deck d;
		d = new Deck();
		for (int i = 0; i < 52; i++) {
			d.dealCard();
		}
		assertEquals(0, d.getCount());
		assertEquals(0, d.getTrueCount());
		d = new Deck(4);
		for (int i = 0; i < 52*4; i++) {
			d.dealCard();
		}
		assertEquals(0, d.getCount());
		assertEquals(0, d.getTrueCount());
	}

}
