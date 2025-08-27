/*
 * Copyright 2024 Detomarco
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

import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jreleaser.model.Active

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.jreleaser")
    id("signing")
    id("com.adarshr.test-logger")
}

val detektGradlePluginVersion: String by project
val junitVersion: String by project
val junitPlatformVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project
val libVersion = (System.getenv("LIB_VERSION") ?: System.getenv("GITHUB_REF") ?: System.getProperty("GITHUB_REF"))
    ?.replaceFirst("refs/tags/", "") ?: "unspecified"
println("Lib Version $libVersion")
subprojects {
    group = "io.github.detomarco.kotlinfixture"
    version = libVersion

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("io.gitlab.arturbosch.detekt")
        plugin("com.adarshr.test-logger")
    }

    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }

    dependencies {
        implementation(kotlin("stdlib"))

        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektGradlePluginVersion")

//        testImplementation(platform("org.junit:junit-bom:$junitVersion"))
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
        testRuntimeOnly("org.junit.platform:junit-platform-engine:$junitPlatformVersion")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")

        testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")
        testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    }

    kotlin {
        jvmToolchain(17)
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    detekt {
        allRules = true
        buildUponDefaultConfig = true
        autoCorrect = System.getProperty("autoCorrect") == "true"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    testlogger {
        theme = ThemeType.MOCHA
        showSimpleNames = true
    }
}

jreleaser {
    project {
        license = "APACHE-2.0"
        authors = listOf("Appmattus Limited", "detomarco")
        copyright = "2019-2023 Appmattus Limited, 2024 detomarco"
        description = "Fixtures for Kotlin providing generated values for unit testing"
    }
    signing {
        active = Active.ALWAYS
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepositories.add("fixture/build/staging-deploy")
                    stagingRepositories.add("fixture-generex/build/staging-deploy")
                    stagingRepositories.add("fixture-datafaker/build/staging-deploy")
                    stagingRepositories.add("fixture-kotest/build/staging-deploy")
                    retryDelay = 60
                    maxRetries = 100
                }
            }

        }
    }
}

