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

package obp2.language.lregexp.diagnosis;

import obp2.language.lregexp.model.LRegExp;
import obp2.language.lregexp.runtime.LRegExpAction;
import obp2.language.lregexp.runtime.LRegExpConfiguration;
import obp2.runtime.core.IAtomicPropositionsEvaluator;
import obp2.runtime.core.defaults.DefaultLanguageService;

public class LRegExpAtomicPropositionEvaluator<T>
        extends DefaultLanguageService<LRegExpConfiguration, LRegExpAction, Void>
        implements IAtomicPropositionsEvaluator<LRegExpConfiguration, LRegExpAction, Void> {
    String atomicPropositions[];

    public LRegExp.Expression<T> model;
    EvaluationEnvironment environment;
    LRegExpDiagnosisExp propositions[];

    public LRegExpAtomicPropositionEvaluator(LRegExp.Expression<T> model) {
        this.model = model;
        environment = new EvaluationEnvironment();
        environment.model = model;
    }

    @Override
    public int[] registerAtomicPropositions(String[] atomicPropositions) {
        int[] result = new int[atomicPropositions.length];
        this.atomicPropositions = atomicPropositions;
        propositions = new LRegExpDiagnosisExp[atomicPropositions.length];
        for (int i = 0; i<propositions.length; i++) {
            propositions[i] = parse(atomicPropositions[i]);
            result[i] = i;
        }
        return result;
    }

    LRegExpDiagnosisExp parse(String code) {
        // index = 3
        // index' = 4
        // index' = index + 1
        // @index a
        // @index' a

        // 'a
        if (code.startsWith("'") && code.length() == 2) {
            return new LRegExpNextCharacterExp(code.charAt(1));
        }
        // a
        if (code.length() == 1) {
            return new LRegExpCharacterExp(code.charAt(0));
        }

        String errorMessage = "LString: Unable to parse " + code + "as a diagnosis expression";
        System.err.println(errorMessage);
        throw new RuntimeException(errorMessage);
    }

    boolean evaluate(LRegExpDiagnosisExp expression, EvaluationEnvironment environment) {
        if (expression instanceof LRegExpNextCharacterExp) {
            return false; //environment.model.string.charAt(environment.target.index) == ((LRegExpNextCharacterExp) expression).character;
        }

        if (expression instanceof LRegExpCharacterExp) {
            return false; //environment.model.string.charAt(environment.source.index) == ((LRegExpCharacterExp) expression).character;
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean[] getAtomicPropositionValuations(LRegExpConfiguration configuration) {
        environment.source = configuration;

        boolean valuations[] = new boolean[propositions.length];
        for (int i = 0; i<propositions.length;i++) {
            valuations[i] = evaluate(propositions[i], environment);
        }
        return valuations;
    }


    @SuppressWarnings("Duplicates")
    @Override
    public boolean[] getAtomicPropositionValuations(LRegExpConfiguration source, LRegExpAction fireable, Void payload, LRegExpConfiguration target) {
        environment.source = source;
        environment.target = target;

        boolean valuations[] = new boolean[propositions.length];
        for (int i = 0; i<propositions.length;i++) {
            valuations[i] = evaluate(propositions[i], environment);
        }
        return valuations;
    }
}

class EvaluationEnvironment<T> {
    LRegExp.Expression<T> model;
    LRegExpConfiguration source;
    LRegExpConfiguration target;
}