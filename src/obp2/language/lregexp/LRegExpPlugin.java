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

package obp2.language.lregexp;

import obp2.language.lregexp.diagnosis.LRegExpAtomicPropositionEvaluator;
import obp2.language.lregexp.model.LRegExpCharReader;
import obp2.language.lregexp.model.LRegExp;
import obp2.language.lregexp.model.StringIterator;
import obp2.language.lregexp.runtime.LRegExpAction;
import obp2.language.lregexp.runtime.LRegExpConfiguration;
import obp2.language.lregexp.runtime.LRegExpTransitionRelation;
import obp2.runtime.core.ILanguageModule;
import obp2.runtime.core.ILanguagePlugin;
import obp2.runtime.core.LanguageModule;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.function.Function;

/**
 * Created by Ciprian TEODOROV on 03/03/17.
 */
public class LRegExpPlugin implements ILanguagePlugin<URI, LRegExpConfiguration, LRegExpAction, Void> {

    @Override
    public String[] getExtensions() {
        return new String[]{".regexp"};
    }

    @Override
    public Function<URI, ILanguageModule<LRegExpConfiguration, LRegExpAction, Void>> languageModuleFunction() {
        return this::getRuntime;
    }

    public ILanguageModule<LRegExpConfiguration, LRegExpAction, Void> getRuntime(URI explicitProgramURI) {
        return getRuntime(new File(explicitProgramURI));
    }

    public ILanguageModule<LRegExpConfiguration, LRegExpAction, Void> getRuntime(String explicitProgramFileName) {
        return getRuntime(new File(explicitProgramFileName));
    }

    public ILanguageModule<LRegExpConfiguration, LRegExpAction, Void> getRuntime(File programFile) {
        try {
            String string = new String(Files.readAllBytes(programFile.toPath()));
            StringIterator iterator = new StringIterator(string);
            LRegExpCharReader reader = new LRegExpCharReader();
            LRegExp.Expression<Character> program = reader.readExpression(iterator);
            LRegExpTransitionRelation<Character> runtime = new LRegExpTransitionRelation<>(programFile.getName(), program);

            return new LanguageModule<>(runtime, new LRegExpAtomicPropositionEvaluator(program), new LRegExpRuntimeView(runtime));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getName() {
        return "LString";
    }


}
