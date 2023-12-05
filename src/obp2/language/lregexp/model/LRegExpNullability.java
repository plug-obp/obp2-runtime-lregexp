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

public class LRegExpNullability<T> extends LRegExp.FunctionalVisitor<T, Void, LRegExp.Expression<T>> {
    LRegExp.Expression<T> nullability(LRegExp.Expression<T> node) {
        return node.accept(this, null);
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Empty<T> node, Void input) {
        return new LRegExp.Empty<>();
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Epsilon<T> node, Void input) {
        return new LRegExp.Epsilon<>();
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Token<T> node, Void input) {
        return new LRegExp.Empty<>();
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Union<T> node, Void input) {
        return new LRegExp.Union<>(
                nullability(node.operands.get(0)),
                nullability(node.operands.get(1))
        );
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Concatenation<T> node, Void input) {
        return new LRegExp.Concatenation<>(
                nullability(node.operands.get(0)),
                nullability(node.operands.get(1))
        );
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.KleeneStar<T> node, Void input) {
        return new LRegExp.Epsilon<>();
    }
}
