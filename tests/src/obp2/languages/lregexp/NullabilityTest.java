/*
 * MIT License
 *
 * Copyright (c) 2023 Ciprian Teodorov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package obp2.languages.lregexp;

import obp2.language.lregexp.model.LRegExp;
import obp2.language.lregexp.model.LRegExpCharReader;
import obp2.language.lregexp.model.LRegExpNullability;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NullabilityTest {

    LRegExpCharReader reader = new LRegExpCharReader();
    LRegExpNullability<Character> nullability = new LRegExpNullability<>();
    @Test
    public void testEmpty() {
        LRegExp.Expression<Character> expression = reader.readExpression("∅");
        LRegExp.Expression<Character> derivative = expression.accept(nullability, null);
        LRegExp.Expression<Character> expected = reader.readExpression("∅");
        assertEquals(expected, derivative);
    }

    @Test
    public void testEpsilon() {
        LRegExp.Expression<Character> expression = reader.readExpression("ε");
        LRegExp.Expression<Character> derivative = expression.accept(nullability, null);
        LRegExp.Expression<Character> expected = reader.readExpression("ε");
        assertEquals(expected, derivative);
    }

    @Test
    public void testToken() {
        LRegExp.Expression<Character> expression = reader.readExpression("a");
        LRegExp.Expression<Character> derivative = expression.accept(nullability, null);
        LRegExp.Expression<Character> expected = reader.readExpression("∅");
        assertEquals(expected, derivative);
    }

    @Test
    public void testUnion() {
        LRegExp.Expression<Character> expression = reader.readExpression("a|b");
        LRegExp.Expression<Character> derivative = expression.accept(nullability, null);
        LRegExp.Expression<Character> expected = reader.readExpression("∅|∅");
        assertEquals(expected, derivative);

        expression = reader.readExpression("ε|b");
        derivative = expression.accept(nullability, null);
        expected = reader.readExpression("ε|∅");
        assertEquals(expected, derivative);

        expression = reader.readExpression("a|ε");
        derivative = expression.accept(nullability, null);
        expected = reader.readExpression("∅|ε");
        assertEquals(expected, derivative);
    }

    @Test
    public void testConcat() {
        LRegExp.Expression<Character> expression = reader.readExpression("ab");
        LRegExp.Expression<Character> derivative = expression.accept(nullability, null);
        LRegExp.Expression<Character> expected = reader.readExpression("∅∅");
        assertEquals(expected, derivative);

        expression = reader.readExpression("εb");
        derivative = expression.accept(nullability, null);
        expected = reader.readExpression("ε∅");
        assertEquals(expected, derivative);

        expression = reader.readExpression("aε");
        derivative = expression.accept(nullability, null);
        expected = reader.readExpression("∅ε");
        assertEquals(expected, derivative);
    }
    @Test
    public void testKleeneStar() {
        LRegExp.Expression<Character> expression = reader.readExpression("a*");
        LRegExp.Expression<Character> derivative = expression.accept(nullability, null);
        LRegExp.Expression<Character> expected = reader.readExpression("ε");
        assertEquals(expected, derivative);
    }
}
