/*
 * Copyright 2021 Arunkumar
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
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("java-gradle-plugin")
  `kotlin-dsl`
  id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

kotlin {
  explicitApi = ExplicitApiMode.Strict
}

gradlePlugin {
  plugins {
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
}
