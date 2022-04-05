package rollingball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import rollingball.expressions.ExpressionParser;
import rollingball.expressions.ExpressionParser.ParserException;

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

}
