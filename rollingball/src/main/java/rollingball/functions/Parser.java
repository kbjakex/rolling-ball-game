package rollingball.functions;

/**
 * Represents a common base class for all parsers in the package.
 */
public abstract sealed class Parser<T> permits ExpressionParser, ConditionParser {
    /**
     * Represents the result of a parsing operation.
     * @param value the parsed value.
     * @param nextCharIdx the index of the next unconsumed character in the source string.
     */
    public static record ParseResult<T>(T value, int nextCharIdx) { }

    /** 
     * source string being parsed
     */
    protected char[] src;
    /**
     * index of the next unconsumed character in the source string
     */
    protected int srcPos;

    /**
     * Parses the specified source string.
     * @return the result of the parsing operation.
     */
    protected abstract T doParse();

    /**
     * Parses the specified source string.
     * @param src the source string to parse.
     * @param startIdx the index of the first character to parse.
     * @return the result of the parsing operation, or null if the string was empty.
     * @throws ParserException if the source string has invalid syntax.
     */
    public final ParseResult<T> parse(char[] src, int startIdx) {
        if (src.length - startIdx <= 0) {
            return new ParseResult<>(null, 0);
        }

        this.src = src;
        this.srcPos = startIdx;

        var value = doParse();

        return new ParseResult<>(value, srcPos);
    }

    /**
     * Expects the next character to be the specified one, or throws an exception if it is not.
     * Advances the source position by one.
     * @param type the expected character.
     * @param format the format for a human-readable detail message.
     * @param args the formatting parameters.
     */
    protected void expect(char type, String format, Object... args) {
        if (!consume(type)) {
            throw new ParserException(format, args);
        }
    }

    /**
     * Consumes the next character if it is the specified one.
     * @param type the expected character.
     * @return true if the character was consumed, false otherwise.
     */
    protected boolean consume(char type) {
        if (nextIs(type)) {
            srcPos += 1;
            return true;
        }
        return false;
    }

    /**
     * Checks if the next character is the specified one. Does not modify state.
     * @param type the expected character.
     * @return true if the next character is the specified one, false otherwise.
     */
    protected boolean nextIs(char type) {
        return srcPos < src.length && src[srcPos] == type;
    }

    /**
     * Checks if there are more characters to be parsed.
     * @return true if there are more characters to be parsed, false otherwise.
     */
    protected boolean hasNext() {
        return srcPos < src.length;
    }

}
