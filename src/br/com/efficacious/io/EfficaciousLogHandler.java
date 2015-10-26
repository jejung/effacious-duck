/**
 * 
 */
package br.com.efficacious.io;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Jean Jung
 *
 */
public class EfficaciousLogHandler extends Handler {
	
	private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

	/**
	 * Actually, it does nothing.
	 */
	@Override
	public void close() throws SecurityException {	
	}

	/**
	 * Flushes the two possible outputs for this handler,
	 * {@link System#out} and {@link System#err}
	 */
	@Override
	public void flush() {
		System.out.flush();
		System.err.flush();
	}

	/**
	 * Write on the console.
	 */
	@Override
	public void publish(LogRecord record) {
		PrintStream stream = null;
		
		if (record.getLevel().intValue() > Level.INFO.intValue())
			stream = System.err;
		else
			stream = System.out;
		
		stream.format("%s %s : %s\n",
			record.getLevel().toString(),
			DATE_FORMAT.format(new Date(record.getMillis())),
			record.getMessage());
		
		if (record.getThrown() != null) {
			record.getThrown().printStackTrace(stream);
		}
	}
}