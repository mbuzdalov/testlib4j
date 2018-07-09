import ru.ifmo.testlib.*;
import static ru.ifmo.testlib.Outcome.Type.*;

public final class CompareIntegers implements Checker {
    private int exhaust(InStream stream) {
        int rv = 0;
        while (!stream.seekEoF()) {
            stream.nextLong();
            ++rv;
        }
        return rv;
    }

    public Outcome test(InStream inf, InStream ouf, InStream ans) {
        int countMatchedNumbers = 0;
        while (!ouf.seekEoF() && !ans.seekEoF()) {
            long answer = ans.nextLong();
            long output = ouf.nextLong();
            if (answer != output) {
                return Outcome.quit(WA, "After %d matched number(s) the next pair differs: expected %d, found %d",
                    countMatchedNumbers, answer, output);
            }
            ++countMatchedNumbers;
        }

        int extraOuf = exhaust(ouf);
        int extraAns = exhaust(ans);

        if (extraOuf != 0) {
            return Outcome.quit(PE, "After %d matched number(s), the output contains %d extra elements", countMatchedNumbers, extraOuf);
        }
        if (extraAns != 0) {
            return Outcome.quit(PE, "After %d matched number(s), the output lacks %d more elements", countMatchedNumbers, extraAns);
        }
        return Outcome.quit(OK, countMatchedNumbers + " number(s)");
    }
}
