import ru.ifmo.testlib.*;
import static ru.ifmo.testlib.Outcome.Type.*;

public final class CompareNumber implements Checker {
    public Outcome test(InStream inf, InStream ouf, InStream ans) {
        long answer = ans.nextLong();
        long output = ouf.nextLong();
        if (answer != output) {
            return new Outcome(WA, "The answer is " + answer + ", but you output " + output);
        } else {
            return new Outcome(OK, "OK: " + answer);
        }
    }
}
