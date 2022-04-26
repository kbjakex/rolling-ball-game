package rollingball.functions;

import rollingball.functions.Function.Condition;
import rollingball.functions.Function.Expr;
import rollingball.functions.Operators.RelationalOp;

public final class ConditionParser extends ParserBase<Condition> {

    private final ExpressionParser exprParser;

    public ConditionParser() {
        this.exprParser = new ExpressionParser();
    }

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

    private Expr parseExpr() {
        var result = exprParser.parse(this.src, this.srcPos);
        this.srcPos = result.nextCharIdx();
        return result.value();
    }

    private Condition parseCondition() {
        var lhs = parseExpr();
        var op = tryParseRelationalOp();
        var rhs = parseExpr();

        var condition = packCondition(lhs, op, rhs);

        while (true) {
            var nextOp = tryParseRelationalOp();
            if (nextOp == null) {
                break;
            }

            var nextRhs = parseExpr();
            var leftCondition = condition;
            var rightCondition = packCondition(rhs, nextOp, nextRhs);
            condition = ctx -> leftCondition.eval(ctx) && rightCondition.eval(ctx);
            rhs = nextRhs;
        }

        return condition;
    }

    private Condition packCondition(Expr lhs, RelationalOp op, Expr rhs) {
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
