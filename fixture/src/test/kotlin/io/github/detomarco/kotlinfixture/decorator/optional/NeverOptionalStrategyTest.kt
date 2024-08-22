/*
 * Copyright 2019 Appmattus Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.detomarco.kotlinfixture.decorator.optional

import io.github.detomarco.kotlinfixture.TestContext
import io.github.detomarco.kotlinfixture.config.ConfigurationBuilder
import io.github.detomarco.kotlinfixture.resolver.TestResolver
import kotlin.test.Test
import kotlin.test.assertFalse

class NeverOptionalStrategyTest {

    private val testContext = TestContext(ConfigurationBuilder().build(), TestResolver())

    data class DataClass(val optionalValue: String = "hello")

    @Test
    fun `Strategy NeverOptionalStrategy returns false`(): Unit = with(testContext) {
        NeverOptionalStrategy.apply {
            assertFalse {
                generateAsOptional(DataClass::class, "optionalValue")
            }
        }
    }
}