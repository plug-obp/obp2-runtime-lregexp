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

public class LRegExpDerivative<T> extends LRegExp.FunctionalVisitor<T, T, LRegExp.Expression<T>> {
    LRegExp.Expression<T> derivative(LRegExp.Expression<T> node, T token) {
        return node.accept(this, token);
    }

    LRegExp.Expression<T> delta(LRegExp.Expression<T> node) {
        LRegExpNullability<T> nullability = new LRegExpNullability<>();
        return node.accept(nullability, null);
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Empty<T> node, T input) {
        return new LRegExp.Empty<>();
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Epsilon<T> node, T input) {
        return new LRegExp.Empty<>();
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Token<T> node, T input) {
        return node.token == input ? new LRegExp.Epsilon<>() : new LRegExp.Empty<>();
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.Union<T> node, T input) {
        return new LRegExp.Union<>(
                derivative(node.operands.get(0), input),
                derivative(node.operands.get(1), input)
        );
    }

    //Dc(L1 ◦ L2) = (Dc(L1) ◦ L2) ∪ (δ(L1) ◦ Dc(L2)).
    @Override
    LRegExp.Expression<T> visit(LRegExp.Concatenation<T> node, T input) {
        return new LRegExp.Union<>(
                (new LRegExp.Concatenation<>(
                        derivative(node.operands.get(0), input),
                        node.operands.get(1))),
                (new LRegExp.Concatenation<>(
                        delta(node.operands.get(0)),
                        derivative(node.operands.get(1), input)))
        );
    }

    @Override
    LRegExp.Expression<T> visit(LRegExp.KleeneStar<T> node, T input) {
        return new LRegExp.Concatenation<>(
                derivative(node.operands.get(0), input),
                node
        );
    }
}

