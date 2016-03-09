package Blackjack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* This class utilizes reflection to give access to all fields and methods in the project suite. */
public class HelperMethods {

	public Field getField(Class c, String s) { // class, field name
		try {
			Field f = c.getDeclaredField(s);
			f.setAccessible(true);
			return f;
		} catch (SecurityException e) {
			System.out.println(e.toString());
		} catch (NoSuchFieldException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	public void setField(Object o1, Field f, Object o2) { // class object, field, new value
		try {
			f.set(o1, o2);
		} catch (IllegalAccessException e) {
			System.out.println(e.toString());
	    }
	}
	
	public Method getMethod(Class c, String s, Class[] cAr) { // class, method name, method parameter types
		try {
			Method m = c.getDeclaredMethod(s, cAr);
			m.setAccessible(true);
			return m;
		} catch (SecurityException e) {
			System.out.println(e.toString());
		} catch(NoSuchMethodException e) {
	        System.out.println(e.toString());
	    }
		return null;
	}
	
	public Object invokeMethod(Object o, Method m) { // class object, method
		try {
			return m.invoke(o);
		} catch (IllegalArgumentException e) {
			System.out.println(e.toString());
		} catch (IllegalAccessException e) {
			System.out.println(e.toString());
		} catch (InvocationTargetException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	public Object invokeMethod(Object o1, Method m, Object o2) { // class object, method, args
		try {
			return m.invoke(o1, o2);
		} catch (IllegalArgumentException e) {
			System.out.println(e.toString());
		} catch (IllegalAccessException e) {
			System.out.println(e.toString());
		} catch (InvocationTargetException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	public Object invokeMethod(Object o1, Method m, Object o2, Object o3) { // class object, method, args
		try {
			return m.invoke(o1, o2, o3);
		} catch (IllegalArgumentException e) {
			System.out.println(e.toString());
		} catch (IllegalAccessException e) {
			System.out.println(e.toString());
		} catch (InvocationTargetException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	public Object invokeMethod(Object o1, Method m, Object o2, Object o3, Object o4) { // class object, method, args
		try {
			return m.invoke(o1, o2, o3, o4);
		} catch (IllegalArgumentException e) {
			System.out.println(e.toString());
		} catch (IllegalAccessException e) {
			System.out.println(e.toString());
		} catch (InvocationTargetException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	public Object invokeMethod(Object o1, Method m, Object o2, Object o3, Object o4, Object o5) { // class object, method, args
		try {
			return m.invoke(o1, o2, o3, o4, o5);
		} catch (IllegalArgumentException e) {
			System.out.println(e.toString());
		} catch (IllegalAccessException e) {
			System.out.println(e.toString());
		} catch (InvocationTargetException e) {
			System.out.println(e.toString());
		}
		return null;
	}

}
