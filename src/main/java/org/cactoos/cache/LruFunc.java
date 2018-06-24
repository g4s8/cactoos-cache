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

import javax.annotation.concurrent.NotThreadSafe;
import org.cactoos.BiFunc;
import org.cactoos.Func;

/**
 * Func implementation based on {@link LruBiFunc} which caches most
 * used func results.
 *
 * @param <X> Argument type
 * @param <Y> Result type
 * @since 0.1
 */
@NotThreadSafe
public final class LruFunc<X, Y> implements Func<X, Y> {

    /**
     * Dummy key for {@link LruBiFunc} argument.
     */
    private static final Object KEY = new Object();

    /**
     * Origin LRI func.
     */
    private final LruBiFunc<Object, X, Y> origin;

    /**
     * Ctor.
     * @param func Origin func
     * @param size Cache size
     */
    public LruFunc(final Func<X, Y> func, final int size) {
        this.origin = new LruBiFunc<>(new LruFunc.FuncWrap<>(func), size);
    }

    @Override
    public Y apply(final X input) throws Exception {
        return this.origin.apply(LruFunc.KEY, input);
    }

    /**
     * Decorator for {@link Func} to be {@link BiFunc}.
     *
     * @param <X> Argument type
     * @param <Y> Result type
     */
    private static final class FuncWrap<X, Y> implements BiFunc<Object, X, Y> {

        /**
         * Origin func.
         */
        private final Func<X, Y> origin;

        /**
         * Ctor.
         * @param func Origin func
         */
        private FuncWrap(final Func<X, Y> func) {
            this.origin = func;
        }

        @Override
        public Y apply(final Object first, final X second) throws Exception {
            return this.origin.apply(second);
        }
    }
}
