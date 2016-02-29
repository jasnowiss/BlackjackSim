import java.util.Arrays;
// import java.util.ArrayList;
import java.util.LinkedList;

public class Strategy {

	/* Chart values taken from wizardofodds.com. */
	/* "H" hit; "S" stand; "Dh" double if allowed, otherwise hit; "Ds" double if allowed, otherwise stand */
	/* "P" split; "Ph" split if double after split is allowed, otherwise hit; "Pd" split if double after split is allowed, otherwise double; "Ps" split if double after split is allowed, otherwise stand */
	/* "Rh" surrender if allowed, otherwise hit; "Rs" surrender if allowed, otherwise stand; "Rp" surrender if allowed, otherwise split */
	
	/* Rules that would require additional strategy charts to be played optimally */
	/* Early surrender, 5-7 card Charlie, changing the value which the dealer has to hit */
	
	private static String[][] basicChart;
	private static String[][] aceChart;
	private static String[][] splitChart;
	private static boolean optimal;
	private static int[] trueCountAr;
	
	private static String[][] basicChartDefault;
	private static String[][] aceChartDefault;
	private static String[][] splitChartDefault;
	private static int[] trueCountArDefault;
	
	private static LinkedList<String[][]> basicChartLastTen = new LinkedList<String[][]>();
	private static LinkedList<String[][]> aceChartLastTen = new LinkedList<String[][]>();
	private static LinkedList<String[][]> splitChartLastTen = new LinkedList<String[][]>();
	private static LinkedList<int[]> betStratLastTen = new LinkedList<int[]>();
	
