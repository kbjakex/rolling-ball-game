package rollingball.functions;

public final class ParserException extends RuntimeException {
    public ParserException(String message) {
        super(message);
    }

    public ParserException(String format, Object... args) {
        this(String.format(format, args));
    }
}