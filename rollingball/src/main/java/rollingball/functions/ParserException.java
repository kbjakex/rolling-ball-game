package rollingball.functions;

/**
 * Exception thrown when the parser encounters a syntax error.
 */
public final class ParserException extends RuntimeException {
    /**
     * Constructs a new ParserException with the specified detail message.
     *
     * @param message a human-readable detail message.
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Constructs a new ParserException with the specified detail message.
     * This is a convenience constructor for <code>new ParserException{String.format(format, args)}</code>.
     * 
     * @param format the format for a human-readable detail message.
     * @param args the formatting parameters.
     */
    public ParserException(String format, Object... args) {
        this(String.format(format, args));
    }
}