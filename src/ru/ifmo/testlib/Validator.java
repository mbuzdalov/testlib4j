package ru.ifmo.testlib;

/**
 * Interface for solution answer validation.
 * 
 * @author Shemplo
 *
 */
public interface Validator {
    
    Outcome validate (InStream out);
    
}
