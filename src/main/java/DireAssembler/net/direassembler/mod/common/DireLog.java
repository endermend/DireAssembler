package DireAssembler.net.direassembler.mod.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DireLog {
	public static final Logger log = LogManager.getLogger("Dire Assembler");

	/**
	 * Logs an exception.
	 *
	 * @param e
	 * @param format
	 * @param data
	 */
	public static void error(final Throwable e, final String format, final Object... data) {
		log.error(String.format(format, data), e);
	}

	/**
	 * Logs basic info.
	 *
	 * @param format
	 * @param data
	 */
	public static void info(final String format, final Object... data) {
		log.info(String.format(format, data));
	}

	/**
	 * Logs an error.<br>
	 * If there is an exception available, use {@code error}.
	 *
	 * @param format
	 * @param data
	 */
	public static void severe(final String format, final Object... data) {
		log.error(String.format(format, data));
	}

	/**
	 * Logs a warning.
	 *
	 * @param format
	 * @param data
	 */
	public static void warning(final String format, final Object... data) {
		log.warn(String.format(format, data));
	}
}
