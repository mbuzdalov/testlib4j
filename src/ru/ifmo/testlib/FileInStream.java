package ru.ifmo.testlib;

import java.io.File;
import java.io.FileInputStream;
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
 * @author Andrey Plotnikov (Shemplo)
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

    public void reset () {
        try {
            if (source != null) {
                source.close ();
            }
            source = new CharSource (new FileInputStream (file));
        } catch (IOException ex) {
            // The output file might not exist, because the participant is "evil".
            throw quit (Outcome.Type.PE, "File not found: " + ex.toString ());
        }
    }
	
}
