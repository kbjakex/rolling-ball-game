package rollingball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import rollingball.expressions.ExpressionParser;
import rollingball.expressions.ExpressionParser.ParserException;
import rollingball.expressions.Expressions.EvalContext;

public class ExpressionParserTest {
    // doubles aren't exact, but on top of that, the double parser is definitely not exact, so use an epsilon 
    private static final double EPSILON = 0.00001;

    @Test
    public void testWhitespaceIsRemovedCorrectly() {
        assertEquals("a+b+c+d", String.valueOf(ExpressionParser.removeWhitespace("    a +  b   +c\t+d    ")));
        assertEquals("", String.valueOf(ExpressionParser.removeWhitespace("")));
        assertEquals("", String.valueOf(ExpressionParser.removeWhitespace("    ")));
    }

    @Test
    public void testSimpleConstantsAreParsedCorrectly() {
        // Expressions without variables don't require a EvalContext, so a null parameter is fine
        assertEquals(1.0, ExpressionParser.parse("1.0").evaluate(null), EPSILON);
        assertEquals(-1.0, ExpressionParser.parse("-1.0").evaluate(null), EPSILON);
        assertEquals(1.0, ExpressionParser.parse("1").evaluate(null), EPSILON);
        assertEquals(.5, ExpressionParser.parse(".5").evaluate(null), EPSILON);
        assertEquals(-.5, ExpressionParser.parse("-.5").evaluate(null), EPSILON);
    }

    @Test
    public void testMultiDigitConstantsAreParsedCorrectly() {
        double tolerance = 0.0001;
        assertEquals(123, ExpressionParser.parse("123").evaluate(null), tolerance);
        assertEquals(0.12345, ExpressionParser.parse("0.12345").evaluate(null), tolerance);
        assertEquals(123.123, ExpressionParser.parse("123.123").evaluate(null), tolerance);
    }

    @Test
    public void testEmptyConstantThrows() {
        assertThrows(ParserException.class, () -> ExpressionParser.parse("1.0+")); // empty constant after +
    }

    @Test
    public void testEmptyExpressionReturnsNull() {
        assertEquals(null, ExpressionParser.parse(""));
    }

    @Test
    public void testBasicBinaryExpressionsWork() {
        assertEquals(2.0, ExpressionParser.parse("1 + 1").evaluate(null), EPSILON);
        assertEquals(-19.0, ExpressionParser.parse("1 - 20").evaluate(null), EPSILON);
        assertEquals(21.0, ExpressionParser.parse("3 * 7").evaluate(null), EPSILON);
        assertEquals(2.5, ExpressionParser.parse("5 / 2").evaluate(null), EPSILON);
    }

    @Test
    public void testTwoOperatorsWork() {
        assertEquals(111.0, ExpressionParser.parse("1 + 10 + 100").evaluate(null), EPSILON);
        assertEquals(1001.0, ExpressionParser.parse("1 + 10 * 100").evaluate(null), EPSILON);
        assertEquals(110.0, ExpressionParser.parse("1 * 10 + 100").evaluate(null), EPSILON);
        assertEquals(1000.0, ExpressionParser.parse("1 * 10 * 100").evaluate(null), EPSILON);
    }

    @Test
    public void testThreeOperatorsWork() {
        assertEquals(161.0, ExpressionParser.parse("1 + 10 + 50 + 100").evaluate(null), EPSILON);
        assertEquals(601.0, ExpressionParser.parse("1 + 10 * 50 + 100").evaluate(null), EPSILON);
        assertEquals(5010.0, ExpressionParser.parse("1 * 10 + 50 * 100").evaluate(null), EPSILON);
        assertEquals(50000.0, ExpressionParser.parse("1 * 10 * 50 * 100").evaluate(null), EPSILON);
    }

    @Test
    public void testBasicParenthesesWork() {
        assertEquals(-16.0, ExpressionParser.parse("(3 + 5) * (3 - 5)").evaluate(null), EPSILON);
        assertEquals(13.0, ExpressionParser.parse("((1+3) / 2.5) * 5 + 5").evaluate(null), EPSILON);
    }

    @Test
    public void testMismatchesParenthesesThrows() {
        assertThrows(ParserException.class, () -> ExpressionParser.parse("(3 + 5) * (3 - 5").evaluate(null));
    }

    @Test
    public void testVariablesRequireEvalContext() {
        assertThrows(RuntimeException.class, () -> ExpressionParser.parse("x").evaluate(null));
        assertThrows(RuntimeException.class, () -> ExpressionParser.parse("t").evaluate(null));
    }

