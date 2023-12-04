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

import obp2.language.lregexp.model.LRegExp2TGF;
import obp2.language.lregexp.model.LRegExpCharReader;
import obp2.language.lregexp.model.LRegExp;
import obp2.language.lregexp.model.StringIterator;
import org.junit.Test;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.identityHashCode;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ReaderTest {

    @Test
    public void testToken() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("a");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);

        assertThat(expression, instanceOf(LRegExp.Token.class));
        assertEquals('a', ((LRegExp.Token<Character>) expression).token.charValue());
    }

    @Test
    public void testSpecialNotToken() {
        //ch == '|'
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("|");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        assertNull(expression);

        //ch == '*'
        iterator = new StringIterator("*");
        expression = reader.readExpression(iterator);
        assertNull(expression);

        //ch == '.'
        iterator = new StringIterator(".");
        expression = reader.readExpression(iterator);
        assertNull(expression);

        //ch == '('
        iterator = new StringIterator("(");
        expression = reader.readExpression(iterator);
        assertNull(expression);

        //ch == ')'
        iterator = new StringIterator(")");
        expression = reader.readExpression(iterator);
        assertNull(expression);

        //ch == '∅'
        iterator = new StringIterator("∅");
        expression = reader.readExpression(iterator);
        assertThat(expression, instanceOf(LRegExp.Empty.class));

        //ch == 'ε'
        iterator = new StringIterator("ε");
        expression = reader.readExpression(iterator);
        assertThat(expression, instanceOf(LRegExp.Epsilon.class));
    }

    @Test
    public void testKleeneStar1() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("a*");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        assertThat(expression, instanceOf(LRegExp.KleeneStar.class));
        assertEquals(new LRegExp.Token<>('a'), ((LRegExp.KleeneStar<Character>) expression).operands.get(0));
    }

    @Test
    public void testUnion1() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("a|b");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        assertThat(expression, instanceOf(LRegExp.Union.class));
        assertEquals(new LRegExp.Token<>('a'), ((LRegExp.Composite<Character>) expression).operands.get(0));
        assertEquals(new LRegExp.Token<>('b'), ((LRegExp.Composite<Character>) expression).operands.get(1));
    }

    @Test
    public void testConcat1() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("a.b");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        assertThat(expression, instanceOf(LRegExp.Concatenation.class));
        assertEquals(new LRegExp.Token<>('a'), ((LRegExp.Composite<Character>) expression).operands.get(0));
        assertEquals(new LRegExp.Token<>('b'), ((LRegExp.Composite<Character>) expression).operands.get(1));
    }

    @Test
    public void testConcat2() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("ab");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);

        LRegExp.Expression<Character> expected = new LRegExp.Concatenation<>(
                new LRegExp.Token<>('a'),
                new LRegExp.Token<>('b'));

        assertEquals(expected, expression);
    }

    @Test
    public void testParensToken() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("(a)");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        assertThat(expression, instanceOf(LRegExp.Token.class));
        assertEquals(new LRegExp.Token<>('a'), expression);
    }

    @Test
    public void testParensUnion() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("(a|b)");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        LRegExp.Expression<Character> expected = new LRegExp.Union<>(new LRegExp.Token<>('a'), new LRegExp.Token<>('b'));
        assertEquals(expected, expression);
    }

    @Test
    public void testParensParens() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("(a|(b.c))");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        LRegExp.Expression<Character> expected =
                new LRegExp.Union<>(
                        new LRegExp.Token<>('a'),
                        new LRegExp.Concatenation<>(
                                new LRegExp.Token<>('b'),
                                new LRegExp.Token<>('c')));
        assertEquals(expected, expression);

        iterator = new StringIterator("(a|(bc))");
        expression = reader.readExpression(iterator);
        assertEquals(expected, expression);
//        System.out.println(LRegExp2TGF.convert(expression));
    }

    @Test
    public void testPrio() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("(a|bc)");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        LRegExp.Expression<Character> expected =
                new LRegExp.Union<>(
                        new LRegExp.Token<>('a'),
                        new LRegExp.Concatenation<>(
                                new LRegExp.Token<>('b'),
                                new LRegExp.Token<>('c')));
        assertEquals(expected, expression);
//        System.out.println(LRegExp2TGF.convert(expression));
    }

    @Test
    public void testConcatParens() {
        LRegExpCharReader reader = new LRegExpCharReader();
        StringIterator iterator = new StringIterator("(a)ε");
        LRegExp.Expression<Character> expression = reader.readExpression(iterator);
        LRegExp.Expression<Character> expected =
                new LRegExp.Concatenation<>(
                        new LRegExp.Token<>('a'),
                        new LRegExp.Epsilon<>());
        assertEquals(expected, expression);
    }
}