	private static String[][] basicChart1NS17 = {
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh"},
    	{"H","H","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","Rh","Rh"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21) {2,3,4,5,6,7,8,9,10,A} (for basic hands with stand on soft 17)
	
    private static String[][] basicChart1S17 = {
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh"},
    	{"H","H","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","Rh"},
    	{"S","S","S","S","S","H","H","H","Rh","Rh"},
    	{"S","S","S","S","S","S","S","S","S","Rs"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21) {2,3,4,5,6,7,8,9,10,A} (for basic hands with hit on soft 17)
	
    private static String[][] aceChart1NS17 = {
        {"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"S","Ds","Ds","Ds","Ds","S","S","H","H","S"},
    	{"S","S","S","S","Ds","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (A,2 A,3 A,4 A,5 A,6 A,7 A,8 A,9 A,10) {2,3,4,5,6,7,8,9,10,A} (for ace as 11 with stand on soft 17)
		
    private static String[][] aceChart1S17 = {
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"S","Ds","Ds","Ds","Ds","S","S","H","H","H"},
    	{"S","S","S","S","Ds","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (A,2 A,3 A,4 A,5 A,6 A,7 A,8 A,9 A,10) {2,3,4,5,6,7,8,9,10,A} (for ace as 11 with hit on soft 17) 	
    
    private static String[][] splitChart1NS17 = {
    	{"Ph","P","P","P","P","P","H","H","H","H"},
    	{"Ph","Ph","P","P","P","P","Ph","H","H","H"},
    	{"H","H","H","Ph","Pd","Pd","H","H","H","H"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","Ph","H","H","H","H"},
    	{"P","P","P","P","P","P","Ph","H","Rs","H"},
    	{"P","P","P","P","P","P","P","P","P","P"},
    	{"P","P","P","P","P","S","P","P","S","S"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","P","P","P","P","P"}
    }; // (2,2 3,3 4,4 5,5 6,6 7,7 8,8 9,9 10,10 A,A) {2,3,4,5,6,7,8,9,10,A} (for splitting with stand on soft 17)
    
    private static String[][] splitChart1S17 = {
    	{"Ph","P","P","P","P","P","H","H","H","H"},
    	{"Ph","Ph","P","P","P","P","Ph","H","H","H"},
    	{"H","H","H","Ph","Pd","Pd","H","H","H","H"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","Ph","H","H","H","H"},
    	{"P","P","P","P","P","P","Ph","H","Rs","Rh"},
    	{"P","P","P","P","P","P","P","P","P","P"},
    	{"P","P","P","P","P","S","P","P","S","Ps"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","P","P","P","P","P"}
    }; // (2,2 3,3 4,4 5,5 6,6 7,7 8,8 9,9 10,10 A,A) {2,3,4,5,6,7,8,9,10,A} (for splitting with hit on soft 17)
	
    private static String[][] basicChart2_3NS17 = {
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh"},
    	{"H","H","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","Rh","H"},
    	{"S","S","S","S","S","H","H","H","Rh","Rh"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21) {2,3,4,5,6,7,8,9,10,A} (for basic hands with stand on soft 17)
	
    private static String[][] basicChart2_3S17 = {
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh"},
    	{"H","H","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","Rh","Rh"},
    	{"S","S","S","S","S","H","H","H","Rh","Rh"},
    	{"S","S","S","S","S","S","S","S","S","Rs"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21) {2,3,4,5,6,7,8,9,10,A} (for basic hands with hit on soft 17)
	
    private static String[][] aceChart2_3NS17 = {
        {"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"S","Ds","Ds","Ds","Ds","S","S","H","H","H"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (A,2 A,3 A,4 A,5 A,6 A,7 A,8 A,9 A,10) {2,3,4,5,6,7,8,9,10,A} (for ace as 11 with stand on soft 17)
		
    private static String[][] aceChart2_3S17 = {
    	{"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Ds","Ds","Ds","Ds","Ds","S","S","H","H","H"},
    	{"S","S","S","S","Ds","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (A,2 A,3 A,4 A,5 A,6 A,7 A,8 A,9 A,10) {2,3,4,5,6,7,8,9,10,A} (for ace as 11 with hit on soft 17) 	
    
    private static String[][] splitChart2_3NS17 = {
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"H","H","H","Ph","Ph","H","H","H","H","H"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","Ph","H","H","H","H"},
    	{"P","P","P","P","P","P","Ph","H","H","H"},
    	{"P","P","P","P","P","P","P","P","P","P"},
    	{"P","P","P","P","P","S","P","P","S","S"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","P","P","P","P","P"}
    }; // (2,2 3,3 4,4 5,5 6,6 7,7 8,8 9,9 10,10 A,A) {2,3,4,5,6,7,8,9,10,A} (for splitting with stand on soft 17)
    
    private static String[][] splitChart2_3S17 = {
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"H","H","H","Ph","Ph","H","H","H","H","H"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","Ph","H","H","H","H"},
    	{"P","P","P","P","P","P","Ph","H","H","H"},
    	{"P","P","P","P","P","P","P","P","P","Rp"},
    	{"P","P","P","P","P","S","P","P","S","S"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","P","P","P","P","P"}
    }; // (2,2 3,3 4,4 5,5 6,6 7,7 8,8 9,9 10,10 A,A) {2,3,4,5,6,7,8,9,10,A} (for splitting with hit on soft 17)
 
    private static String[][] basicChart4_8NS17 = {
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","H"},
    	{"H","H","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","Rh","H"},
    	{"S","S","S","S","S","H","H","Rh","Rh","Rh"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21) {2,3,4,5,6,7,8,9,10,A} (for basic hands with stand on soft 17)
	
    private static String[][] basicChart4_8S17 = {
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","H","H","H","H","H","H","H","H","H"},
    	{"H","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","H","H"},
    	{"Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh","Dh"},
    	{"H","H","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","H","H"},
    	{"S","S","S","S","S","H","H","H","Rh","Rh"},
    	{"S","S","S","S","S","H","H","Rh","Rh","Rh"},
    	{"S","S","S","S","S","S","S","S","S","Rs"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21) {2,3,4,5,6,7,8,9,10,A} (for basic hands with hit on soft 17)
	
    private static String[][] aceChart4_8NS17 = {
        {"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"S","Ds","Ds","Ds","Ds","S","S","H","H","H"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (A,2 A,3 A,4 A,5 A,6 A,7 A,8 A,9 A,10) {2,3,4,5,6,7,8,9,10,A} (for ace as 11 with stand on soft 17)
		
    private static String[][] aceChart4_8S17 = {
    	{"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"H","H","H","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","H","Dh","Dh","Dh","H","H","H","H","H"},
    	{"H","Dh","Dh","Dh","Dh","H","H","H","H","H"},
    	{"Ds","Ds","Ds","Ds","Ds","S","S","H","H","H"},
    	{"S","S","S","S","Ds","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"},
    	{"S","S","S","S","S","S","S","S","S","S"}
    }; // (A,2 A,3 A,4 A,5 A,6 A,7 A,8 A,9 A,10) {2,3,4,5,6,7,8,9,10,A} (for ace as 11 with hit on soft 17) 	
    
    private static String[][] splitChart4_8NS17 = {
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"H","H","H","Ph","Ph","H","H","H","H","H"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"Ph","P","P","P","P","H","H","H","H","H"},
    	{"P","P","P","P","P","P","H","H","H","H"},
    	{"P","P","P","P","P","P","P","P","P","P"},
    	{"P","P","P","P","P","S","P","P","S","S"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","P","P","P","P","P"}
    }; // (2,2 3,3 4,4 5,5 6,6 7,7 8,8 9,9 10,10 A,A) {2,3,4,5,6,7,8,9,10,A} (for splitting with stand on soft 17)
    
    private static String[][] splitChart4_8S17 = {
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"Ph","Ph","P","P","P","P","H","H","H","H"},
    	{"H","H","H","Ph","Ph","H","H","H","H","H"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"Ph","P","P","P","P","H","H","H","H","H"},
    	{"P","P","P","P","P","P","H","H","H","H"},
    	{"P","P","P","P","P","P","P","P","P","Rp"},
    	{"P","P","P","P","P","S","P","P","S","S"},
    	{"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"},
    	{"P","P","P","P","P","P","P","P","P","P"}
    }; // (2,2 3,3 4,4 5,5 6,6 7,7 8,8 9,9 10,10 A,A) {2,3,4,5,6,7,8,9,10,A} (for splitting with hit on soft 17)
    
    private static void changeChartVal(String pv, String dv, String nv, String ch) {
    	int pvval = -1;
    	if (ch.toUpperCase().equals("HARD")) {
    		pvval = Integer.parseInt(pv) - 4;
    	} else if (ch.toUpperCase().equals("SOFT")) {
    		pvval = Integer.parseInt(pv) - 13;
    	} else if (ch.toUpperCase().equals("SPLIT")){
    		if (pv.equals("A")) {
    			pvval = 9;
    		} else {
    			pvval = Integer.parseInt(pv) - 2;
    		}
    	}
    	int dvval;
    	if (dv.equals("A")) {
    		dvval = 9;
    	} else {
    		dvval = Integer.parseInt(dv) - 2;
    	}
    	String nvval = nv;
    	if (nvval.length() == 2 && !nvval.equals("NA")) {
    		nvval = "" + nvval.charAt(0) + Character.toLowerCase(nvval.charAt(1));
    	}
    	if (ch.toUpperCase().equals("HARD")) {
    		basicChart[pvval][dvval] = nvval;
    	} else if (ch.toUpperCase().equals("SOFT")) {
    		aceChart[pvval][dvval] = nvval;
    	} else if (ch.toUpperCase().equals("SPLIT")) {
    		splitChart[pvval][dvval] = nvval;
    	}
    }
    
    private static void changeBetVal(String tc, String nv) {
    	int tcval = Integer.parseInt(tc);
    	int nvval = Integer.parseInt(nv);
    	trueCountAr[tcval] = nvval;
    }
    
    public static void setStrategy(int numdecks, boolean softhit, int hitnum, int surrendnum, int charliecount, String betstrat) {
    	optimal = true;
    	if (hitnum != 16) {
    		optimal = false;
    	}
    	if (surrendnum == 1) {
    		optimal = false;
    	}
    	if (charliecount != -1) {
    		optimal = false;
    	}
    	if (numdecks == 1) {
    		if (softhit) {
    			basicChart = basicChart1S17;
    			aceChart = aceChart1S17;
    			splitChart = splitChart1S17;
    		} else {
    			basicChart = basicChart1NS17;
    			aceChart = aceChart1NS17;
    			splitChart = splitChart1NS17;
    		}
    	} else if (numdecks < 4) {
    		if (softhit) {
    			basicChart = basicChart2_3S17;
    			aceChart = aceChart2_3S17;
    			splitChart = splitChart2_3S17;
    		} else {
    			basicChart = basicChart2_3NS17;
    			aceChart = aceChart2_3NS17;
    			splitChart = splitChart2_3NS17;
    		}
    	} else {
    		if (softhit) {
    			basicChart = basicChart4_8S17;
    			aceChart = aceChart4_8S17;
    			splitChart = splitChart4_8S17;
    		} else {
    			basicChart = basicChart4_8NS17;
    			aceChart = aceChart4_8NS17;
    			splitChart = splitChart4_8NS17;
    		}
    	}
    	basicChartDefault = deepCopy(basicChart);
    	aceChartDefault = deepCopy(aceChart);
    	splitChartDefault = deepCopy(splitChart);
    	basicChart = deepCopy(basicChartDefault);
    	aceChart = deepCopy(aceChartDefault);
    	splitChart = deepCopy(splitChartDefault);
    	setBettingStrat(betstrat);
    }
    
    /* Means no charts are available for this strategy. */
    private static boolean isOptimal() { // setStrategy must be called once before this method is called. checks if the strategy charts used are optimal
    	return optimal;
    }
    
    public static String getMove(BlackjackHand userhand, BlackjackHand dealerhand, boolean cansplit) {
    	int uval = userhand.getBlackjackValue();
    	boolean a11 = userhand.isAce11();
    	int dval = dealerhand.getDealerValue();
    	String move = "";
    	
    	int utemp;
    	int dtemp = dval - 2; // for matrix offset
    	if (cansplit) {
    		utemp = (uval / 2) - 2; // offset for split charts
    		move = splitChart[utemp][dtemp];
    		if (!move.equals("NA")) {
    			return move;
    		}
    	}
    	if (a11 && uval > 12) {
    		utemp = uval - 13; // offset for ace charts
    		move = aceChart[utemp][dtemp];
    		return move;
    	}
    	utemp = uval - 4; // offset for basic charts
    	move = basicChart[utemp][dtemp];
		return move;	
    }
    
    // true counts 0,1,2,3,4,5,6,7,8,9,10 (these are multipliers)
    private static int[] noBettingStrat = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private static int[] basicBettingStrat = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5};
    private static int[] MITBettingStrat = {1, 1, 5, 10, 15, 20, 25, 30, 35, 40, 45};
    private static int[] MITImprovedStrat = {1, 1, 10, 15, 20, 25, 30, 35, 40, 45, 50};
    private static int[] guerrillaBettingStrat = {1, 1, 50, 50, 50, 50, 50, 50, 50, 50, 50};
    
    private static void setBettingStrat(String id) {
    	if (id.equals("BASIC")) {
    		trueCountAr = basicBettingStrat;
    	} else if (id.equals("MIT")) {
    		trueCountAr = MITBettingStrat;
    	} else if (id.equals("IMIT")) {
    		trueCountAr = MITImprovedStrat;
    	} else if (id.equals("RILLA")) {
    		trueCountAr = guerrillaBettingStrat;
    	} else {
    		trueCountAr = noBettingStrat;
    	}
    	trueCountArDefault = Arrays.copyOf(trueCountAr, trueCountAr.length);
    	trueCountAr = Arrays.copyOf(trueCountArDefault, trueCountArDefault.length);
    }
    
    public static double getTrueCountBet(double baseBet, int trueCount) {
    	if (trueCount < 0) {
    		return baseBet;
    	} else if (trueCount > 10) {
    		return baseBet * trueCountAr[10];
    	} else {
    		return baseBet * trueCountAr[trueCount];
    	}
    }
    
    private static void showBasicChart(String[][] chart) {
    	System.out.println();
    	System.out.println("HARD               Dealer's card");	
    	System.out.println("Player  {2} {3} {4} {5} {6} {7} {8} {9} {10}{A}");
    	// System.out.println("      ---------------------------------------");
    	for (int i = 0; i < chart.length; i++) {
    		if (i+4 < 10) {
    			System.out.print("{" + (i+4) + "}    ");
    		} else {
    			System.out.print("{" + (i+4) + "}   ");
    		}
    		// System.out.println(Arrays.toString(chart[i]));
    		twoSpaceAdjustedPrint(chart[i]);
    	}
    	System.out.println();
    }
    
    private static void showAceChart(String[][] chart) {
    	System.out.println();
    	System.out.println("SOFT               Dealer's card");	
    	System.out.println("Player  {2} {3} {4} {5} {6} {7} {8} {9} {10}{A}");
    	for (int i = 0; i < chart.length; i++) {
    		System.out.print("{" + (i+13) + "}   ");
    		// System.out.println(Arrays.toString(chart[i]));
    		twoSpaceAdjustedPrint(chart[i]);
    	}
    	System.out.println();
    }
    
    private static void showSplitChart(String[][] chart) {
    	System.out.println();
    	System.out.println("SPLITS               Dealer's Card");
    	System.out.println("Player   {2} {3} {4} {5} {6} {7} {8} {9} {10}{A}");
    	for (int i = 0; i < chart.length - 2; i++) {
    		System.out.print("{" + (i+2) + "," + (i+2) + "}   ");
    		// System.out.println(Arrays.toString(chart[i]));
    		twoSpaceAdjustedPrint(chart[i]);
    	}
    	System.out.print("{10,10} ");
    	// System.out.println(Arrays.toString(chart[i]));
		twoSpaceAdjustedPrint(chart[chart.length - 2]);
    	System.out.print("{A,A}   ");
		// System.out.println(Arrays.toString(chart[chart.length - 1]));
		twoSpaceAdjustedPrint(chart[chart.length - 1]);
		System.out.println();
    }
    
    private static void showBettingStrat(int[] strat) {
    	System.out.println();
    	System.out.println("                      True Count");
    	System.out.println("      {0} {1} {2} {3} {4} {5} {6} {7} {8} {9} {10}");
    	System.out.print("Mult ");
    	twoSpaceAdjustedPrint(strat);
    	System.out.println();
    }
    
    private static void twoSpaceAdjustedPrint(String[] s) {
    	System.out.print("[ " + s[0]);
    	boolean oneLetter = (s[0].length() == 1);
    	for (int i = 1; i < s.length; i++) {
    		System.out.print(",");
    		if (oneLetter) { // extra space
    			System.out.print(" ");
    		}
    		System.out.print(" " + s[i]);
    		oneLetter = false;
    		if (s[i].length() == 1) {
    			oneLetter = true;
    		}
    	}
    	if (oneLetter) { // extra space
    		System.out.print(" ");
    	}
    	System.out.println("]");
    }
    
    private static void twoSpaceAdjustedPrint(int[] s) {
    	System.out.print("[ " + s[0]);
    	boolean oneDigit = (s[0] < 10);
    	for (int i = 1; i < s.length; i++) {
    		System.out.print(",");
    		if (oneDigit) { // extra space
    			System.out.print(" ");
    		}
    		System.out.print(" " + s[i]);
    		oneDigit = false;
    		if (s[i] < 10) {
    			oneDigit = true;
    		}
    	}
    	if (oneDigit) { // extra space
    		System.out.print(" ");
    	}
    	System.out.println("]");
    }
    
    private static void showPrevBasicChart(int num) {
    	showBasicChart(basicChartLastTen.get(basicChartLastTen.size() - num));
    }
    
    private static void showPrevAceChart(int num) {
    	showAceChart(aceChartLastTen.get(aceChartLastTen.size() - num));
    }
    
    private static void showPrevSplitChart(int num) {
    	showSplitChart(splitChartLastTen.get(splitChartLastTen.size() - num));
    }
    private static void showPrevBetStrat(int num) {
    	showBettingStrat(betStratLastTen.get(betStratLastTen.size() - num));
    }
    
    private static void changePrevBasicChart(int num) {
    	basicChart = deepCopy(basicChartLastTen.get(basicChartLastTen.size() - num));
    }
    
    private static void changePrevAceChart(int num) {
    	aceChart = deepCopy(aceChartLastTen.get(aceChartLastTen.size() - num));
    }
    
    private static void changePrevSplitChart(int num) {
    	splitChart = deepCopy(splitChartLastTen.get(splitChartLastTen.size() - num));
    }
    
    private static void changePrevBetStrat(int num) {
    	trueCountAr = Arrays.copyOf(betStratLastTen.get(betStratLastTen.size() - num), betStratLastTen.get(betStratLastTen.size() - num).length);
    }
    
    public static void promptStrategyWindow() {
    	System.out.println();
    	System.out.println("Strategy Window:");
    	System.out.println("Enter commands to view and edit the computer's simulation strategy.");
    	System.out.println("Type 'H' for commands help. Type 'X' to exit the Strategy Window.");
    	System.out.println("The simulation will begin once you exit the Strategy Window.");
    	
    	String cmd = "";
    	while (!cmd.equals("X")) {
	    	System.out.print("? ");
	    	cmd = Blackjack.getScanner().nextLine();
	    	cmd = cmd.replaceAll("\\s","").toUpperCase();
	    	if (!stratValidCommand(cmd)) {
	    		System.out.println("Invalid command.");
	    		System.out.println("Type 'H' for commands help. Type 'X' to exit the Strategy Window.");
	    	} else {
	    		applyStratCommand(cmd);
	    	}
    	}
    	// add some dialogue and options to give the user the ability to change the strategy charts if they wish to
    }
    
    private static boolean stratValidCommand(String c) {
    	if (c.equals("H") || c.equals("X") || c.equals("SHTC") || c.equals("SSTC") || c.equals("SSPC")
    		|| c.equals("SDHTC") || c.equals("SDSTC") || c.equals("SDSPC") || c.equals("SOPTS") || c.equals("OPTML")
    		|| c.equals("SBST") || c.equals("SDBST")) {
    		return true;
    	} else if (c.matches("CVHTC\\(.+\\)")) {
    		String temp = "([4-9]|1[0-9]|2[01])," + dvReg() + "," + nvReg();
    		String res = "CVHTC\\(" + temp + "(:" + temp + ")*\\)";
    		return c.matches(res);
    	} else if (c.matches("CVSTC\\(.+\\)")) {
        	String temp = "(1[3-9]|2[01])," + dvReg() + "," + nvReg();
        	String res = "CVSTC\\(" + temp + "(:" + temp + ")*\\)";
       		return c.matches(res);
    	} else if (c.matches("CVSPC\\(.+\\)")) {
        	String temp = "([2-9]|10|A)," + dvReg() + "," + nvRegSp();
        	String res = "CVSPC\\(" + temp + "(:" + temp + ")*\\)";
       		return c.matches(res);
    	} else if (c.matches("CVBST\\(.+\\)")) {
        	String temp = "([0-9]|10),([1-9]\\d?)";
        	String res = "CVBST\\(" + temp + "(:" + temp + ")*\\)";
       		return c.matches(res);    
    	} else if (c.matches("SPHTC([1-9]|10)") || c.matches("SPSTC([1-9]|10)") || c.matches("SPSPC([1-9]|10)") ||
    			c.matches("SPBST([1-9]|10)") || c.matches("CPHTC([0-9]|10)") ||c.matches("CPSTC([0-9]|10)") ||
    			c.matches("CPSPC([0-9]|10)") || c.matches("CPBST([0-9]|10)") || c.matches("CPALL([0-9]|10)")) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    private static void applyStratCommand(String c) {
    	if (c.equals("H")) {
    		showCommands();
    	} else if (c.equals("SHTC")) {
    		showBasicChart(basicChart);
    	} else if (c.equals("SSTC")) {
    		showAceChart(aceChart);
    	} else if (c.equals("SSPC")) {
    		showSplitChart(splitChart);
    	} else if (c.equals("SDHTC")) {
    		showBasicChart(basicChartDefault);
    	} else if (c.equals("SDSTC")) {
    		showAceChart(aceChartDefault);
    	} else if (c.equals("SDSPC")) {
    		showSplitChart(splitChartDefault);
    	} else if (c.equals("SOPTS")) {
    		System.out.println();
    		System.out.print("Simulation options: ");
    		if (Blackjack.options.get(0).equals("")) {
    			System.out.println("DEFAULT");
    		} else {
    			for (int i = 0; i < Blackjack.options.size() - 1; i++) {
    				System.out.print(Blackjack.options.get(i) + ", ");
    			}
    			System.out.println(Blackjack.options.get(Blackjack.options.size() - 1));
    		}
    		System.out.println();
    	} else if (c.equals("OPTML")) {
    		System.out.println();
    		if (isOptimal()) {
        		System.out.println("The strategy charts being used are optimal.");
        	} else {
        		System.out.println("The strategy charts being used are NOT optimal.");
        	}
    		System.out.println();
    	} else if (c.equals("SBST")) {
    		showBettingStrat(trueCountAr);
    	} else if (c.equals("SDBST")) {
    		showBettingStrat(trueCountArDefault);
    	} else if (c.contains("CVHTC")) {
    		String[] temp = c.substring(6,c.length() - 1).split(",|:");
    		for (int i = 0; i < temp.length; i += 3) {
    			changeChartVal(temp[i], temp[i+1], temp[i+2], "HARD");
    		}
    	} else if (c.contains("CVSTC")) {
    		String[] temp = c.substring(6,c.length() - 1).split(",|:");
    		for (int i = 0; i < temp.length; i += 3) {
    			changeChartVal(temp[i], temp[i+1], temp[i+2], "SOFT");
    		}
    	} else if (c.contains("CVSPC")) {
    		String[] temp = c.substring(6,c.length() - 1).split(",|:");
    		for (int i = 0; i < temp.length; i += 3) {
    			changeChartVal(temp[i], temp[i+1], temp[i+2], "SPLIT");
    		}
    	} else if (c.contains("CVBST")) {
    		String[] temp = c.substring(6,c.length() - 1).split(",|:");
    		for (int i = 0; i < temp.length; i += 2) {
    			changeBetVal(temp[i], temp[i+1]);
    		}
    	} else if (c.contains("SPHTC")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > basicChartLastTen.size()) {
    			noPrev();
    		} else {
    			showPrevBasicChart(temp);
    		}
    	} else if (c.contains("SPSTC")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > aceChartLastTen.size()) {
    			noPrev();
    		} else {
    			showPrevAceChart(temp);
    		}
    	} else if (c.contains("SPSPC")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > splitChartLastTen.size()) {
    			noPrev();
    		} else {
    			showPrevSplitChart(temp);
    		}
    	} else if (c.contains("SPBST")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > betStratLastTen.size()) {
    			noPrevStrat();
    		} else {
    			showPrevBetStrat(temp);
    		}
    	} else if (c.contains("CPHTC")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > basicChartLastTen.size()) {
    			noPrev();
    		} else {
    			if (temp == 0) {
    				basicChart = deepCopy(basicChartDefault);
    			} else {
    				changePrevBasicChart(temp);
    			}
    		}
    	} else if (c.contains("CPSTC")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > aceChartLastTen.size()) {
    			noPrev();
    		} else {
    			if (temp == 0) {
    				aceChart = deepCopy(aceChartDefault);
    			} else {
    				changePrevAceChart(temp);
    			}
    		}
    	} else if (c.contains("CPSPC")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > splitChartLastTen.size()) {
    			noPrev();
    		} else {
    			if (temp == 0) {
    				splitChart = deepCopy(splitChartDefault);
    			} else {
    				changePrevSplitChart(temp);
    			}
    		}
    	} else if (c.contains("CPBST")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > betStratLastTen.size()) {
    			noPrevStrat();
    		} else {
    			if (temp == 0) {
    				trueCountAr = Arrays.copyOf(trueCountArDefault, trueCountArDefault.length);
    			} else {
    				changePrevBetStrat(temp);
    			}
    		}
    	} else if (c.contains("CPALL")) {
    		int temp = Integer.parseInt(c.substring(5));
    		if (temp > basicChartLastTen.size()) {
    			noPrevChartStrat();
    		} else {
    			if (temp == 0) {
    				basicChart = deepCopy(basicChartDefault);
    				aceChart = deepCopy(aceChartDefault);
    				splitChart = deepCopy(splitChartDefault);
    				trueCountAr = Arrays.copyOf(trueCountArDefault, trueCountArDefault.length);
    			} else {
	    			changePrevBasicChart(temp);
	    			changePrevAceChart(temp);
	    			changePrevSplitChart(temp);
	    			changePrevBetStrat(temp);
    			}
    		}
    	}
    }
    
    private static void noPrev() {
    	if (basicChartLastTen.size() == 0) {
			System.out.println("No previous charts are available.");
		} else if (basicChartLastTen.size() == 1) {
			System.out.println("Only 1 previous chart is available.");
		} else {
			System.out.println("Only " + basicChartLastTen.size() + " previous charts are available.");
		}
    }
    
    private static void noPrevStrat() {
    	if (basicChartLastTen.size() == 0) {
			System.out.println("No previous strategies are available.");
		} else if (basicChartLastTen.size() == 1) {
			System.out.println("Only 1 previous strategy is available.");
		} else {
			System.out.println("Only " + basicChartLastTen.size() + " previous strategies are available.");
		}
    }
    
    private static void noPrevChartStrat() {
    	if (basicChartLastTen.size() == 0) {
			System.out.println("No previous charts and strategies are available.");
		} else if (basicChartLastTen.size() == 1) {
			System.out.println("Only the charts and betting strategy from the last simulation are available.");
		} else {
			System.out.println("Only charts and strategies from the previous " + basicChartLastTen.size() + "  simulations are available.");
		}
    }
    
    private static String dvReg() {
    	return "([2-9]|10|A)";
    }
    
    private static String nvReg() {
    	return "(H|S|DH|DS|RH|RS)";
    }
    
    private static String nvRegSp() {
    	return "(H|S|DH|DS|P|PH|PS|PD|RH|RS|RP|NA)";
    }
    
    private static void showCommands() {
    	System.out.println();
		System.out.println("COMMANDS:");
		System.out.println("SHTC - Show the hard total chart being used.");
		System.out.println("SSTC - Show the soft total chart being used.");
		System.out.println("SSPC - Show the split chart being used.");
		System.out.println("SDHTC - Show the default hard total chart.");
		System.out.println("SDSTC - Show the default soft total chart.");
		System.out.println("SDSPC - Show the default split chart.");
		System.out.println("OPTML - Says whether the default charts for this strategy are optimal.");
		System.out.println("        Optimality is based on online findings and may not be 100% accurate.");
		System.out.println("SBST - Show the betting strategy being used.");
		System.out.println("SDBST - Show the default betting strategy.");
		System.out.println("SOPTS - Show the simulation options that were set.");
		System.out.println("CVHTC(pv,dv,nv) - Change the value of a square in the hard total chart.");
		System.out.println("      Example: Player has 12 and the dealer is showing an 8.");
		System.out.println("      To change the value of this square to 'Stand', type the command CVHTC(12,8,S).");
		System.out.println("      Use colons to make multiple changes at once. e.g. CVHTC(15,4,H:16,7,S)");
		System.out.println("CVSTC(pv,dv,nv) - Change the value of a square in the soft total chart.");
		System.out.println("      Example: Player has soft 17 and the dealer is showing a 10.");
		System.out.println("      To change the value of this square to 'Double', type the command CVSTC(17,10,Dh).");
		System.out.println("      Use colons to make multiple changes at once. e.g. CVSTC(18,9,S:14,8,Dh)");
		System.out.println("CVSPC(pv,dv,nv) - Change the value of a square in the split chart.");
		System.out.println("      Example: Player has two 6s and the dealer is showing an Ace.");
		System.out.println("      To change the value of this square to 'Split', type the command CVSPC(6,A,P).");
		System.out.println("      Note that '6' is used for the player value instead of the hand total which is 12.");
		System.out.println("      Use colons to make multiple changes at once. e.g. CVSPC(A,5,S:9,10,P)");
		System.out.println("CVBST(tc,nv) - Change the multiplier value of a true count in the betting strategy.");
		System.out.println("      Example: CVBST(3,20) changes the multiplier for a true count of +3 to 20.");
		System.out.println("      Use colons to make multiple changes at once. e.g. CVBST(9,50:2,1)");
		System.out.println("      Lowest multiplier is 1, and highest is 99.");
		System.out.println("SPHTC[num] - Show a previously used hard total chart. You can see up to 10 charts back.");
		System.out.println("      e.g. SPHTC1 shows the hard total chart used in the most recent simulation.");
		System.out.println("SPSTC[num] - Show a previously used soft total chart. You can see up to 10 charts back.");
		System.out.println("      e.g. SPSTC1 shows the soft total chart used in the most recent simulation.");
		System.out.println("SPSPC[num] - Show a previously used split chart. You can see up to 10 charts back.");
		System.out.println("      e.g. SPSPC1 shows the split chart used in the most recent simulation.");
		System.out.println("SPBST[num] - Show a previously used betting strategy. You can see up to 10 strategies back.");
		System.out.println("      e.g. SPBST1 shows the betting strategy used in the most recent simulation..");
		System.out.println("CPHTC[num] - Change the current hard total chart to a previously used one. You can use any of the previous 10 charts.");
		System.out.println("      e.g. CPHTC1 changes the hard total chart to the one used in the most recent simulation.");
		System.out.println("      Use command CPHTC0 to change back to the default hard total chart.");
		System.out.println("CPSTC[num] - Change the current soft total chart to a previously used one. You can use any of the previous 10 charts.");
		System.out.println("      e.g. CPSTC1 changes the soft total chart to the one used in the most recent simulation.");
		System.out.println("      Use command CPSTC0 to change back to the default soft total chart.");
		System.out.println("CPSPC[num] - Change the current split chart to a previously used one. You can use any of the previous 10 charts.");
		System.out.println("      e.g. CPSPC1 changes the split chart to the one used in the most recent simulation.");
		System.out.println("      Use command CPSPC0 to change back to the default split chart.");
		System.out.println("CPBST[num] - Change the current betting strategy to a previously used one. You can use any of the previous 10 strategies.");
		System.out.println("      e.g. CPBST1 changes the betting strategy to the one used in the most recent simulation.");
		System.out.println("      Use command CPBST0 to change back to the default betting strategy.");
		System.out.println("CPALL[num] - Change all the charts and the betting strategy to those in a previous simulation.");
		System.out.println("      This applies to up to 10 previous simulations.");
		System.out.println("      e.g. CPALL1 changes all the charts and betting strategy to those used in the most recent simulation.");
		System.out.println("      Use command CPALL0 to change back to the default charts and betting strategy.");
		System.out.println();
		System.out.println("COMMAND NOTES");
		System.out.println("pv stands for 'player value'. Player values are shown in the leftmost column of the charts.");
		System.out.println("dv stands for 'dealer value'. Dealer values are shown in the topmost row of the charts.");
		System.out.println("nv stands for 'new value', which replaces the current value.");
		System.out.println("tc stands for 'true count'.");
		System.out.println("The values of the chart squares can be of the following: H for hit, S for stand,");
		System.out.println("Dh for double if allowed otherwise hit, Ds for double if allowed otherwise stand,");
		System.out.println("P for split, Ph for split if double after split allowed otherwise hit,");
		System.out.println("Ps for split if double after split allowed otherwise stand, Pd for split if double after split allowed otherwise double,");
		System.out.println("Rh for surrender if allowed otherwise hit, Rs for surrender if allowed otherwise stand,");
		System.out.println("Rp for surrender if allowed otherwise split, NA for not applicable.");
		System.out.println("The split and NA chart values are not valid to use in the hard and soft total charts.");
		System.out.println("Betting strategy is based on the true count. The values in the strategy are multipliers.");
		System.out.println("For example, if the true count is 5, and the multiplier is 10, then a base bet of $5 is increased to $50.");
		System.out.println("True counts below 0 default to the base bet, and true counts above 10 use the multiplier for true count 10.");
		System.out.println();
    }
    
    public static void addStrats() {
    	if (basicChartLastTen.size() == 10) { // only 10 for space and because more isn't really necessary 
    		basicChartLastTen.remove();
    		aceChartLastTen.remove();
    		splitChartLastTen.remove();
    		betStratLastTen.remove();
    	}
    	basicChartLastTen.add(basicChart);
    	aceChartLastTen.add(aceChart);
    	splitChartLastTen.add(splitChart);
    	betStratLastTen.add(trueCountAr);
    }
    
    private static String[][] deepCopy(String[][] original) {
        if (original == null) {
            return null;
        }

        final String[][] result = new String[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
            // For Java versions prior to Java 6 use the next:
            // System.arraycopy(original[i], 0, result[i], 0, original[i].length);
        }
        return result;
    }
    
}