    @Test
    public void testNamedConstantsAreConstEval() {
        assertEquals(Math.PI, ExpressionParser.parse("pi").evaluate(null));
        assertEquals(Math.PI, ExpressionParser.parse("PI").evaluate(null));
        assertEquals(Math.E, ExpressionParser.parse("e").evaluate(null));
        assertEquals(Math.E, ExpressionParser.parse("E").evaluate(null));
    }

    @Test
    public void testRuntimeVariablesWork() {
        EvalContext ctx = new EvalContext(1.0); // t = 1.0
        assertEquals(1.0, ExpressionParser.parse("t").evaluate(ctx));

        ctx.varX = 10.0;
        assertEquals(10.0, ExpressionParser.parse("x").evaluate(ctx));

        ctx.varX = -2.0;
        assertEquals(-1.0, ExpressionParser.parse("x + t").evaluate(ctx), EPSILON);
    }

    @Test
    public void testUnrecognizedVariablesThrow() {
        assertThrows(ParserException.class, () -> ExpressionParser.parse("a").evaluate(null));
    }

    @Test
    public void testMultiplicationSignCanBeOmittedBeforeIdentifier() {
        EvalContext ctx = new EvalContext(2.0);
        ctx.varX = 3*Math.PI/2;
        assertEquals(2*ctx.varX, ExpressionParser.parse("2x").evaluate(ctx), EPSILON);
        assertEquals(-3.0, ExpressionParser.parse("-3sin(3x)").evaluate(ctx), EPSILON);
    }

    @Test
    public void testSingleParamFunctionsWork() {
        assertEquals(Math.sin(3+5), ExpressionParser.parse("sin(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.cos(3+5), ExpressionParser.parse("cos(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.tan(3+5), ExpressionParser.parse("tan(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.asin(3+5), ExpressionParser.parse("asin(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.acos(3+5), ExpressionParser.parse("acos(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.atan(3+5), ExpressionParser.parse("atan(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.sinh(3+5), ExpressionParser.parse("sinh(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.cosh(3+5), ExpressionParser.parse("cosh(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.tanh(3+5), ExpressionParser.parse("tanh(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.exp(3+5), ExpressionParser.parse("exp(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.log(3+5), ExpressionParser.parse("log(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.log10(3+5), ExpressionParser.parse("log10(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.sqrt(3+5), ExpressionParser.parse("sqrt(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.cbrt(3+5), ExpressionParser.parse("cbrt(3+5)").evaluate(null), EPSILON);
        assertEquals(Math.abs(3-5), ExpressionParser.parse("abs(3-5)").evaluate(null), EPSILON);
        assertEquals(Math.floor(3.5), ExpressionParser.parse("floor(3.5)").evaluate(null), EPSILON);
        assertEquals(Math.ceil(3.5), ExpressionParser.parse("ceil(3.5)").evaluate(null), EPSILON);
        assertEquals(Math.round(3.49), ExpressionParser.parse("round(3.49)").evaluate(null), EPSILON);
        assertEquals(Math.round(3.5), ExpressionParser.parse("round(3.5)").evaluate(null), EPSILON);
        assertEquals(Math.signum(3-5), ExpressionParser.parse("sign(3-5)").evaluate(null), EPSILON);
        assertEquals(Math.signum(5-3), ExpressionParser.parse("signum(5-3)").evaluate(null), EPSILON);
    }

    @Test
    public void testMultiParamFunctionsWork() {
        assertEquals(Math.sqrt(3*3+5*5), ExpressionParser.parse("hypot(3, 5)").evaluate(null), EPSILON);
        assertEquals(Math.atan2(3,5), ExpressionParser.parse("atan2(3, 5)").evaluate(null), EPSILON);
        assertEquals(Math.min(3,5), ExpressionParser.parse("min(3, 5)").evaluate(null), EPSILON);
        assertEquals(Math.max(3,5), ExpressionParser.parse("max(3, 5)").evaluate(null), EPSILON);
        assertEquals(Math.pow(3,5), ExpressionParser.parse("pow(3, 5)").evaluate(null), EPSILON);
    }

    @Test
    public void testUnrecognizedFunctionThrows() {
        assertThrows(ParserException.class, () -> ExpressionParser.parse("random(3)").evaluate(null));
    }

    @Test
    public void testMissingParameterThrows() {
        assertThrows(ParserException.class, () -> ExpressionParser.parse("atan2(0)").evaluate(null));
        assertThrows(ParserException.class, () -> ExpressionParser.parse("atan2(0,)").evaluate(null));
    }

    @Test
    public void trailingContentsThrows() {
        assertThrows(ParserException.class, () -> ExpressionParser.parse("(5+3))").evaluate(null));
        assertThrows(ParserException.class, () -> ExpressionParser.parse("(5+3)3").evaluate(null));
        assertThrows(ParserException.class, () -> ExpressionParser.parse("(5+3)x").evaluate(null));
    }
}
