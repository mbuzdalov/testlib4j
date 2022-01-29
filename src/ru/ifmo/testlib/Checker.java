package ru.ifmo.testlib;

/**
 * The interface for all checkers.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public interface Checker {
    
    /**
     * Runs the checker on the given input, output and answer streams.
     * Returns (or throws, at the discretion of the implementor) the outcome.
     *
     * @param inf the stream corresponding to the input file.
     * @param ouf the stream corresponding to the output file (the one the contestant has created).
     * @param ans the stream corresponding to the answer file (the one the jury hsa created).
     * @return the outcome.
     * @throws Outcome the outcome.
     */
    Outcome test(InStream inf, InStream ouf, InStream ans);
    
}
