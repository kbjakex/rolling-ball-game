package rollingball.functions;

import rollingball.functions.Function.Condition;
import rollingball.functions.Operators.RelationalOp;

public final class ConditionParser extends ParserBase<Condition> {

    @Override
    protected final Condition doParse() {
        return parseCondition();
    }

    private RelationalOp tryParseRelationalOp() {
        if (!hasNext()) {
            return null;
        }

        var first = src[srcPos++];
        var eq = consume('=');
        return switch (first) {
            case '<' -> eq ? RelationalOp.LE : RelationalOp.LT;
            case '>' -> eq ? RelationalOp.GE : RelationalOp.GT;
            case '=' -> eq ? RelationalOp.EQ : null;
            case '!' -> eq ? RelationalOp.NE : null;
            default -> {
                srcPos--;
                if (eq) srcPos --;
                yield null;
            }
        };
    }

    private Condition parseCondition() {
        var exprParser = new ExpressionParser();

        var lhsResult = exprParser.parse(this.src, this.srcPos);
        var lhs = lhsResult.value();
        this.srcPos = lhsResult.nextCharIdx();

        var op = tryParseRelationalOp();
        
        var rhsResult = exprParser.parse(this.src, this.srcPos);
        var rhs = rhsResult.value();
        this.srcPos = rhsResult.nextCharIdx();

        return switch(op) {
            case LT -> ctx -> lhs.eval(ctx) < rhs.eval(ctx);
            case LE -> ctx -> lhs.eval(ctx) <= rhs.eval(ctx);
            case GT -> ctx -> lhs.eval(ctx) > rhs.eval(ctx);
            case GE -> ctx -> lhs.eval(ctx) >= rhs.eval(ctx);
            case EQ -> ctx -> lhs.eval(ctx) == rhs.eval(ctx);
            case NE -> ctx -> lhs.eval(ctx) != rhs.eval(ctx);
            default -> {
                throw new ParserException("Invalid relational operator");
            }
        };
    }

}
