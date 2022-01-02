package ru.ifmo.testlib.verifiers;

import ru.ifmo.testlib.*;

import java.io.*;

/**
 * A result adapter for Testsys.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class KittenResultAdapter implements ResultAdapter {
    /**
     * Applies the optional command-line arguments to the verifier.
     *
     * @param args the extra arguments to the result adapter.
     */
    public void initArgs(String[] args) {}

    /**
     * Returns the exit code for the specified quit type. It may differ from platform to platform.
     *
     * @param outcome the quit type.
     * @return the exit code.
     */
    public int getExitCodeFor(Outcome outcome) {
        switch (outcome.getType()) {
            case OK:   return 0;
            case PE:   return 2;
            case FAIL: return 3;
            case POINTS: throw new UnsupportedOperationException();
            default:   return 1;
        }
    }

    /**
     * Prints an outcome message to the specified writer.
     *
     * @param outcome   the outcome.
     * @param logWriter the writer to print to.
     * @param console   <tt>true</tt> if the writer prints on console, <tt>false</tt> if to file.
     */
    public void printMessage(Outcome outcome, PrintWriter logWriter, boolean console) {
        switch (outcome.getType()) {
            case FAIL:
                logWriter.print("* Облом * ");
                break;
            case PE:
                logWriter.print("* Формат в/в * ");
                break;
            case OK:
                logWriter.print("* ok * ");
                break;
            case WA:
                logWriter.print("* Неверный ответ * ");
                break;
            case POINTS:
                throw new UnsupportedOperationException();
            default:
                logWriter.print("Непонятный код завершения ??? ");
        }
        logWriter.println(outcome.getComment());
    }
}
