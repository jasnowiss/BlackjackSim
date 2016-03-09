package Blackjack;

import static org.junit.Assert.*;

import org.junit.Test;

//import java.lang.reflect.Field;
//import java.lang.reflect.Method;

public class HandTests {

	@Test
	public void addRemoveGetCardGetCountTest() {
		Hand h = new Hand();
		Card c1 = new Card(1,0);
		Card c2 = new Card(8,2);
		h.addCard(c1);
		h.addCard(c2);
		assertEquals(2, h.getCardCount());
		assertEquals(c2, h.getCard(1));
		h.removeCard(c1);
		assertEquals(1, h.getCardCount());
		assertEquals(c2, h.getCard(0));
		h.removeCard(0);
		assertEquals(0, h.getCardCount());
		// fail("Not yet implemented");
	}
	
	@Test
	public void sortBySuitTest() {
		Hand h = new Hand();
		Card c1 = new Card(2,3);
		Card c2 = new Card(8,2);
		Card c3 = new Card(1,3);
		Card c4 = new Card(4,2);
		h.addCard(c1);
		h.addCard(c2);
		h.addCard(c3);
		h.addCard(c4);
		h.sortBySuit();
		Card[] res = {h.getCard(0),h.getCard(1),h.getCard(2),h.getCard(3)};
		Card[] exp = {c4,c2,c3,c1};
		assertArrayEquals(exp, res);
	}
	
	@Test
	public void sortByValueTest() {
		Hand h = new Hand();
		Card c1 = new Card(2,3);
		Card c2 = new Card(8,2);
		Card c3 = new Card(2,2);
		Card c4 = new Card(8,1);
		h.addCard(c1);
		h.addCard(c2);
		h.addCard(c3);
		h.addCard(c4);
		h.sortByValue();
		Card[] res = {h.getCard(0),h.getCard(1),h.getCard(2),h.getCard(3)};
		Card[] exp = {c3,c1,c4,c2};
		assertArrayEquals(exp, res);
	}

}
