package rollingball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rollingball.functions.EvalContext;
import rollingball.functions.ExpressionParser;
import rollingball.functions.FunctionParser;
import rollingball.functions.ParserException;
import rollingball.functions.Function.Expr;

// TODO update tests to account for major refactorings
public class FunctionParserTest {
    // doubles aren't exact, but on top of that, the double parser is definitely not exact, so use an epsilon 
    private static final double EPSILON = 0.00001;

    ExpressionParser parser;

    Expr parse(String input) {
        return parser.parse(input.toCharArray(), 0).value();
    }

    double parseAndEval(String input) {
        return parse(input).eval(null);
    }

    @BeforeEach
    public void setUp() {
        parser = new ExpressionParser();
    }

    @Test
    public void testWhitespaceIsRemovedCorrectly() {
        assertEquals("a+b+c+d", String.valueOf(FunctionParser.removeWhitespace("    a +  b   +c\t+d    ")));
        assertEquals("", String.valueOf(FunctionParser.removeWhitespace("")));
        assertEquals("", String.valueOf(FunctionParser.removeWhitespace("    ")));
    }

    @Test
    public void testSimpleConstantsAreParsedCorrectly() {
        // Expressions without variables don't require a EvalContext, so a null parameter is fine
        assertEquals(1.0, parseAndEval("1.0"), EPSILON);
        assertEquals(-1.0, parseAndEval("-1.0"), EPSILON);
        assertEquals(1.0, parseAndEval("1"), EPSILON);
        assertEquals(.5, parseAndEval(".5"), EPSILON);
        assertEquals(-.5, parseAndEval("-.5"), EPSILON);
    }

    @Test
    public void testMultiDigitConstantsAreParsedCorrectly() {
        double tolerance = 0.0001;
        assertEquals(123, parseAndEval("123"), tolerance);
        assertEquals(0.12345, parseAndEval("0.12345"), tolerance);
        assertEquals(123.123, parseAndEval("123.123"), tolerance);
    }

    @Test
    public void testEmptyConstantThrows() {
        assertThrowsExactly(ParserException.class, () -> parseAndEval("1.0+")); // empty constant after +
    }

    @Test
    public void testEmptyExpressionReturnsNull() {
        assertEquals(null, parseAndEval(""));
    }

    @Test
    public void testUnaryOperatorsWork() {
        assertEquals(-1.0, parseAndEval("-(3-2)"), EPSILON);
        assertEquals(2.0, parseAndEval("-(3-5)"), EPSILON);
    }

    @Test
    public void testDivByZero() {
        assertEquals(Double.POSITIVE_INFINITY, parseAndEval("1/0"), EPSILON);
        assertEquals(Double.NEGATIVE_INFINITY, parseAndEval("-1/0"), EPSILON);
        assertEquals(Double.NaN, parseAndEval("0/0"), EPSILON);
    }

    @Test
    public void testBasicBinaryExpressionsWork() {
        assertEquals(2.0, parseAndEval("1 + 1"), EPSILON);
        assertEquals(-19.0, parseAndEval("1 - 20"), EPSILON);
        assertEquals(21.0, parseAndEval("3 * 7"), EPSILON);
        assertEquals(2.5, parseAndEval("5 / 2"), EPSILON);
    }

    @Test
    public void testTwoOperatorsWork() {
        assertEquals(111.0, parseAndEval("1 + 10 + 100"), EPSILON);
        assertEquals(1001.0, parseAndEval("1 + 10 * 100"), EPSILON);
        assertEquals(110.0, parseAndEval("1 * 10 + 100"), EPSILON);
        assertEquals(1000.0, parseAndEval("1 * 10 * 100"), EPSILON);
    }

    @Test
    public void testThreeOperatorsWork() {
        assertEquals(161.0, parseAndEval("1 + 10 + 50 + 100"), EPSILON);
        assertEquals(601.0, parseAndEval("1 + 10 * 50 + 100"), EPSILON);
        assertEquals(5010.0, parseAndEval("1 * 10 + 50 * 100"), EPSILON);
        assertEquals(50000.0, parseAndEval("1 * 10 * 50 * 100"), EPSILON);
    }

    @Test
    public void testBasicParenthesesWork() {
        assertEquals(-16.0, parseAndEval("(3 + 5) * (3 - 5)"), EPSILON);
        assertEquals(13.0, parseAndEval("((1+3) / 2.5) * 5 + 5"), EPSILON);
    }

    @Test
    public void testMismatchesParenthesesThrows() {
        assertThrowsExactly(ParserException.class, () -> parseAndEval("(3 + 5) * (3 - 5"));
    }

    @Test
    public void testVariablesRequireEvalContext() {
        assertThrows(RuntimeException.class, () -> parseAndEval("x"));
        assertThrows(RuntimeException.class, () -> parseAndEval("t"));
    }

    @Test
    public void testNamedConstantsAreConstEval() {
        assertEquals(Math.PI, parseAndEval("pi"));
        assertEquals(Math.PI, parseAndEval("PI"));
        assertEquals(Math.E, parseAndEval("e"));
        assertEquals(Math.E, parseAndEval("E"));
    }

