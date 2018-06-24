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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import javax.annotation.concurrent.NotThreadSafe;
import org.cactoos.BiFunc;
import org.cactoos.Text;
import org.cactoos.func.IoCheckedBiFunc;

/**
 * Text implementation based on {@link SoftBiFunc}.
 *
 * @since 0.1
 */
@NotThreadSafe
public final class SoftText implements Text {

    /**
     * Dummy key for {@link SoftBiFunc} argument.
     */
    private static final Object KEY = new Object();

    /**
     * Origin func.
     */
    private final SoftBiFunc<Object, Object, String> origin;

    /**
     * Ctor.
     *
     * @param text Origin text
     * @param queue Reference queue
     */
    public SoftText(final Text text, final ReferenceQueue<String> queue) {
        this(new SoftBiFunc<>(new SoftText.TextWrap(text), queue));
    }

    /**
     * Ctor.
     *
     * @param text Origin text
     */
    public SoftText(final Text text) {
        this(new SoftBiFunc<>(new SoftText.TextWrap(text)));
    }

    /**
     * Primary ctor.
     *
     * @param origin Origin func
     */
    private SoftText(final SoftBiFunc<Object, Object, String> origin) {
        this.origin = origin;
    }

    @Override
    public String asString() throws IOException {
        return new IoCheckedBiFunc<>(this.origin)
            .apply(SoftText.KEY, SoftText.KEY);
    }

    /**
     * Decorator for {@link Text} to be {@link BiFunc}.
     */
    private static final class TextWrap
        implements BiFunc<Object, Object, String> {

        /**
         * Origin text.
         */
        private final Text origin;

        /**
         * Ctor.
         *
         * @param text Origin text
         */
        private TextWrap(final Text text) {
            this.origin = text;
        }

        @Override
        @SuppressWarnings("PMD.StringInstantiation")
        @SuppressFBWarnings(
            value = "DM_STRING_CTOR",
            justification = "unintern string reference"
        )
        public String apply(final Object first, final Object second)
            throws Exception {
            return new String(this.origin.asString());
        }
    }
}
