/*
 * MIT License
 *
 * Copyright (c) 2018 Kirill
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights * to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.cactoos.cache;

import java.util.Map;
import java.util.WeakHashMap;
import org.cactoos.Func;

/**
 * Func implementation which uses {@link java.lang.ref.WeakReference}
 * to store func <b>arguments</b> (not values). It means that func value
 * will be available as strong reference until func key will be available
 * through strong reference or soft reference chain.
 *
 * @param <X> Argument type
 * @param <Y> Result type
 * @since 0.1
 */
public final class WeakFunc<X, Y> implements Func<X, Y> {

    /**
     * Weak map.
     */
    private final Map<X, Y> map;

    /**
     * Origin func.
     */
    private final Func<X, Y> origin;

    /**
     * Ctor.
     *
     * @param func Origin func
     */
    public WeakFunc(final Func<X, Y> func) {
        this.origin = func;
        // @checkstyle MagicNumberCheck (1 line)
        this.map = new WeakHashMap<>(16, 0.75F);
    }

    @Override
    public Y apply(final X arg) throws Exception {
        Y val = this.map.get(arg);
        if (val == null) {
            val = this.origin.apply(arg);
            this.map.put(arg, val);
        }
        return val;
    }
}
