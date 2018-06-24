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

import java.lang.ref.ReferenceQueue;
import javax.annotation.concurrent.NotThreadSafe;
import org.cactoos.BiFunc;
import org.cactoos.Func;

/**
 * Func implementation based on {@link SoftBiFunc}.
 *
 * @param <X> Argument type
 * @param <Y> Result type
 * @since 0.1
 */
@NotThreadSafe
public final class SoftFunc<X, Y> implements Func<X, Y> {

    /**
     * Dummy key for {@link SoftBiFunc} argument.
     */
    private static final Object KEY = new Object();

    /**
     * Origin func.
     */
    private final SoftBiFunc<Object, X, Y> origin;

    /**
     * Ctor.
     *
     * @param func Origin func.
     * @param queue Reference queue.
     */
    public SoftFunc(final Func<X, Y> func, final ReferenceQueue<Y> queue) {
        this(new SoftBiFunc<>(new BiFuncWrap<>(func), queue));
    }

    /**
     * Ctor.
     *
     * @param func Origin func.
     */
    public SoftFunc(final Func<X, Y> func) {
        this(new SoftBiFunc<>(new SoftFunc.BiFuncWrap<>(func)));
    }

    /**
     * Ctor.
     *
     * @param func Origin func
     */
    private SoftFunc(final SoftBiFunc<Object, X, Y> func) {
        this.origin = func;
    }

    @Override
    public Y apply(final X arg) throws Exception {
        return this.origin.apply(SoftFunc.KEY, arg);
    }

    /**
     * Decorator for {@link Func} to be {@link BiFunc}.
     *
     * @param <X> Argument type
     * @param <Y> Result type
     */
    private static final class BiFuncWrap<X, Y> implements
        BiFunc<Object, X, Y> {

        /**
         * Decorating func.
         */
        private final Func<X, Y> origin;

        /**
         * Ctor.
         * @param func Decorating func
         */
        BiFuncWrap(final Func<X, Y> func) {
            this.origin = func;
        }

        @Override
        public Y apply(final Object ignore, final X arg) throws Exception {
            return this.origin.apply(arg);
        }
    }
}
