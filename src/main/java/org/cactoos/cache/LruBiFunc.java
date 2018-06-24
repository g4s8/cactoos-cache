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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.NotThreadSafe;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.collection.Sorted;
import org.cactoos.map.MapEntry;
import org.cactoos.scalar.ItemAt;

/**
 * Func implementation which uses LRU (least Recently Used) cache to
 * store func results. This Func keeps in cache only most used values and
 * clear least used if cache is full. Cache size can be configured via
 * {@code size} constructor parameter: {@code new LruBiFunc(10, func)}.
 * <p>
 * Func arguments must implement
 * {@link Object#equals(Object)} and {@link Object#hashCode()} methods
 * to be used as cache keys.
 *
 * @param <X> First argument type
 * @param <Y> First argument type
 * @param <Z> Result type
 * @since 0.1
 * @todo #5:30min Add LRU func (Func and BiFunc) tests, which should cover
 *  that cached values are most used by consumer and least used can be cleared
 *  when cache is full.
 */
@NotThreadSafe
public final class LruBiFunc<X, Y, Z> implements BiFunc<X, Y, Z> {

    /**
     * Cache.
     */
    private final Map<Map.Entry<X, Y>, AtomicInteger> hits;
    /**
     * Origin func.
     */
    private final Func<Map.Entry<X, Y>, Z> origin;
    /**
     * Threshold (max cache size).
     */
    private final int threshold;

    /**
     * Ctor.
     * @param func Origin func
     * @param size Cache size
     */
    public LruBiFunc(final BiFunc<X, Y, Z> func, final int size) {
        this.hits = new HashMap<>(size);
        this.threshold = size;
        this.origin = new WeakFunc<>(new LruBiFunc.WeakWrap<>(func));
    }

    @Override
    public Z apply(final X first, final Y second) throws Exception {
        final Map.Entry<X, Y> key = new MapEntry<>(first, second);
        final Z val;
        if (this.hits.containsKey(key)) {
            this.hits.get(key).incrementAndGet();
            val = this.origin.apply(key);
        } else {
            if (this.hits.size() >= this.threshold) {
                this.hits.remove(this.loser());
            }
            this.hits.put(key, new AtomicInteger(1));
            val = this.origin.apply(key);
        }
        return val;
    }

    /**
     * Hits loser (entry with lowest hits).
     *
     * @return Hits key
     * @throws Exception If fails
     */
    private Map.Entry<X, Y> loser() throws Exception {
        return new ItemAt<>(
            new Sorted<>(
                Comparator.comparingInt(left -> left.getValue().get()),
                this.hits.entrySet()
            )
        ).value().getKey();
    }

    /**
     * Decorator for {@link BiFunc} to be {@link Func}.
     *
     * @param <X> First argument type
     * @param <Y> Second argument type
     * @param <Z> Result type
     */
    private static final class WeakWrap<X, Y, Z> implements
        Func<Map.Entry<X, Y>, Z> {

        /**
         * Origin func to wrap.
         */
        private final BiFunc<X, Y, Z> origin;

        /**
         * Ctor.
         *
         * @param func Origin func
         */
        private WeakWrap(final BiFunc<X, Y, Z> func) {
            this.origin = func;
        }

        @Override
        public Z apply(final Map.Entry<X, Y> input) throws Exception {
            return this.origin.apply(input.getKey(), input.getValue());
        }
    }
}
