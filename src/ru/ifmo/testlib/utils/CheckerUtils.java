package ru.ifmo.testlib.utils;

import ru.ifmo.testlib.Outcome;
import ru.ifmo.testlib.Outcome.Type;

/**
 * 
 * @author Andrey Plotnikov (Shemplo)
 *
 */
public class CheckerUtils {
    
    public static Number assertEquals (Number a, Number b) throws Outcome {
        if (!a.equals (b)) {
            throw Outcome.quit (Type.WA, "Numbers `%s` and `%s` are not equal", a, b);
        }
        
        return a;
    }
    
    public static Number assertEquals (Number a, Number b, double precision) throws Outcome {
        if (Math.abs (a.doubleValue () - b.doubleValue ()) > precision) {
            throw Outcome.quit (Type.WA, "Numbers `%s` and `%s` are not equal", a, b);
        }
        
        return a;
    }
    
    public static String assertEquals (String a, String b) throws Outcome {
        if (!a.equals (b)) {
            throw Outcome.quit (Type.WA, "Strings `%s` and `%s` are not equal", 
                Utils.shrinkString (a, 50), Utils.shrinkString (b, 50)
            );
        }
        
        return a;
    }
    
    public static Number assertDivisible (Number a, long divider) {
        if (divider == 0) {
            throw Outcome.quit (Type.WA, "Divider `%s` is 0", divider);
        } else if (a.longValue () % divider != 0) {
            throw Outcome.quit (Type.WA, "Number `%s` is not divisible by `%d`", a, divider);
        }
        
        return a;
    }
    
    public static Number assertDivisible (Number a, double divider) {
        if (divider == 0.0) {
            throw Outcome.quit (Type.WA, "Divider `%s` is 0.0", divider);
        } 
        
        double divided = a.doubleValue () / divider;
        if (Math.abs (Math.floor (divided) - divided) > 1e-8) {
            throw Outcome.quit (Type.WA, "Number `%s` is not divisible by `%d`", a, divider);
        }
        
        return a;
    }
    
    public static Number assertInRange (Number a, Number min, Number max) throws Outcome {
        if (a.doubleValue () < min.doubleValue () || a.doubleValue () > max.doubleValue ()) {
            throw Outcome.quit (Type.WA, "Number `%s` is out of range [%s; %s]", a, min, max);
        }
        
        return a;
    }
    
    public static float assertFinite (float a) {
        if (!Float.isFinite (a)) {
            throw Outcome.quit (Type.WA, "Number `%s` is not a finite float value", a);
        }
        
        return a;
    }
    
    public static double assertFinite (double a) {
        if (!Double.isFinite (a)) {
            throw Outcome.quit (Type.WA, "Number `%s` is not a finite double value", a);
        }
        
        return a;
    }
    
    public static String assertNonEmpty (String value) {
        if (value == null || value.isBlank ()) {
            throw Outcome.quit (Type.PE, "Non-empty string was expected");
        }
        
        return value;
    }
    
}
