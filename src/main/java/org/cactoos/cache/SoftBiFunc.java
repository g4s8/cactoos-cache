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
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.annotation.concurrent.NotThreadSafe;
import org.cactoos.BiFunc;
import org.cactoos.map.MapEntry;

/**
 * Caching {@link BiFunc} implementation based on {@link SoftReference}s.
 * <p>
 * JVM will not delete cached value if
 * {@code time (ms) <= free-heap-size (mb) * MSPerMB}, where
 * {@code time} is a period from last strong-referencing to cached value,
 * {@code MSPerMB} is JVM constant, can be configured as option:
 * {@code -XX:SoftRefLRUPolicyMSPerMB}, default value (for most known JVMs)
 * is {@code 1000 ms for free heap MB}.
 * But  keep in mind, that all cached values which are not linked with strong
 * references may be deleted before JVM will throw {@link OutOfMemoryError}.
 *
 * @param <X> First argument type
 * @param <Y> Second argument type
 * @param <Z> Result type
 * @since 0.1
 */
@NotThreadSafe
public final class SoftBiFunc<X, Y, Z> implements BiFunc<X, Y, Z> {

    /**
     * Empty reference.
     */
    private static final SoftReference<Void> EMPTY =
        new SoftReference<>(null);

    /**
     * Reference map.
     */
    private final Map<Map.Entry<X, Y>, SoftReference<Z>> map;

    /**
     * Reference queue.
     */
    private final ReferenceQueue<Z> references;

    /**
     * Origin func.
     */
    private final BiFunc<X, Y, Z> origin;

    /**
     * Ctor.
     *
     * @param func Origin func
     */
    public SoftBiFunc(final BiFunc<X, Y, Z> func) {
        this(func, null);
    }

    /**
     * Primary ctor.
     *
     * @param func Origin func
     * @param queue Reference queue
     */
    public SoftBiFunc(final BiFunc<X, Y, Z> func,
        final ReferenceQueue<Z> queue) {
        this.origin = func;
        this.references = queue;
        this.map = new LinkedHashMap<>(0);
    }

    @Override
    public Z apply(final X first, final Y second) throws Exception {
        final Map.Entry<X, Y> key = new MapEntry<>(first, second);
        @SuppressWarnings("unchecked") final SoftReference<Z> ref =
            this.map.getOrDefault(key, (SoftReference<Z>) SoftBiFunc.EMPTY);
        Z val = ref.get();
        if (val == null) {
            val = this.origin.apply(first, second);
            this.map.put(key, new SoftReference<>(val, this.references));
        }
        final Collection<Map.Entry<X, Y>> empty = new LinkedList<>();
        for (final Map.Entry<Map.Entry<X, Y>, SoftReference<Z>> entry
            : this.map.entrySet()) {
            if (entry.getValue().get() == null) {
                empty.add(entry.getKey());
            }
        }
        for (final Map.Entry<X, Y> item : empty) {
            this.map.remove(item);
        }
        return val;
    }
}
