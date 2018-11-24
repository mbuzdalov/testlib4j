package ru.ifmo.testlib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static ru.ifmo.testlib.Outcome.Type.*;
import ru.ifmo.testlib.verifiers.EJudgeResultAdapter;
import ru.ifmo.testlib.verifiers.IFMOResultAdapter;
import ru.ifmo.testlib.verifiers.KittenResultAdapter;

/**
 * This is the checker framework, which is the entry point for all checkers.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class CheckerFramework {
    private static final String DEFAULT_RESULT_ADAPTER = "checker-type:ifmo";
    private static final String CHECKER_CLASS_ENTRY = "Checker-Class";
    private static final String EXPECTED_EXIT_CODE_PROPERTY = "testlib.expected.exitcode";
    private static final String SYS_EXIT_DISABLED = "System.exit(int) did not exit. Exiting abnormally.";
    private static final String USAGE =
            "Usage: [<verifier_classname>] <input_file> <output_file> <answer_file> [<result_file> [<test_system_args>]].\n" +
            "    The <verifier_classname> value may also be specified in MANIFEST.MF as Checker-Class attribute.";

    private static HashMap<String, ResultAdapter> resultAdapters = new HashMap<>();

    private static void registerResultAdapter(String id, ResultAdapter v) {
        resultAdapters.put(id, v);
    }

    static {
        registerResultAdapter("checker-type:ifmo", new IFMOResultAdapter());
        registerResultAdapter("checker-type:kitten", new KittenResultAdapter());
        registerResultAdapter("checker-type:ejudge", new EJudgeResultAdapter());
    }

    private static void printUsageAndExit() {
        System.err.println(USAGE);
        System.exit(3);
    }

    private static void fatal(String message, Object... args) {
        System.err.println(String.format(message, args));
        System.exit(3);
    }

    private static String findCheckerInManifest() {
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
            printUsageAndExit();
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        String checkerClassName = findCheckerInManifest();
        if (checkerClassName == null) {
            checkerClassName = args[0];
        }

        // This includes two cases:
        //   the case when the checker class name is given in the command line only.
        //   the case when the checker class name is defined in manifest AND given in the command line.
        if (args[0].equals(checkerClassName)) {
            delta = 1;
        }

        if (args.length < 3 + delta) {
            printUsageAndExit();
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }
        Class<?> checkerClass;

        try {
            checkerClass = Class.forName(checkerClassName.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fatal(e.getMessage());
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        run(args, delta, instantiateChecker(checkerClass));
    }

    private static Checker instantiateChecker(Class<?> checkerClass) {
        try {
            return (Checker) (checkerClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            fatal(e.getMessage());
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }
    }

    private static void run(String[] args, int delta, Checker checker) {
        PrintWriter result;
        boolean shallCloseResult = true;

        String[] verifierArgs;

        try {
            if (args.length > 3 + delta) {
                result = new PrintWriter(args[3 + delta], "utf-8");
                verifierArgs = new String[args.length - 4 - delta];
                System.arraycopy(args, 4 + delta, verifierArgs, 0, verifierArgs.length);
            } else {
                result = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
                verifierArgs = new String[0];
                shallCloseResult = false;
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
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        resultAdapter.initArgs(verifierArgs);

        Outcome outcome;
        try (InStream input = new FileInStream(new File(args[delta]), Outcome.nonOkayIsFail);
             InStream output = new FileInStream(new File(args[1 + delta]), Collections.emptyMap());
             InStream answer = new FileInStream(new File(args[2 + delta]), Outcome.nonOkayIsFail)) {
            try {
                outcome = checker.test(input, output, answer);
            } catch (Outcome out) {
                outcome = out;
            }

            if (outcome.getType() == OK && !output.seekEoF()) {
                outcome = new Outcome(PE, "Extra information in output file");
            }
        } catch (Outcome out) {
            outcome = out;
        } catch (Throwable th) {
            th.printStackTrace();
            outcome = new Outcome(FAIL, th.toString());
        }

        resultAdapter.printMessage(outcome, result, args.length <= 4);
        if (shallCloseResult) {
            result.close();
        } else {
            result.flush();
        }

        int theExitCode = resultAdapter.getExitCodeFor(outcome);
        try {
            String expectedExitCode = System.getProperty(EXPECTED_EXIT_CODE_PROPERTY);
            if (expectedExitCode != null) {
                if (String.valueOf(theExitCode).equals(expectedExitCode)) {
                    System.exit(0); // exit codes match
                } else {
                    System.err.println("Expected exit code is " + expectedExitCode + ", but the actual one is " + theExitCode);
                    System.exit(1); // exit codes did not match
                }
            }
        } catch (AccessControlException e) {
            // nop
        }
        System.exit(theExitCode);
    }

    public static void runChecker(Class<? extends Checker> checkerClass, String[] args) {
        runChecker(instantiateChecker(checkerClass), args);
    }

    @SuppressWarnings("WeakerAccess")
    public static void runChecker(Checker checker, String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: <input_file> <output_file> <answer_file> [<result_file> [<test_system_args>]]");
            System.exit(3);
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }
        run(args, 0, checker);
    }
}
