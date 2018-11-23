import ru.ifmo.testlib.Checker;
import ru.ifmo.testlib.InStream;
import ru.ifmo.testlib.Outcome;

import static ru.ifmo.testlib.CheckerFramework.*;
import static ru.ifmo.testlib.Outcome.Type.OK;
import static ru.ifmo.testlib.Outcome.Type.WA;

public final class CompareIntegerWithMain implements Checker {
    public Outcome test(InStream inf, InStream ouf, InStream ans) {
        long answer = ans.nextLong();
        long output = ouf.nextLong();
        if (answer != output) {
            return new Outcome(WA, "The answer is " + answer + ", but you output " + output);
        } else {
            return new Outcome(OK, "OK: " + answer);
        }
    }

    public static void main(String[] args) {
        runChecker(CompareIntegerWithMain.class, args);
    }
}
