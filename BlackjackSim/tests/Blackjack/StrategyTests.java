package Blackjack;

import static org.junit.Assert.*;

import org.junit.Test;

//import java.lang.reflect.Field;
//import java.lang.reflect.Method;

public class StrategyTests {

	@Test
	public void simpleStrategyTest() {
		Strategy s = new Strategy();
		s.setStrategy(2, true, 17, 0, -1, "");
		assertFalse(s.isOptimal());	
		s.setStrategy(2, true, 16, 0, -1, "");
		assertTrue(s.isOptimal());
		BlackjackHand uh = new BlackjackHand();
		BlackjackHand dh = new BlackjackHand();
		uh.addCard(new Card(8,0));
		dh.addCard(new Card(6,2));
		uh.addCard(new Card(8,1));
		dh.addCard(new Card(4,3));
		assertEquals("P", s.getMove(uh, dh, true));
		assertEquals("S", s.getMove(uh, dh, false));
		s.changeChartVal("16", "6", "H", "HARD");
		assertEquals("H", s.getMove(uh, dh, false));
		// fail("Not yet implemented");
	}

}
