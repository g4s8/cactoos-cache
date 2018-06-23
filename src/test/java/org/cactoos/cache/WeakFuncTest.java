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
import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.Func;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link WeakFunc}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public final class WeakFuncTest {
    @Test
    public void keepsValueIfKeyAccessibleThroughStringReference()
        throws Exception {
        final AtomicInteger cnt = new AtomicInteger();
        final Func<Object, Object> target =
            new WeakFunc<>(new WeakFuncTest.Target(cnt));
        final Object key = new Object();
        final Object first = target.apply(key);
        WeakFuncTest.clear();
        final Object second = target.apply(key);
        MatcherAssert.assertThat(
            cnt.get(),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            first == second,
            Matchers.is(true)
        );
    }

    @Test
    public void keepsValueIfKeyAccessibleThroughSoftReference()
        throws Exception {
        final AtomicInteger cnt = new AtomicInteger();
        final WeakFunc<Object, Object> target =
            new WeakFunc<>(new WeakFuncTest.Target(cnt));
        final Reference<Object> ref = new SoftReference<>(new Object());
        final Object first = target.apply(ref.get());
        WeakFuncTest.clear();
        final Object second = target.apply(ref.get());
        MatcherAssert.assertThat(
            cnt.get(),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            first == second,
            Matchers.is(true)
        );
    }

    @Test
    public void clearValueIfKeyAccessibleThroughWeakReference()
        throws Exception {
        final AtomicInteger cnt = new AtomicInteger();
        final WeakFunc<Object, Object> target =
            new WeakFunc<>(new WeakFuncTest.Target(cnt));
        final Reference<Object> ref = new WeakReference<>(new Object());
        final Object first = target.apply(ref.get());
        WeakFuncTest.clear();
        final Object second = target.apply(ref.get());
        MatcherAssert.assertThat(
            cnt.get(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            first == second,
            Matchers.is(false)
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
        final Reference<Object> ref = new WeakReference<>(new Object(), queue);
        Reference<?> poll;
        do {
            poll = queue.poll();
            System.gc();
        } while (!ref.equals(poll));
    }

    /**
     * Target func.
     */
    private static final class Target implements Func<Object, Object> {

        /**
         * Counter.
         */
        private final AtomicInteger cnt;

        /**
         * Ctor.
         *
         * @param cnt Counter
         */
        private Target(final AtomicInteger cnt) {
            this.cnt = cnt;
        }

        @Override
        public Object apply(final Object arg) {
            this.cnt.incrementAndGet();
            return new Object();
        }
    }
}
