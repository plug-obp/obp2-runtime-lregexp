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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ciprian Teodorov (ciprian.teodorov@ensta-bretagne.fr)
 * Created on 04/12/23.
 */
public class LRegExp {

    public static class FunctionalVisitor<T, I, O> {
        O visit(Expression<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(Terminal<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(Empty<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(Epsilon<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(Token<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(Composite<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(Union<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(Concatenation<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }

        O visit(KleeneStar<T> node, I input) {
            throw new UnsupportedOperationException("missing visitor method");
        }
    }

    public static abstract class Expression<T> {
        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }

        public abstract String getSymbol();
    }

    public static abstract class Terminal<T> extends Expression<T> {
        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

    public static class Empty<T> extends Terminal<T> {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof Empty);
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }

        @Override
        public String getSymbol() {
            return "∅";
        }

        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

    public static class Epsilon<T> extends Terminal<T> {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof Epsilon);
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }

        @Override
        public String getSymbol() {
            return "ε";
        }

        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

    public static class Token<T> extends Terminal<T> {
        public T token;

        public Token(T token) {
            this.token = token;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj)
                    || (obj instanceof Token && token == ((Token<?>) obj).token);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getClass(), token);
        }

        @Override
        public String getSymbol() {
            return "τ";
        }

        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

    public static abstract class Composite<T> extends Expression<T> {
        public List<Expression<T>> operands;

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj)
                    || (obj instanceof Composite && Objects.equals(operands, ((Composite<T>) obj).operands));
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getClass(), operands);
        }

        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

    public static class Concatenation<T> extends Composite<T> {
        public Concatenation(Expression<T> lhs, Expression<T> rhs) {
            operands = Stream.of(lhs, rhs).collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && (obj instanceof Concatenation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getClass(), operands);
        }

        @Override
        public String getSymbol() {
            return "∘";
        }

        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

    public static class Union<T> extends Composite<T> {
        public Union(Expression<T> lhs, Expression<T> rhs) {
            operands = Stream.of(lhs, rhs).collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && (obj instanceof Union);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getClass(), operands);
        }

        @Override
        public String getSymbol() {
            return "|";
        }

        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

    public static class KleeneStar<T> extends Composite<T> {
        public KleeneStar(Expression<T> operand) {
            operands = Collections.singletonList(operand);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && (obj instanceof KleeneStar);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getClass(), operands);
        }

        @Override
        public String getSymbol() {
            return "*";
        }

        public <I, O> O accept(FunctionalVisitor<T, I, O> visitor, I input) {
            return visitor.visit(this, input);
        }
    }

}