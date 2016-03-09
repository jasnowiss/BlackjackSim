package Blackjack;

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Scanner;

public class BlackjackTests {

	Blackjack b;
	Class c;
	HelperMethods h = new HelperMethods();
	
	public void reset() {
		b = new Blackjack();
		c = b.getClass();
	}
	
	@Test
	public void simpleUserInputTest() {
		reset();
		
		Field s = h.getField(c, "scanner");
		h.setField(b, s, new Scanner("h\nr\nt\nq\n"));
        Method m = h.getMethod(c, "simAskAndSetOptions", null);
        h.invokeMethod(b, m);
       
        Class[] cArg = {String.class};
        Method m2 = h.getMethod(c, "isNumeric", cArg);
        boolean res = (Boolean) h.invokeMethod(b, m2, "243");
        assertEquals(res, true);
        
		// fail("Not yet implemented");		
	}
	
}
