/*
 * Copyright 2021-2023 Appmattus Limited
 *           2024 Detomarco Limited
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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jreleaser.model.Active

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.kotlin
    id("org.jreleaser")
    id("maven-publish")
    id("signing")
}

apply(from = "$rootDir/gradle/scripts/jacoco.gradle.kts")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.github.classgraph:classgraph:${Versions.classgraph}")
    implementation(kotlin("reflect"))

    compileOnly("joda-time:joda-time:${Versions.jodaTime}")
    testImplementation("joda-time:joda-time:${Versions.jodaTime}")

    compileOnly("org.threeten:threetenbp:${Versions.threeTen}")
    testImplementation("org.threeten:threetenbp:${Versions.threeTen}")

    compileOnly("org.ktorm:ktorm-core:${Versions.kTorm}")
    testImplementation("org.ktorm:ktorm-core:${Versions.kTorm}")

    testImplementation("junit:junit:${Versions.junit4}")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KotlinX.serialization}")

    // Used for ComparisonTest
    @Suppress("GradleDependency")
    testImplementation("com.github.marcellogalhardo:kotlin-fixture:${Versions.marcellogalhardo}")
    testImplementation("com.flextrade.jfixture:kfixture:${Versions.flextrade}")
    testImplementation("org.jeasy:easy-random-core:${Versions.easyrandom}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

tasks.jar{
    enabled = true
    // Remove `plain` postfix from jar file name
    archiveClassifier.set("")
}

publishing{
    publications {
        create<MavenPublication>("Maven") {
            from(components["java"])
            groupId = "com.detomarco.kotlinfixture"
            artifactId = "fixture"
            description = "Fixtures for Kotlin providing generated values for unit testing"
        }
        withType<MavenPublication> {
            pom {
                packaging = "jar"
                name.set("kotlinfixture")
                description.set("Kotlin Fixture")
                url.set("https://github.com/detomarco/kotlinfixture")
                inceptionYear.set("2024")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://spdx.org/licenses/Apache-2.0.html")
                    }
                }
                developers {
                    developer {
                        id.set("detomarco")
                        name.set("Marco De Toma")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/detomarco/kotlinfixture.git")
                    developerConnection.set("scm:git:ssh://github.com/detomarco/kotlinfixture.git")
                    url.set("http://github.com/detomarco/kotlinfixture")
                }
            }
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

jreleaser {
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}

tasks.named("check") {
    finalizedBy(rootProject.tasks.named("detekt"))
}
