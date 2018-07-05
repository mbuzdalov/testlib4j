import ru.ifmo.testlib.Checker;
import ru.ifmo.testlib.InStream;
import ru.ifmo.testlib.Outcome;

public final class YesNo implements Checker {
    private String nextAnswer(InStream stream) {
        String token = stream.nextToken();
        if (!token.equals("YES") && !token.equals("NO")) {
            throw stream.quit(Outcome.Type.PE, "Expected 'YES' or 'NO', found '%s'", token);
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
}
