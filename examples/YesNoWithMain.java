import ru.ifmo.testlib.Checker;
import ru.ifmo.testlib.InStream;
import ru.ifmo.testlib.Outcome;

import static ru.ifmo.testlib.CheckerFramework.*;

public final class YesNoWithMain implements Checker {
    private String yes;
    private String no;

    private YesNoWithMain(String yes, String no) {
        this.yes = yes;
        this.no = no;
    }

    private String nextAnswer(InStream stream) {
        String token = stream.nextToken();
        if (!token.equals(yes) && !token.equals(no)) {
            throw stream.quit(Outcome.Type.PE, "Expected '%s' or '%s', found '%s'", yes, no, token);
        } else {
            return token;
        }
    }

    @Override
    public Outcome test(InStream inf, InStream ouf, InStream ans) {
        String expected = nextAnswer(ans);
        String found = nextAnswer(ouf);
        if (expected.equals(found)) {
            return new Outcome(Outcome.Type.OK, expected);
        } else {
            return Outcome.quit(Outcome.Type.WA, "Expected %s found %s", expected, found);
        }
    }

    public static void main(String[] args) {
        runChecker(new YesNoWithMain("YES", "NO"), args);
    }
}
