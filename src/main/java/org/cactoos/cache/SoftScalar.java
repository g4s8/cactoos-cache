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
import org.cactoos.Scalar;

/**
 * Scalar implementation based on {@link SoftBiFunc}.
 *
 * @param <T> Result type
 * @since 0.1
 */
@NotThreadSafe
public final class SoftScalar<T> implements Scalar<T> {

    /**
     * Dummy key for {@link SoftBiFunc} argument.
     */
    private static final Object KEY = new Object();

    /**
     * Origin func.
     */
    private final SoftBiFunc<Object, Object, T> origin;

    /**
     * Ctor.
     *
     * @param scalar Origin scalar
     * @param queue Reference queue
     */
    public SoftScalar(final Scalar<T> scalar, final ReferenceQueue<T> queue) {
        this(new SoftBiFunc<>(new SoftScalar.ScalarWrap<>(scalar), queue));
    }

    /**
     * Ctor.
     *
     * @param scalar Origin scalar
     */
    public SoftScalar(final Scalar<T> scalar) {
        this(new SoftBiFunc<>(new SoftScalar.ScalarWrap<>(scalar)));
    }

    /**
     * Primary ctor.
     *
     * @param func Origin func
     */
    private SoftScalar(final SoftBiFunc<Object, Object, T> func) {
        this.origin = func;
    }

    @Override
    public T value() throws Exception {
        return this.origin.apply(SoftScalar.KEY, SoftScalar.KEY);
    }

    /**
     * Decorator for {@link Scalar} to be {@link BiFunc}.
     *
     * @param <T> Result type
     */
    private static final class ScalarWrap<T> implements
        BiFunc<Object, Object, T> {

        /**
         * Origin scalar.
         */
        private final Scalar<T> origin;

        /**
         * Ctor.
         *
         * @param scalar Origin scalar
         */
        private ScalarWrap(final Scalar<T> scalar) {
            this.origin = scalar;
        }

        @Override
        public T apply(final Object first, final Object second)
            throws Exception {
            return this.origin.value();
        }
    }
}
