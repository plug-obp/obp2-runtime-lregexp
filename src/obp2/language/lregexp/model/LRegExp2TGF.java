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

import java.util.IdentityHashMap;
import java.util.Map;

import static java.lang.System.identityHashCode;

public class LRegExp2TGF<T> extends LRegExp.FunctionalVisitor<T, Map<LRegExp.Expression<T>, String>, String> {

    public static <T> String convert(LRegExp.Expression<T> expression) {
        Map<LRegExp.Expression<T>, String> nodeMap = new IdentityHashMap<>();
        LRegExp2TGF<T> toTGF = new LRegExp2TGF<>();
        String links = expression.accept(toTGF, nodeMap);
        String nodes = String.join("\n", nodeMap.values());
        return String.format("0 0\n%s\n#\n0 %s\n%s", nodes, identityHashCode(expression), links);
    }

    String computeTGF(LRegExp.Expression<T> node, Map<LRegExp.Expression<T>, String> map) {
        return node.accept(this, map);
    }
    @Override
    String visit(LRegExp.Empty<T> node, Map<LRegExp.Expression<T>, String> input) {
        if (input.get(node) != null) return "";
        input.put(node, String.format("%s %s", identityHashCode(node), node.getSymbol()));
        return "";
    }

    @Override
    String visit(LRegExp.Epsilon<T> node, Map<LRegExp.Expression<T>, String> input) {
        if (input.get(node) != null) return "";
        input.put(node, String.format("%s %s", identityHashCode(node), node.getSymbol()));
        return "";
    }

    @Override
    String visit(LRegExp.Token<T> node, Map<LRegExp.Expression<T>, String> input) {
        if (input.get(node) != null) return "";
        input.put(node, String.format("%s (%s %s)", identityHashCode(node), node.getSymbol(), node.token));
        return "";
    }

    @Override
    String visit(LRegExp.Composite<T> node, Map<LRegExp.Expression<T>, String> input) {
        if (input.get(node) != null) return "";
        input.put(node, String.format("%s %s", identityHashCode(node), node.getSymbol()));

        String result = "";
        int idx = 0;
        for (LRegExp.Expression<T> operand: node.operands) {
            String opL = computeTGF(operand, input);
            result += String.format("%s\n%s %s %s\n", opL, identityHashCode(node), identityHashCode(operand), idx++);
        }

        return result;
    }

    @Override
    String visit(LRegExp.Concatenation<T> node, Map<LRegExp.Expression<T>, String> input) {
        return visit((LRegExp.Composite<T>) node, input);
    }

    @Override
    String visit(LRegExp.Union<T> node, Map<LRegExp.Expression<T>, String> input) {
        return visit((LRegExp.Composite<T>) node, input);
    }

    @Override
    String visit(LRegExp.KleeneStar<T> node, Map<LRegExp.Expression<T>, String> input) {
        return visit((LRegExp.Composite<T>) node, input);
    }
}
