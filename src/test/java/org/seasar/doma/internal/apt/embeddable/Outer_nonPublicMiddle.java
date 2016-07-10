/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.apt.embeddable;

import org.seasar.doma.Embeddable;

/**
 * @author nakamura-to
 *
 */
public class Outer_nonPublicMiddle {

    static class Middle {

        @Embeddable
        public static class Inner {

            private final String city;

            private final String street;

            public Inner(String city, String street) {
                this.city = city;
                this.street = street;
            }

            public String getCity() {
                return city;
            }

            public String getStreet() {
                return street;
            }
        }

    }

}
