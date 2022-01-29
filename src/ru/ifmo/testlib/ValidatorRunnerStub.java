package ru.ifmo.testlib;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Place this class (renamed to just ValidatorRunner) to sources files in polygon.
 * In validator tab choose this file as validator entry point.
 * 
 * Your actual validator code should be placed in `Validate.java` file (no package)
 * in resource files of polygon. That class should implements {@link Validator} interface.
 * 
 * @author Shemplo
 *
 */
public class ValidatorRunnerStub {
    
    public static void main (String ... args) throws Throwable {
        final var path = List.of ("ru", "ifmo", "testlib", "CheckerFramework");
        final var type = Class.forName (path.stream ().collect (Collectors.joining (".")));
        final var method = type.getMethod ("main", String [].class);
        try {            
            method.invoke (null, ((Object) args));
        } catch (InvocationTargetException ite) {
            throw ite.getCause ();
        }
    }
    
}
