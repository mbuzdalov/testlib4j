package ru.ifmo.testlib;

import java.io.*;

/**
 * The interface for a result adapter.
 * This helps to adapt outcomes to various testing systems,
 * such as the one for PCMS by Computer Technologies Department of ITMO University,
 * Testsys by Kitten Computing or ejudge by Alexander Chernov.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public interface ResultAdapter {
    /**
     * Applies the optional command-line arguments to the result adapter.
     *
     * @param args the extra arguments to the result adapter.
     */
    void initArgs(String[] args);

    /**
     * Returns the exit code for the specified quit type. It may differ from platform to platform.
     *
     * @param outcome the quit type.
     * @return the exit code.
     */
    int getExitCodeFor(Outcome outcome);

    /**
     * Prints an outcome message to the specified writer.
     * The behaviour may differ depending on whether console is used, so this is provided as a parameter.
     *
     * @param outcome the outcome.
     * @param result the writer to print to.
     * @param console <tt>true</tt> if the writer prints on console, <tt>false</tt> if to file.
     */
    void printMessage(Outcome outcome, PrintWriter result, boolean console);
}
