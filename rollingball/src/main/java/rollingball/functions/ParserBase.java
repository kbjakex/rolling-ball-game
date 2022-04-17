package rollingball.functions;

public abstract class ParserBase<T> {
    public static record ParseResult<T>(T value, int nextCharIdx) {}

    protected char[] src;
    protected int srcPos;

    protected abstract T doParse();

    public final ParseResult<T> parse(char[] src, int startIdx) {
        this.src = src;
        this.srcPos = startIdx;

        var value = doParse();

        return new ParseResult<>(value, srcPos);
    }

    protected void expect(char type, String format, Object... args) {
        if (!consume(type)) {
            throw new ParserException(format, args);
        }
    }

    protected boolean consume(char type) {
        if (nextIs(type)) {
            srcPos += 1;
            return true;
        }
        return false;
    }

    protected boolean nextIs(char type) {
        return srcPos < src.length && src[srcPos] == type;
    }

    protected boolean hasNext() {
        return srcPos < src.length;
    }

}
