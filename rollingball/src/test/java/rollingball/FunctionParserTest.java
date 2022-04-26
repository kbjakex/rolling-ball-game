package rollingball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;

import rollingball.functions.EvalContext;
import rollingball.functions.FunctionParser;
import rollingball.functions.ParserException;

// TODO update tests to account for major refactorings
public class FunctionParserTest {
    // doubles aren't exact, but on top of that, the double parser is definitely not exact, so use an epsilon 
    private static final double EPSILON = 0.00001;

    @Test
    public void testWhitespaceIsRemovedCorrectly() {
        assertEquals("a+b+c+d", String.valueOf(FunctionParser.removeWhitespace("    a +  b   +c\t+d    ")));
        assertEquals("", String.valueOf(FunctionParser.removeWhitespace("")));
        assertEquals("", String.valueOf(FunctionParser.removeWhitespace("    ")));
    }

    @Test
    public void testSimpleConstantsAreParsedCorrectly() {
        // Expressions without variables don't require a EvalContext, so a null parameter is fine
        assertEquals(1.0, FunctionParser.parse("1.0", "").eval(null), EPSILON);
        assertEquals(-1.0, FunctionParser.parse("-1.0", "").eval(null), EPSILON);
        assertEquals(1.0, FunctionParser.parse("1", "").eval(null), EPSILON);
        assertEquals(.5, FunctionParser.parse(".5", "").eval(null), EPSILON);
        assertEquals(-.5, FunctionParser.parse("-.5", "").eval(null), EPSILON);
    }

    @Test
    public void testMultiDigitConstantsAreParsedCorrectly() {
        double tolerance = 0.0001;
        assertEquals(123, FunctionParser.parse("123", "").eval(null), tolerance);
        assertEquals(0.12345, FunctionParser.parse("0.12345", "").eval(null), tolerance);
        assertEquals(123.123, FunctionParser.parse("123.123", "").eval(null), tolerance);
    }

