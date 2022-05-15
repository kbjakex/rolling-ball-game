package rollingball.functions;

import rollingball.functions.Function.Condition;
import rollingball.functions.Function.Expr;
import rollingball.functions.Operators.RelationalOp;

public final class ConditionParser extends Parser<Condition> {

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
        var res = switch (first) {
            case '<' -> eq ? RelationalOp.LE : RelationalOp.LT;
            case '>' -> eq ? RelationalOp.GE : RelationalOp.GT;
            default -> null;
        };
        if (res == null) {
            srcPos -= eq ? 2 : 1;
        }
        return res;
    }

    private Expr parseExpr() {
        var result = exprParser.parse(this.src, this.srcPos);
        this.srcPos = result.nextCharIdx();
        return result.value();
    }

    private Condition parseCondition() {
        var lhs = parseExpr();
        var op = tryParseRelationalOp();
        if (op == null) {
            throw new ParserException("Expected a relational operator, found '%s'",
                    srcPos == src.length ? "(end of expression)" : ("" + src[srcPos]));
        }

        var rhs = parseExpr();
        var condition = packCondition(lhs, op, rhs);
        return parseComplexCondition(condition, rhs, op);
    }

    private Condition parseComplexCondition(Condition condition, Expr rhs, RelationalOp op) {
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
        return switch (op) {
            case LT -> ctx -> lhs.eval(ctx) < rhs.eval(ctx);
            case LE -> ctx -> lhs.eval(ctx) <= rhs.eval(ctx);
            case GT -> ctx -> lhs.eval(ctx) > rhs.eval(ctx);
            case GE -> ctx -> lhs.eval(ctx) >= rhs.eval(ctx);
        };
    }

}
