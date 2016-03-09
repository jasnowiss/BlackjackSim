package Blackjack;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BlackjackHandTests.class, BlackjackTests.class,
		CardTests.class, DeckTests.class, HandTests.class, StrategyTests.class })
public class AllTests {
	
}