    @Test
    public void testEmptyConstantThrows() {
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("1.0+", "").eval(null)); // empty constant after +
    }

    @Test
    public void testUnaryOperatorsWork() {
        assertEquals(-1.0, FunctionParser.parse("-(3-2)", "").eval(null), EPSILON);
        assertEquals(2.0, FunctionParser.parse("-(3-5)", "").eval(null), EPSILON);
    }

    @Test
    public void testDivByZero() {
        assertEquals(Double.POSITIVE_INFINITY, FunctionParser.parse("1/0", "").eval(null), EPSILON);
        assertEquals(Double.NEGATIVE_INFINITY, FunctionParser.parse("-1/0", "").eval(null), EPSILON);
        assertEquals(Double.NaN, FunctionParser.parse("0/0", "").eval(null), EPSILON);
    }

    @Test
    public void testBasicBinaryExpressionsWork() {
        assertEquals(2.0, FunctionParser.parse("1 + 1", "").eval(null), EPSILON);
        assertEquals(-19.0, FunctionParser.parse("1 - 20", "").eval(null), EPSILON);
        assertEquals(21.0, FunctionParser.parse("3 * 7", "").eval(null), EPSILON);
        assertEquals(2.5, FunctionParser.parse("5 / 2", "").eval(null), EPSILON);
    }

    @Test
    public void testTwoOperatorsWork() {
        assertEquals(111.0, FunctionParser.parse("1 + 10 + 100", "").eval(null), EPSILON);
        assertEquals(1001.0, FunctionParser.parse("1 + 10 * 100", "").eval(null), EPSILON);
        assertEquals(110.0, FunctionParser.parse("1 * 10 + 100", "").eval(null), EPSILON);
        assertEquals(1000.0, FunctionParser.parse("1 * 10 * 100", "").eval(null), EPSILON);
    }

    @Test
    public void testThreeOperatorsWork() {
        assertEquals(161.0, FunctionParser.parse("1 + 10 + 50 + 100", "").eval(null), EPSILON);
        assertEquals(601.0, FunctionParser.parse("1 + 10 * 50 + 100", "").eval(null), EPSILON);
        assertEquals(5010.0, FunctionParser.parse("1 * 10 + 50 * 100", "").eval(null), EPSILON);
        assertEquals(50000.0, FunctionParser.parse("1 * 10 * 50 * 100", "").eval(null), EPSILON);
    }

    @Test
    public void testBasicParenthesesWork() {
        assertEquals(-16.0, FunctionParser.parse("(3 + 5) * (3 - 5)", "").eval(null), EPSILON);
        assertEquals(13.0, FunctionParser.parse("((1+3) / 2.5) * 5 + 5", "").eval(null), EPSILON);
    }

    @Test
    public void testMismatchesParenthesesThrows() {
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("(3 + 5) * (3 - 5", "").eval(null));
    }

    @Test
    public void testVariablesRequireEvalContext() {
        assertThrows(RuntimeException.class, () -> FunctionParser.parse("x", "").eval(null));
        assertThrows(RuntimeException.class, () -> FunctionParser.parse("t", "").eval(null));
    }

    @Test
    public void testNamedConstantsAreConstEval() {
        assertEquals(Math.PI, FunctionParser.parse("pi", "").eval(null));
        assertEquals(Math.PI, FunctionParser.parse("PI", "").eval(null));
        assertEquals(Math.E, FunctionParser.parse("e", "").eval(null));
        assertEquals(Math.E, FunctionParser.parse("E", "").eval(null));
    }

    @Test
    public void testRuntimeVariablesWork() {
        EvalContext ctx = new EvalContext(1.0); // t = 1.0
        assertEquals(1.0, FunctionParser.parse("t", "").eval(ctx));
        
        ctx.x = 10.0;
        assertEquals(10.0, FunctionParser.parse("x", "").eval(ctx));

        ctx.x = -2.0;
        assertEquals(-1.0, FunctionParser.parse("t + x", "").eval(ctx), EPSILON);
    }

    @Test
    public void testUnrecognizedVariablesThrow() {
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("a", "").eval(null));
    }

    @Test
    public void testMultiplicationSignCanBeOmittedBeforeIdentifier() {
        EvalContext ctx = new EvalContext(2.0);
        ctx.x = 3*Math.PI/2;
        assertEquals(2*ctx.x, FunctionParser.parse("2x", "").eval(ctx), EPSILON);
        assertEquals(-3.0, FunctionParser.parse("-3sin(3x)", "").eval(ctx), EPSILON);
        assertEquals(12*Math.PI, FunctionParser.parse("(5+3)x", "").eval(ctx), EPSILON);
    }

    @Test
    public void testSingleParamFunctionsWork() {
        assertEquals(Math.sin(3+5), FunctionParser.parse("sin(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.cos(3+5), FunctionParser.parse("cos(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.tan(3+5), FunctionParser.parse("tan(3+5)", "").eval(null), EPSILON);
        
        assertEquals(Math.asin(3+5), FunctionParser.parse("asin(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.asin(3+5), FunctionParser.parse("arcsin(3+5)", "").eval(null), EPSILON);
        
        assertEquals(Math.acos(3+5), FunctionParser.parse("acos(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.acos(3+5), FunctionParser.parse("arccos(3+5)", "").eval(null), EPSILON);
        
        assertEquals(Math.atan(3+5), FunctionParser.parse("atan(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.atan(3+5), FunctionParser.parse("arctan(3+5)", "").eval(null), EPSILON);

        assertEquals(Math.sinh(3+5), FunctionParser.parse("sinh(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.cosh(3+5), FunctionParser.parse("cosh(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.tanh(3+5), FunctionParser.parse("tanh(3+5)", "").eval(null), EPSILON);

        assertEquals(Math.exp(3+5), FunctionParser.parse("exp(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.log(3+5), FunctionParser.parse("log(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.log(3+5), FunctionParser.parse("ln(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.log10(3+5), FunctionParser.parse("log10(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.log10(3+5), FunctionParser.parse("lg(3+5)", "").eval(null), EPSILON);
        
        assertEquals(Math.sqrt(3+5), FunctionParser.parse("sqrt(3+5)", "").eval(null), EPSILON);
        assertEquals(Math.cbrt(3+5), FunctionParser.parse("cbrt(3+5)", "").eval(null), EPSILON);
        
        assertEquals(Math.abs(3-5), FunctionParser.parse("abs(3-5)", "").eval(null), EPSILON);
        assertEquals(Math.signum(3-5), FunctionParser.parse("sign(3-5)", "").eval(null), EPSILON);
        assertEquals(Math.signum(5-3), FunctionParser.parse("signum(5-3)", "").eval(null), EPSILON);
        
        assertEquals(Math.floor(3.5), FunctionParser.parse("floor(3.5)", "").eval(null), EPSILON);
        assertEquals(Math.ceil(3.5), FunctionParser.parse("ceil(3.5)", "").eval(null), EPSILON);
        assertEquals(Math.round(3.49), FunctionParser.parse("round(3.49)", "").eval(null), EPSILON);
        assertEquals(Math.round(3.5), FunctionParser.parse("round(3.5)", "").eval(null), EPSILON);
    }

    @Test
    public void testMultiParamFunctionsWork() {
        assertEquals(Math.sqrt(3*3+5*5), FunctionParser.parse("hypot(3, 5)", "").eval(null), EPSILON);
        assertEquals(Math.atan2(3,5), FunctionParser.parse("atan2(3, 5)", "").eval(null), EPSILON);
        assertEquals(Math.min(3,5), FunctionParser.parse("min(3, 5)", "").eval(null), EPSILON);
        assertEquals(Math.max(3,5), FunctionParser.parse("max(3, 5)", "").eval(null), EPSILON);
        assertEquals(Math.pow(3,5), FunctionParser.parse("pow(3, 5)", "").eval(null), EPSILON);
    }

    @Test
    public void testUnrecognizedFunctionThrows() {
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("random(3)", "").eval(null));
    }

    @Test
    public void testMissingParameterThrows() {
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("atan2(0)", "").eval(null));
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("atan2(0,)", "").eval(null));
    }

    @Test
    public void trailingContentsThrows() {
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("(5+3))", "").eval(null));
        assertThrowsExactly(ParserException.class, () -> FunctionParser.parse("(5+3)3", "").eval(null));
    }
}
