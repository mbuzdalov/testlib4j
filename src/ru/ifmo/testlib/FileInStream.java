package ru.ifmo.testlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import ru.ifmo.testlib.Outcome.Type;

/**
 * A file-based implementation of the {@link InStream} interface.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 * @author Shemplo
 */
public class FileInStream extends InputStreamInStream {
	
    /** A file to read data from. */
	private final File file;

	/**
	 * Creates new {@link InStream} for specified file and with the specified outcome mapping.
	 *
	 * @param file a file to read data from
	 */
	public FileInStream (File file, Map <Type, Type> outcomeMapping) {
	    super (null, outcomeMapping);
	    this.file = file;
	    reset ();
	}

	public void setOutcomeMapping(Outcome.Type from, Outcome.Type to) {
		outcomeMapping.put(from, to);
	}

    public void reset () {
        try {
            if (reader != null) {
                reader.close ();
            }
            reader = new BufferedReader (new FileReader (file));
        } catch (IOException ex) {
            // The output file might not exist, because the participant is "evil".
            throw quit (Outcome.Type.PE, "File not found: " + ex.toString ());
        }
        nextChar ();
    }
	
}
