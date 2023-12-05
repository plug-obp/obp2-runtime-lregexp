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

package obp2.language.lregexp.runtime;

import obp2.language.lregexp.model.LRegExp;
import obp2.language.lregexp.model.LRegExpBooleanNullability;
import obp2.language.lregexp.model.LRegExpDerivative;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class LRexExpSemantics<T> implements IOSemantics<LRegExpConfiguration<T>, LRegExpDerivative<T>, T, Boolean> {
    LRegExp.Expression<T> expression;
    LRegExpDerivative<T> derivator;

    public LRexExpSemantics(LRegExp.Expression<T> expression) {
        this.expression = expression;
        this.derivator = new LRegExpDerivative<>();
    }

    @Override
    public Collection<LRegExpConfiguration<T>> initial() {
        return Collections.singleton( new LRegExpConfiguration<>(expression) );
    }

    @Override
    public Collection<LRegExpDerivative<T>> actions(T input, LRegExpConfiguration<T> source) {
        return Collections.singleton(derivator);
    }
    @Override
    public Collection<Outcome<Boolean, LRegExpConfiguration<T>>> execute(LRegExpDerivative<T> action, T input, LRegExpConfiguration<T> source) {
        LRegExpConfiguration<T> target = new LRegExpConfiguration<>( source.expression.accept(action, input) );
        boolean isNullable = target.expression.accept(new LRegExpBooleanNullability<>(), null);
        return Collections.singleton(new Outcome<>(isNullable, target));
    }
}

interface IOSemantics<C, A, I, O> {
    Collection<C> initial();
    Collection<A> actions(I input, C source);
    Collection<Outcome<O,C>> execute(A action, I input, C source);
}

abstract class MaybeStutter<A> {
    public static<A> MaybeStutter<A> stutter() {
        return Stutter.instance();
    }

    public static<A> MaybeStutter<A> enabled(A action) {
        return new Enabled(action);
    }
}
class Enabled<A> extends MaybeStutter<A> {
    A action;
    Enabled(A action) {
        this.action = Objects.requireNonNull(action);
    }
    public A action() {
        return action;
    }
}
class Stutter<A> extends MaybeStutter<A> {
    private static final Stutter<?> EMPTY = new Stutter<>();
    static<T> Stutter<T> instance() {
        @SuppressWarnings("unchecked")
        Stutter<T> t = (Stutter<T>) EMPTY;
        return t;
    }
}

class Outcome<O, C> {
    O output;
    C target;

    public Outcome(O out, C target) {
        this.output = out;
        this.target = target;
    }
}

class Step<C, A, I, O> {
    I input;
    C source;
    MaybeStutter<A> action;
    Outcome<O, C> outcome;

    public Step(I in, C s, MaybeStutter<A> a, O out, C t) {
        this.input = in;
        this.source = s;
        this.action = a;
        this.outcome = new Outcome<>(out, t);
    }

    public Step(I in, C s, MaybeStutter<A> a, Outcome<O, C> outcome) {
        this.input = in;
        this.source = s;
        this.action = a;
        this.outcome = outcome;
    }

    public I input() {
        return input;
    }
    public C source() {
        return source;
    }

    public MaybeStutter<A> action() {
        return action;
    }

    public Outcome<O,C> outcome() {
        return outcome;
    }

    public O output() {
        return outcome.output;
    }

    public C target() {
        return outcome.target;
    }
}
