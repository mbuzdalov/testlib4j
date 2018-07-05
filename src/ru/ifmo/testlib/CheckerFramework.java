package ru.ifmo.testlib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
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
    private static final String SYS_EXIT_DISABLED = "System.exit(int) did not exit. Exiting abnormally.";
    private static final String USAGE =
            "Usage: [<verifier_classname>] <input_file> <output_file> <answer_file> [<result_file> [<test_system_args>]].\n" +
            "    The <verifier_classname> value may also be specified in MANIFEST.MF as Checker-Class attribute.";

    private static final String DEFAULT_RESULT_ADAPTER = "checker-type:ifmo";

    private final static String CHECKER_CLASS_ENTRY = "Checker-Class";

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

        Checker checker;
        try {
            checker = (Checker) (Class.forName(checkerClassName).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            fatal(e.getMessage());
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

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
            throw new RuntimeException(SYS_EXIT_DISABLED);
        }

        resultAdapter.initArgs(verifierArgs);

        Outcome outcome;
        try (InStream input = new FileInStream(new File(args[delta]), Outcome.nonOkayIsFail);
             InStream output = new FileInStream(new File(args[1 + delta]), Collections.emptyMap());
             InStream answer = new FileInStream(new File(args[2 + delta]), Outcome.nonOkayIsFail)) {
            outcome = checker.test(input, output, answer);
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
        result.close();

        System.exit(resultAdapter.getExitCodeFor(outcome));
    }
}
