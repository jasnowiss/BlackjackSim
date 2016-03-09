package Blackjack;

import static org.junit.Assert.*;

import org.junit.Test;

//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
import java.util.ArrayList;

public class BlackjackHandTests {
	
	BlackjackHand bh;
	Deck d;
//	HelperMethods h = new HelperMethods();

	@Test
	public void getBlackjackValueTest() {
		Blackjack.b = new Blackjack();
		bh = new BlackjackHand();
		bh.addCard(new Card(1,0));
		bh.addCard(new Card(10,0));
		assertEquals(21, bh.getBlackjackValue());
		bh.addCard(new Card(1,1));
		assertEquals(12, bh.getBlackjackValue());
		bh = new BlackjackHand();
		bh.addCard(new Card(8,0));
		bh.addCard(new Card(11,0));
		assertEquals(18, bh.getBlackjackValue());
		// fail("Not yet implemented");
	}
	
	@Test
	public void isAce11Test() {
		Blackjack.b = new Blackjack();
		bh = new BlackjackHand();
		bh.addCard(new Card(1,0));
		bh.addCard(new Card(10,0));
		bh.getBlackjackValue();
		assertTrue(bh.isAce11());
		bh.addCard(new Card(1,1));
		bh.getBlackjackValue();
		assertFalse(bh.isAce11());
		bh = new BlackjackHand();
		bh.addCard(new Card(8,0));
		bh.addCard(new Card(11,0));
		bh.getBlackjackValue();
		assertFalse(bh.isAce11());
	}
	
	@Test
	public void softHitTest() {
		Blackjack.b = new Blackjack();
		Blackjack.b.options = new ArrayList<String>();
		bh = new BlackjackHand();
		bh.addCard(new Card(1,0));
		bh.addCard(new Card(6,0));
		assertTrue(bh.softHit());
		bh = new BlackjackHand();
		bh.addCard(new Card(1,0));
		bh.addCard(new Card(2,0));
		bh.addCard(new Card(4,0));
		assertTrue(bh.softHit());
		bh = new BlackjackHand();
		bh.addCard(new Card(1,0));
		bh.addCard(new Card(5,0));
		assertFalse(bh.softHit());
		bh = new BlackjackHand();
		bh.addCard(new Card(1,0));
		bh.addCard(new Card(7,0));
		assertFalse(bh.softHit());
		bh = new BlackjackHand();
		bh.addCard(new Card(10,0));
		bh.addCard(new Card(7,0));
		assertFalse(bh.softHit());
	}
	
	@Test
	public void getDealerValueTest() {
		Blackjack.b = new Blackjack();
		bh = new BlackjackHand();
		bh.addCard(new Card(1,0));
		bh.addCard(new Card(6,0));
		assertEquals(11, bh.getDealerValue());
		bh = new BlackjackHand();
		bh.addCard(new Card(11,0));
		bh.addCard(new Card(6,0));
		assertEquals(10, bh.getDealerValue());
		bh = new BlackjackHand();
		bh.addCard(new Card(8,0));
		bh.addCard(new Card(6,0));
		assertEquals(8, bh.getDealerValue());
	}

}
