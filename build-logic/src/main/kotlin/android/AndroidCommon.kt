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

package android

import ANDROID_COMPILE_SDK
import ANDROID_MIN_SDK
import ANDROID_RELEASE_VARIANT
import ANDROID_TARGET_SDK
import gradle.deps
import gradle.version
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.androidCommon() {
  apply(plugin = "org.jetbrains.kotlin.android")

  android {
    compileSdkVersion(ANDROID_COMPILE_SDK)

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
      minSdk = ANDROID_MIN_SDK
      targetSdk = ANDROID_TARGET_SDK

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      vectorDrawables {
        useSupportLibrary = true
      }
    }

    buildTypes {
      named(ANDROID_RELEASE_VARIANT) {
        proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro"
        )
      }
    }

    composeOptions {
      kotlinCompilerExtensionVersion = deps.version("compose")!!
    }

    packagingOptions {
      resources.excludes += listOf(
        "META-INF/AL2.0",
        "META-INF/LGPL2.1",
        "META-INF/licenses/**"
      )
    }
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.ExperimentalStdlibApi",
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.time.ExperimentalTime",
        "-Xopt-in=kotlin.experimental.ExperimentalTypeInference",
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        //"-Xexplicit-api=strict" // TODO Uncomment if strict API is needed
      )
    }
  }
}
