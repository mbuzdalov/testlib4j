package ru.ifmo.testlib.verifiers;

import ru.ifmo.testlib.*;

import java.io.*;

/**
 * A result adapter for APPES and PCMS2.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class IFMOResultAdapter implements ResultAdapter {
    private static final String APPES_MODE_ARG = "-appes";
    private static final String XML_MODE_ARG = "-xml";

    private boolean xmlMode;

    private String[] outcomes = {
        "accepted", "wrong-answer", "presentation-error", "fail", "points"
    };

    /**
     * Applies the optional command-line arguments to the verifier.
     *
     * @param args the extra arguments to the result adapter.
     */
    public void initArgs(String[] args) {
        xmlMode = (args.length > 0 && (args[0].equalsIgnoreCase(APPES_MODE_ARG) || args[0].equalsIgnoreCase(XML_MODE_ARG)));
    }

    /**
     * Returns the exit code for the specified quit type. It may differ from platform to platform.
     *
     * @param outcome the quit type.
     * @return the exit code.
     */
    public int getExitCodeFor(Outcome outcome) {
        if (xmlMode) {
            return 0;
        }

        switch (outcome.getType()) {
            case OK:   return 0;
            case WA:   return 1;
            case PE:   return 2;
            case FAIL: return 3;
            case POINTS: return 7;
            default:   return 3;
        }
    }

    /**
     * Prints an outcome message to the specified writer.
     *
     * @param outcome   the outcome.
     * @param result the writer to print to.
     * @param console   <tt>true</tt> if the writer prints on console, <tt>false</tt> if to file.
     */
    public void printMessage(Outcome outcome, PrintWriter result, boolean console) {
        if (console) {
            switch (outcome.getType()) {
            case FAIL:
                result.print("FAIL ");
                break;
            case PE:
                result.print("wrong output format ");
                break;
            case OK:
                result.print("ok ");
                break;
            case WA:
                result.print("wrong answer ");
                break;
            case POINTS:
                result.print("points ");
                break;
            }
            result.println(outcome.getComment());
        } else {
            if (xmlMode) {
                result.println("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
                result.print("<result outcome = \"" + outcomes[outcome.getType().ordinal()] + "\" comment = \"");
                xmlSafeWrite(result, outcome.getComment());
                result.println("\" />");
            } else {
                result.println(outcome.getComment());
            }
        }
    }

    private static void xmlSafeWrite(PrintWriter result, String message) {
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            switch (c) {
                case '&':  result.print("&amp;"); break;
                case '<':  result.print("&lt;"); break;
                case '>':  result.print("&gt;"); break;
                case '"':  result.print("&quot;"); break;
                case '\'': result.print("&apos;"); break;
                default:
                    result.print((c < ' ') ? '.' : c);
            }
        }
    }
}
