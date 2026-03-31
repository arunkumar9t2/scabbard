/*
 * Copyright 2022 Arunkumar
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

plugins {
  id("java-gradle-plugin")
  `kotlin-dsl`
  id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    freeCompilerArgs.addAll(
      "-opt-in=kotlin.ExperimentalStdlibApi",
      "-opt-in=kotlin.RequiresOptIn",
      "-opt-in=kotlin.time.ExperimentalTime",
      "-opt-in=kotlin.experimental.ExperimentalTypeInference",
      "-Xexplicit-api=strict"
    )
  }
}
gradlePlugin {
  plugins {
    create("javaLibrary") {
      id = "java-library-plugin"
      implementationClass = "javaplugin.JavaLibrary"
    }
    create("kotlinLibrary") {
      id = "kotlin-library-plugin"
      implementationClass = "kt.KotlinLibrary"
    }
    create("androidLibrary") {
      id = "android-library-plugin"
      implementationClass = "android.AndroidLibrary"
    }
    create("androidBinary") {
      id = "android-binary-plugin"
      implementationClass = "android.AndroidBinary"
    }
    create("buildCommon") {
      id = "build-common"
      implementationClass = "common.BuildCommonPlugin"
    }
    create("publishingCommon") {
      id = "publish-common"
      implementationClass = "publish.PublishingCommon"
    }
    create("publishing") {
      id = "publish"
      implementationClass = "publish.PublishingLibrary"
    }
  }
}

dependencies {
  implementation(deps.gradle.dependency.updates)
  implementation(deps.agp)
  implementation(deps.kotlin)
  implementation(deps.spotless)
  implementation(deps.dokka)
  implementation(deps.nexus.publish)
  implementation(deps.kotlinx.binaryvalidator)
  implementation(deps.dagger.hilt.gradle)
  implementation(deps.dagger.anvil)
}
