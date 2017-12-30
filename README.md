# testlib4j

## Introduction

Testlib is the name for a library which is used to create checkers.
Checkers are programs which validate the output of solutions for problems in programming competitions.

Originally, Testlib was written in Pascal.
A modern implementation was done in C++ by Mike Mirzayanov,
which is available [on GitHub](https://github.com/MikeMirzayanov/testlib).

In this repository, an implementation in Java is made to do the same thing.
This implementation was used in recent [NEERC semifinals](http://neerc.ifmo.ru).

## Examples

This is the simplest checker.
It checks whether the output file produced by the contest participant
contains the single integer which is the same as the right answer. 

```
import ru.ifmo.testlib.*;
import static ru.ifmo.testlib.Outcome.Type.*;

public class CompareNumber implements Checker {
    public Outcome test(InStream inf, InStream ouf, InStream ans) {
        long answer = ans.nextLong();
        long output = ouf.nextLong();
        if (answer != output) {
            return new Outcome(WA,
                "The answer is " + answer + ", but you output " + output);
        } else {
            return new Outcome(OK,
                "OK: " + answer);
        }
    }
}
```
