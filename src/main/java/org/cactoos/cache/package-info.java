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
/**
 * Cactoos caching entities based on {@link java.lang.ref.SoftReference}
 * and {@link java.lang.ref.WeakReference} capacities.
 * <p>
 * There are {@link org.cactoos.Func}, {@link org.cactoos.BiFunc},
 * {@link org.cactoos.Scalar} and {@link org.cactoos.Text} implementations
 * based on soft-references, but only {@link org.cactoos.Func} implementation
 * based on weak-references. It's not a bug, it's by design, because most
 * probably you don't need to keep func or text results in weak-references,
 * weak-references are used to keep association between
 * weak-ref <b>keys</b> and values, for instance:
 * {@link java.util.WeakHashMap}, if you want to use
 * {@link java.lang.ref.WeakReference} to cache results - you are doing
 * something wrong.
 * <p>
 * Most documented base classes here are: {@link org.cactoos.cache.SoftBiFunc}
 * and {@link org.cactoos.cache.WeakFunc}, all other classes are based on
 * these two classes decorating them.
 * @todo #1:30min Add `SoftBytes` class which should implement `Bytes`
 *  interface and uses `SoftReference` to cache byte[] value.
 *
 * @since 0.1
 */
package org.cactoos.cache;
