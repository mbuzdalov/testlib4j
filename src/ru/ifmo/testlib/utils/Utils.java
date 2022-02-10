package ru.ifmo.testlib.utils;

/**
 * 
 * @author Andrey Plotnikov (Shemplo)
 *
 */
public class Utils {
    
    public static String shrinkString (String str, int maxLength) {
        return str.length () > maxLength - 3 ? str.substring (0, maxLength - 3) + "..." : str;
    }
    
}
