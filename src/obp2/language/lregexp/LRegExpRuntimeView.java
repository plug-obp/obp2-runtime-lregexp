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

import obp2.language.lregexp.runtime.LRegExpAction;
import obp2.language.lregexp.runtime.LRegExpConfiguration;
import obp2.language.lregexp.runtime.LRegExpTransitionRelation;
import obp2.runtime.core.TreeItem;
import obp2.runtime.core.defaults.DefaultTreeProjector;

import java.util.Collections;

/**
 * Created by Ciprian TEODOROV on 03/03/17.
 */
public class LRegExpRuntimeView<T> extends DefaultTreeProjector<LRegExpConfiguration, LRegExpAction, Void> {
    private final LRegExpTransitionRelation<T> runtime;

    public LRegExpRuntimeView(LRegExpTransitionRelation<T> runtime) {
        this.runtime = runtime;
    }

    @Override
    public TreeItem projectConfiguration(LRegExpConfiguration value) {
        TreeItem entry = new TreeItem("nothing");
        return new TreeItem(runtime.name, Collections.singletonList(entry));
    }

    @Override
    public TreeItem projectFireable(LRegExpAction action) {
        return new TreeItem("next");
    }
}
