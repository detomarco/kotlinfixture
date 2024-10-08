/*
 * Copyright 2020 Appmattus Limited
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

package io.github.detomarco.kotlinfixture.decorator.constructor

import io.github.detomarco.kotlinfixture.TestContext
import io.github.detomarco.kotlinfixture.assertIsRandom
import io.github.detomarco.kotlinfixture.config.Configuration
import io.github.detomarco.kotlinfixture.resolver.TestResolver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RandomConstructorStrategyTest {

    private val context = TestContext(Configuration(), TestResolver())

    @Test
    fun `Order constructors at random`() {
        assertIsRandom {
            RandomConstructorStrategy.constructors(context, MultipleConstructors::class).map {
                val emptyParameters = List<Any?>(it.parameters.size) { null }
                (it.call(*emptyParameters.toTypedArray()) as MultipleConstructors).constructorCalled
            }
        }
    }

    @Test
    fun `Random is seeded`() {
        repeat(10) {
            val value1 = RandomConstructorStrategy.constructors(context.seedRandom(), MultipleConstructors::class).map {
                val emptyParameters = List<Any?>(it.parameters.size) { null }
                (it.call(*emptyParameters.toTypedArray()) as MultipleConstructors).constructorCalled
            }

            val value2 = RandomConstructorStrategy.constructors(context.seedRandom(), MultipleConstructors::class).map {
                val emptyParameters = List<Any?>(it.parameters.size) { null }
                (it.call(*emptyParameters.toTypedArray()) as MultipleConstructors).constructorCalled
            }

            assertEquals(value1, value2)
        }
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    class MultipleConstructors {
        val constructorCalled: String

        constructor() {
            constructorCalled = "0"
        }

        constructor(array: Array<String>?) {
            constructorCalled = "1"
        }
    }
}
