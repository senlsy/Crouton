/*
 * Copyright 2012 - 2014 Benjamin Weiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.keyboardsurfer.android.widget.crouton;

/**
 * Allows configuring a {@link Crouton}s behaviour aside from the actual view,
 * which is defined via {@link Style}.
 * <p/>
 * This allows to re-use a {@link Style} while modifying parameters that only have to be applied
 * when the {@link Crouton} is being displayed.
 *
 * @author chris
 * @since 1.8
 */
public class Configuration {


    public static final int DURATION_INFINITE = -1;
    public static final int DURATION_SHORT = 3000;
    public static final int DURATION_LONG = 5000;

    public static final Configuration DEFAULT;

    static {
        DEFAULT = new Builder().setDuration(DURATION_SHORT).build();
    }

    final int durationInMilliseconds;
    final int inAnimationResId;
    final int outAnimationResId;

    private Configuration(Builder builder) {
        this.durationInMilliseconds = builder.durationInMilliseconds;
        this.inAnimationResId = builder.inAnimationResId;
        this.outAnimationResId = builder.outAnimationResId;
    }

    public static class Builder {

        private int durationInMilliseconds = DURATION_SHORT;
        private int inAnimationResId = 0;
        private int outAnimationResId = 0;

        public Builder setDuration(final int duration) {
            this.durationInMilliseconds = duration;
            return this;
        }

        public Builder setInAnimation(final int inAnimationResId) {
            this.inAnimationResId = inAnimationResId;
            return this;
        }

        public Builder setOutAnimation(final int outAnimationResId) {
            this.outAnimationResId = outAnimationResId;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "durationInMilliseconds=" + durationInMilliseconds +
                ", inAnimationResId=" + inAnimationResId +
                ", outAnimationResId=" + outAnimationResId +
                '}';
    }
}