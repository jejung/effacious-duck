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
 * A console based log handler with a simple and specific format.
 * 
 * @author Jean Jung
 */
public class CrawlerLogHandler extends Handler {
	
	private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

	/**
	 * Actually, it does nothing, because we cannot close the stderr or stdout.
	 */
	@Override
	public void close() throws SecurityException {
	}

	/**
	 * Flushes the two possible outputs for this handler, {@link System#out} and {@link System#err}.
	 */
	@Override
	public void flush() {
		System.out.flush();
		System.err.flush();
	}

	/**
	 * Write the log record on the console, it can be the {@link System#out} or {@link System#err} {@link PrintStream}s.
	 * If the {@link LogRecord} define a exception cause, the stack trace will be printed on the {@link PrintStream}.
	 */
	@Override
	public void publish(LogRecord record) {
		PrintStream stream = null;
		
		if (record.getLevel().intValue() > Level.INFO.intValue())
			stream = System.err;
		else
			stream = System.out;
		
		stream.format("THREAD %d - %s %s : %s\n",
			record.getThreadID(),
			record.getLevel().toString(),
			DATE_FORMAT.format(new Date(record.getMillis())),
			record.getMessage());
		
		if (record.getThrown() != null) {
			record.getThrown().printStackTrace(stream);
		}
	}
}