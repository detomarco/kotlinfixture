/*
 * Copyright 2021-2023 Appmattus Limited
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

import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jreleaser.model.Active
import org.jreleaser.model.internal.project.Project
import java.net.URI

plugins {
    kotlin("jvm") version Versions.kotlin apply false
    id("io.gitlab.arturbosch.detekt") version Versions.detektGradlePlugin
    id("org.jetbrains.dokka") version Versions.dokkaPlugin
    id("org.jreleaser") version "1.13.1"
    id("signing")
}

buildscript {
    repositories {
        google()
    }
}

apply(from = "$rootDir/gradle/scripts/dependencyUpdates.gradle.kts")
apply(from = "$rootDir/owaspDependencyCheck.gradle.kts")

allprojects {
    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }

    group = "com.detomarco.kotlinfixture"
//    version = (System.getenv("GITHUB_REF") ?: System.getProperty("GITHUB_REF"))
//        ?.replaceFirst("refs/tags/", "") ?: "unspecified"

    version = "0.0.1"

    plugins.withType<DokkaPlugin> {
        tasks.withType<DokkaTask>().configureEach {
            dokkaSourceSets {
                configureEach {
                    skipDeprecated.set(true)

                    if (name.startsWith("ios")) {
                        displayName.set("ios")
                    }

                    sourceLink {
                        localDirectory.set(rootDir)
                        remoteUrl.set(URI("https://github.com/detomarco/kotlinfixture/blob/main").toURL())
                        remoteLineSuffix.set("#L")
                    }
                }
            }
        }
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detektGradlePlugin}")
}

detekt {
    source = files(fileTree(projectDir).matching {
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
    }.files)

    buildUponDefaultConfig = true

    autoCorrect = true

    config = files("detekt-config.yml")
}

jreleaser {
    project {
        license = "APACHE-2.0"
        authors = listOf("Appmattus", "detomarco")
        copyright = "2019-2023 Appmattus, 2024 detomarco"
        description = "Fixtures for Kotlin providing generated values for unit testing"
    }
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
    }
    deploy {
        maven {
            nexus2 {
                create("maven-central") {
                    active.set(Active.ALWAYS)
                    url.set("https://s01.oss.sonatype.org/service/local")
                    closeRepository.set(false)
                    releaseRepository.set(false)
                    stagingRepositories.add("build/staging-deploy")
                }
            }
        }
    }
}
