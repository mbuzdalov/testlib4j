package ru.ifmo.testlib;

import ru.ifmo.testlib.verifiers.*;
import ru.ifmo.testlib.io.*;

import static ru.ifmo.testlib.Outcome.Type.*;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * This is the checker framework, which is the entry point for all checkers.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class CheckerFramework {
    private static final String SYS_EXIT_DISABLED = "System.exit(int) is disabled. Exiting abnormally.";
    private static final String USAGE =
            "Usage: [<verifier_classname>] <input_file> <output_file> <answer_file> [<result_file> [<test_system_args>]]\n" +
            "    (<verifier_classname> may also be specified in MANIFEST.MF as Checker-Class attribute)";

    private static final String DEFAULT_RESULT_ADAPTER = "checker-type:ifmo";

    private final static String CHECKER_CLASS_ENTRY = "Checker-Class";

    @Deprecated
    public static boolean PE_IF_OK_AND_NOT_EOF = true;

    private static HashMap<String, ResultAdapter> resultAdapters = new HashMap<>();

    private static void registerResultAdapter(String id, ResultAdapter v) {
        resultAdapters.put(id, v);
    }

    static {
        registerResultAdapter("checker-type:ifmo", new IFMOResultAdapter());
        registerResultAdapter("checker-type:kitten", new KittenResultAdapter());
        registerResultAdapter("checker-type:ejudge", new EJudgeResultAdapter());
    }

    private static void misusage() {
        System.err.println(USAGE);
        System.exit(3);
    }

    private static void fatal(String message, Object... args) {
        System.err.println(String.format(message, args));
        System.exit(3);
    }

    private static String findVerifier() {
        try {
            Enumeration<URL> resources = CheckerFramework.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                Attributes attrs = manifest.getMainAttributes();
                String checkerClass = attrs.getValue(CHECKER_CLASS_ENTRY);
                if (checkerClass != null) {
                    return checkerClass;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public static void main(String[] args) {
        int delta = 0;
        if (args.length < 3 + delta) {
            misusage();
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        // TODO fix verifier -> result adapter
        String verifierClassName = findVerifier();
        if (verifierClassName == null) {
            verifierClassName = args[0];
            delta = 1;
        }
        
        if (args[0].equals(verifierClassName)) {
            // Compatibility fix
            delta = 1;
        }

        if (args.length < 3 + delta) {
            misusage();
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        Checker checker;
        try {
            checker = (Checker) (Class.forName(verifierClassName).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            fatal(e.getMessage());
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        InputInStream input = new InputInStream(new File(args[delta]));
        OutputInStream output = new OutputInStream(new File(args[1 + delta]));
        AnswerInStream answer = new AnswerInStream(new File(args[2 + delta]));

        PrintWriter result;

        String[] verifierArgs;

        try {
            if (args.length > 3 + delta) {
                result = new PrintWriter(args[3 + delta], "utf-8");
                verifierArgs = new String[args.length - 4 - delta];
                System.arraycopy(args, 4 + delta, verifierArgs, 0, verifierArgs.length);
            } else {
                result = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"));
                verifierArgs = new String[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
            fatal(e.getMessage());
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        String verifierType = System.getProperty("checker-type");
        if (verifierType == null) {
            verifierType = DEFAULT_RESULT_ADAPTER;
        }
        if (verifierType.indexOf(':') == -1) {
            verifierType = "checker-type:" + verifierType;
        }

        ResultAdapter resultAdapter = resultAdapters.get(verifierType);

        if (resultAdapter == null) {
            System.err.println("No result adapter found (property checker-type = \"" + verifierType + "\")");
            System.exit(1);
        }

        resultAdapter.initArgs(verifierArgs);

        Outcome outcome;
        try {
            outcome = checker.test(input, output, answer);
        } catch (Outcome out) {
            outcome = out;
        } catch (Throwable th) {
            th.printStackTrace();
            outcome = new Outcome(FAIL, th.toString());
        }
        if (outcome.getType() == OK && PE_IF_OK_AND_NOT_EOF && !output.seekEoF()) {
            outcome = new Outcome(PE, "Extra information in output file");
        }
        resultAdapter.printMessage(outcome, result, args.length <= 4);

        result.close();
        input.close();
        output.close();
        answer.close();

        System.exit(resultAdapter.getExitCodeFor(outcome));
    }
}
