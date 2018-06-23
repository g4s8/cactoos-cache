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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.BiFunc;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link SoftBiFunc}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public final class SoftBiFuncTest {
    @Test
    public void usesCachedValuesIfStrongReferenceIsAlive()
        throws Exception {
        final AtomicInteger cnt = new AtomicInteger();
        final BiFunc<Object, Object, Object> target =
            new SoftBiFunc<>(new SoftBiFuncTest.Target(cnt));
        final Object first = new Object();
        final Object second = new Object();
        final Object strong = target.apply(first, second);
        SoftBiFuncTest.clear();
        MatcherAssert.assertThat(
            target.apply(first, second) == strong,
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            cnt.get(),
            Matchers.equalTo(1)
        );
    }

    @Test
    public void clearsCachedValuesIfCacheWasLinkedViaSoftReference()
        throws Exception {
        final AtomicInteger cnt = new AtomicInteger();
        final BiFunc<Object, Object, Object> target =
            new SoftBiFunc<>(new SoftBiFuncTest.Target(cnt));
        final Object first = new Object();
        final Object second = new Object();
        final Reference<Object> soft =
            new SoftReference<>(target.apply(first, second));
        SoftBiFuncTest.clear();
        target.apply(first, second);
        MatcherAssert.assertThat(
            cnt.get(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            soft.get(),
            Matchers.nullValue()
        );
    }

    @Test
    public void clearsCachedValuesIfCacheWasLinkedViaWeakReference()
        throws Exception {
        final AtomicInteger cnt = new AtomicInteger();
        final BiFunc<Object, Object, Object> target =
            new SoftBiFunc<>(new SoftBiFuncTest.Target(cnt));
        final Object first = new Object();
        final Object second = new Object();
        final Reference<Object> weak =
            new WeakReference<>(target.apply(first, second));
        SoftBiFuncTest.clear();
        target.apply(first, second);
        MatcherAssert.assertThat(
            cnt.get(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            weak.get(),
            Matchers.nullValue()
        );
    }

    @Test
    public void usesProvidedReferenceQueue() throws Exception {
        final ReferenceQueue<Object> queue = new ReferenceQueue<>();
        final BiFunc<Object, Object, Object> target =
            new SoftBiFunc<>(
                new SoftBiFuncTest.Target(new AtomicInteger()),
                queue
            );
        target.apply(new Object(), new Object());
        SoftBiFuncTest.clear();
        MatcherAssert.assertThat(
            queue.poll(),
            Matchers.notNullValue()
        );
    }

    @SuppressWarnings(
        {
            "PMD.DoNotCallGarbageCollectionExplicitly",
            "PMD.EmptyCatchBlock",
            "PMD.AvoidInstantiatingObjectsInLoops"
        }
    )
    private static void clear() {
        final ReferenceQueue<Object> queue = new ReferenceQueue<>();
        final Reference<Object> ref = new SoftReference<>(new Object(), queue);
        Reference<?> poll;
        final Collection<byte[]> mem = new LinkedList<>();
        final Runtime runtime = Runtime.getRuntime();
        do {
            poll = queue.poll();
            try {
                // @checkstyle AvoidInstantiatingObjectsInLoops (1 line)
                mem.add(new byte[(int) runtime.freeMemory()]);
                // @checkstyle EmptyCatchBlock (2 liens)
            } catch (final OutOfMemoryError ignored) {
            }
            System.gc();
        } while (!ref.equals(poll));
        mem.clear();
        System.gc();
    }

    /**
     * Target func.
     */
    private static final class Target
        implements BiFunc<Object, Object, Object> {

        /**
         * Counter.
         */
        private final AtomicInteger cnt;
        /**
         * Ctor.
         * @param cnt Counter
         */
        private Target(final AtomicInteger cnt) {
            this.cnt = cnt;
        }

        @Override
        public Object apply(final Object first, final Object second) {
            this.cnt.incrementAndGet();
            return new Object();
        }
    }
}
