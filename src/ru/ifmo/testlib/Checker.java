package ru.ifmo.testlib;

/**
 * This is an interface for all checkers.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public interface Checker {
    Outcome test(InStream inf, InStream ouf, InStream ans);
}
