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

package obp2.language.lregexp.model;


/*
*
    E -> empty
    | epsilon
    | char
    | E u E
    | E . E
    | E *
    | (E)
---

    E -> empty E'
    | epsilon E'
    | char E'
    | (E) E'
    E' -> u E E'
    | . E E'
    | * E'
    | ϵ

* */

public class LRegExpCharReader {
    LRegExp.Expression<Character> context;

    public LRegExp.Expression<Character> readExpression(StringIterator iterator) {
        LRegExp.Expression<Character> expression = readEmpty(iterator);
        if (expression != null) {
            return getRemaining(iterator, expression);
        }
        expression = readEpsilon(iterator);
        if (expression != null) {
            return getRemaining(iterator, expression);
        }
        ;
        expression = readToken(iterator);
        if (expression != null) {
            return getRemaining(iterator, expression);
        }
        expression = readParens(iterator);
        if (expression != null) {
            return getRemaining(iterator, expression);
        }
        return null;
    }

    private LRegExp.Expression<Character> getRemaining(StringIterator iterator, LRegExp.Expression<Character> expression) {
        context = expression;
        expression = readExpressionPrim(iterator);
        if (expression == null) {
            expression = context;
        }
        context = null;
        return expression;
    }

    LRegExp.Expression<Character> readExpressionPrim(StringIterator iterator) {
        LRegExp.Expression<Character> expression = readConcatenation(iterator);
        if (expression != null) {
            return getRemaining(iterator, expression);
        }
        expression = readUnion(iterator);
        if (expression != null) {
            return getRemaining(iterator, expression);
        }
        expression = readKleeneStar(iterator);
        if (expression != null) {
            return getRemaining(iterator, expression);
        }
        if (iterator.hasNext()) return null;
        return context;
    }

    LRegExp.Expression<Character> readEmpty(StringIterator iterator) {
        if (!iterator.hasNext() || iterator.peek() != '∅') return null;
        iterator.advance();
        return new LRegExp.Empty<>();

    }

    LRegExp.Expression<Character> readEpsilon(StringIterator iterator) {
        if (!iterator.hasNext() || iterator.peek() != 'ε') return null;
        iterator.advance();
        return new LRegExp.Epsilon<>();
    }

    LRegExp.Expression<Character> readToken(StringIterator iterator) {
        if (!iterator.hasNext() || isSpecial(iterator.peek())) return null;
        Character ch = iterator.next();
        return new LRegExp.Token<>(ch);
    }

    LRegExp.Expression<Character> readConcatenation(StringIterator iterator) {
        LRegExp.Expression<Character> lhs = context;
        if (!iterator.hasNext()) return null;
        // '.' is the concatenation operator
        if (iterator.peek() == '.') {
            iterator.advance();
        }
        // just next is also a concatenation operator
        LRegExp.Expression<Character> rhs = readExpression(iterator);
        if (rhs == null) return null;
        return new LRegExp.Concatenation<>(lhs, rhs);
    }

    LRegExp.Expression<Character> readUnion(StringIterator iterator) {
        LRegExp.Expression<Character> lhs = context;
        if (!iterator.hasNext() || iterator.peek() != '|') return null;
        iterator.advance();
        LRegExp.Expression<Character> rhs = readExpression(iterator);
        if (rhs == null) return null;
        return new LRegExp.Union<>(lhs, rhs);
    }

    LRegExp.Expression<Character> readKleeneStar(StringIterator iterator) {
        if (!iterator.hasNext() || iterator.peek() != '*') return null;
        iterator.advance();
        return new LRegExp.KleeneStar<>(context);
    }

    LRegExp.Expression<Character> readParens(StringIterator iterator) {
        if (!iterator.hasNext() || iterator.peek() != '(') return null;
        iterator.advance();
        LRegExp.Expression<Character> expression = readExpression(iterator);
        if (!iterator.hasNext() || iterator.peek() != ')') return null;
        iterator.advance();
        return expression;
    }

    boolean isSpecial(Character ch) {
        return ch == '∅' || ch == 'ε' || ch == '|' || ch == '*' || ch == '.' || ch == '(' || ch == ')';
    }
}