    @Test
    public void testRuntimeVariablesWork() {
        EvalContext ctx = new EvalContext(1.0); // t = 1.0
        assertEquals(1.0, parse("t").eval(ctx));
        
        ctx.x = 10.0;
        assertEquals(10.0, parse("x").eval(ctx));

        ctx.x = -2.0;
        assertEquals(-1.0, parse("x + t").eval(ctx), EPSILON);
    }

    @Test
    public void testUnrecognizedVariablesThrow() {
        assertThrowsExactly(ParserException.class, () -> parseAndEval("a"));
    }

    @Test
    public void testMultiplicationSignCanBeOmittedBeforeIdentifier() {
        EvalContext ctx = new EvalContext(2.0);
        ctx.x = 3*Math.PI/2;
        assertEquals(2*ctx.x, parse("2x").eval(ctx), EPSILON);
        assertEquals(-3.0, parse("-3sin(3x)").eval(ctx), EPSILON);
        assertEquals(12*Math.PI, parse("(5+3)x").eval(ctx), EPSILON);
    }

    @Test
    public void testSingleParamFunctionsWork() {
        assertEquals(Math.sin(3+5), parseAndEval("sin(3+5)"), EPSILON);
        assertEquals(Math.cos(3+5), parseAndEval("cos(3+5)"), EPSILON);
        assertEquals(Math.tan(3+5), parseAndEval("tan(3+5)"), EPSILON);
        
        assertEquals(Math.asin(3+5), parseAndEval("asin(3+5)"), EPSILON);
        assertEquals(Math.asin(3+5), parseAndEval("arcsin(3+5)"), EPSILON);
        
        assertEquals(Math.acos(3+5), parseAndEval("acos(3+5)"), EPSILON);
        assertEquals(Math.acos(3+5), parseAndEval("arccos(3+5)"), EPSILON);
        
        assertEquals(Math.atan(3+5), parseAndEval("atan(3+5)"), EPSILON);
        assertEquals(Math.atan(3+5), parseAndEval("arctan(3+5)"), EPSILON);

        assertEquals(Math.sinh(3+5), parseAndEval("sinh(3+5)"), EPSILON);
        assertEquals(Math.cosh(3+5), parseAndEval("cosh(3+5)"), EPSILON);
        assertEquals(Math.tanh(3+5), parseAndEval("tanh(3+5)"), EPSILON);

        assertEquals(Math.exp(3+5), parseAndEval("exp(3+5)"), EPSILON);
        assertEquals(Math.log(3+5), parseAndEval("log(3+5)"), EPSILON);
        assertEquals(Math.log(3+5), parseAndEval("ln(3+5)"), EPSILON);
        assertEquals(Math.log10(3+5), parseAndEval("log10(3+5)"), EPSILON);
        assertEquals(Math.log10(3+5), parseAndEval("lg(3+5)"), EPSILON);
        
        assertEquals(Math.sqrt(3+5), parseAndEval("sqrt(3+5)"), EPSILON);
        assertEquals(Math.cbrt(3+5), parseAndEval("cbrt(3+5)"), EPSILON);
        
        assertEquals(Math.abs(3-5), parseAndEval("abs(3-5)"), EPSILON);
        assertEquals(Math.signum(3-5), parseAndEval("sign(3-5)"), EPSILON);
        assertEquals(Math.signum(5-3), parseAndEval("signum(5-3)"), EPSILON);
        
        assertEquals(Math.floor(3.5), parseAndEval("floor(3.5)"), EPSILON);
        assertEquals(Math.ceil(3.5), parseAndEval("ceil(3.5)"), EPSILON);
        assertEquals(Math.round(3.49), parseAndEval("round(3.49)"), EPSILON);
        assertEquals(Math.round(3.5), parseAndEval("round(3.5)"), EPSILON);
    }

    @Test
    public void testMultiParamFunctionsWork() {
        assertEquals(Math.sqrt(3*3+5*5), parseAndEval("hypot(3, 5)"), EPSILON);
        assertEquals(Math.atan2(3,5), parseAndEval("atan2(3, 5)"), EPSILON);
        assertEquals(Math.min(3,5), parseAndEval("min(3, 5)"), EPSILON);
        assertEquals(Math.max(3,5), parseAndEval("max(3, 5)"), EPSILON);
        assertEquals(Math.pow(3,5), parseAndEval("pow(3, 5)"), EPSILON);
    }

    @Test
    public void testUnrecognizedFunctionThrows() {
        assertThrowsExactly(ParserException.class, () -> parseAndEval("random(3)"));
    }

    @Test
    public void testMissingParameterThrows() {
        assertThrowsExactly(ParserException.class, () -> parseAndEval("atan2(0)"));
        assertThrowsExactly(ParserException.class, () -> parseAndEval("atan2(0,)"));
    }

    @Test
    public void trailingContentsThrows() {
        assertThrowsExactly(ParserException.class, () -> parseAndEval("(5+3))"));
        assertThrowsExactly(ParserException.class, () -> parseAndEval("(5+3)3"));
    }
}
