package ru.ifmo.testlib;

/**
 * Interface for problem input validation.
 * 
 * @author Shemplo
 *
 */
public interface Validator {
    
    /**
     * Checks whether input satisfies problem statements.
     * In case of input doesn't fit to required conditions use
     * {@link InStream#quit(ru.ifmo.testlib.Outcome.Type, String) quit(Outcome.Type, String)} 
     * method to notify about that.
     * 
     * @param in stream of input data for particular test
     */
    void validate (InStream in);
    
}
