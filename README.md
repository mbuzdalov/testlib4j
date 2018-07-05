# testlib4j

## Introduction

Testlib is the name for a library which is used to create checkers.
Checkers are programs which validate the output of solutions for problems in programming competitions.

Originally, Testlib was written in Pascal.
A modern implementation was done in C++ by Mike Mirzayanov,
which is available [on GitHub](https://github.com/MikeMirzayanov/testlib).

In this repository, an implementation in Java is made to do the same thing.
This implementation was used in recent [NEERC semifinals](http://neerc.ifmo.ru).

## How to build

We use Ant due to its simplicity.

* Run `ant compile-src`, or simply `ant`, and `testlib4j.jar` will be created in the `deploy` directory.
* Run `and compile-examples`, and all the example checkers from the `examples` directory will be compiled and put into the `deploy/examples` directory.
* Run `ant clean` to remove all JARs and intermediate build files.

## How to implement your checker

Every checker should implement the `ru.ifmo.testlib.Checker` interface.
There is a single method to implement, namely,
`Outcome test(InStream inf, InStream ouf, InStream ans)`,
where the arguments of type `ru.ifmo.testlib.InStream` correspond to
the input file, the output file and the answer file, correspondingly.

In this method, you do everything you normally do within a checker.
For example, you can read the answer from the contestant's output file (the `ouf` parameter),
the correct answer from the answer file (the `ans` parameter)
and compare them for equality. You can also read the input file (the `inf` parameter)
and validate the contestant's output against this input.

You need to return the `Outcome`, which is your verdict about correctness of the
contestant's output. There are various types of outcomes, reflected by the `Outcome.Type` enum:

* `Outcome.Type.OK`: everything is OK, and the contestant's output is correct.
* `Outcome.Type.PE`: "Presentation Error", the output cannot be parsed.
* `Outcome.Type.WA`: "Wrong Answer", the output is understandable but wrong.
* `Outcome.Type.FAIL`: something is really bad; either the files are totally wrong, or things are so bad that someone needs to rewrite the checker.

When needed, you can create and return instances of `Outcome` directly, for instance:
`return new Outcome(OK, "This is a brilliant answer")`.
`Outcome`s are also `Throwable`s, so you can also `throw new Outcome(WA, "Bad guys receive no gifts today")`.
There is a convenient shorthand `Outcome.quit(Outcome.Type, String, Object...)`
for immediately quitting from anywhere with the given outcome,
the format string and its arguments, for instance:

```
throw/return Outcome.quit(WA, "Expected %d found %d", 42, 26);
```

If your checker consists of a validation procedure which needs to be run both for the contestant's answer and for the jury's answer,
you can use the `InStream.quit(Outcome.Type, String, Object...)` method to achieve the same aim. The framework will ensure, however,
that when you are processing the jury's answer, every error will manifest itself as a `FAIL`, which is what you need.

## How to run your checker

Once you have compiled your checker (we assume the class name is `MyChecker`,
it is located in the default package, and the class files are packed in
a file called `Check.jar`), you can run it as follows:

```
java -cp Check.jar:testlib4j.jar ru.ifmo.testlib.CheckerFramework MyChecker <input> <output> <answer>
```

where `testlib4j.jar` is the JAR file containing classes from this project.
On Windows, you need to separate JAR files with `;`, for instance:
`java -cp Check.jar;testlib4j.jar`.

For your convenience, you can put the checker's class name in the manifest of one of the JAR files,
so you can drop it in the command line. The corresponding attribute is called `Checker-Class`. 
The example manifest file is below:

```
Manifest-Version: 1.0
Ant-Version: Apache Ant 1.9.1
Created-By: 1.8.0_172-b11 (Oracle Corporation)
Checker-Class: MyChecker
```

## Examples

This is maybe the simplest possible checker.
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
            return new Outcome(WA, "The answer is " + answer + ", but you output " + output);
        } else {
            return new Outcome(OK, "OK: " + answer);
        }
    }
}
```
