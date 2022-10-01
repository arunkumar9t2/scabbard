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

package dev.arunkumar.scabbard.gradle.util

import dev.arunkumar.scabbard.gradle.plugin.VERSION

const val SCABBARD = "scabbard"
internal const val SCABBARD_PLUGIN_ID = "scabbard.gradle"
internal const val KOTLIN_PLUGIN_ID = "kotlin"
internal const val KAPT_PLUGIN_ID = "kotlin-kapt"
internal const val JAVA_PLUGIN_ID = "java"
internal const val JAVA_LIBRARY_PLUGIN_ID = "java-library"
internal const val ANDROID_APP_PLUGIN_ID = "com.android.application"
internal const val ANDROID_LIBRARY_PLUGIN_ID = "com.android.library"


internal const val SCABBARD_GROUP = "dev.arunkumar"
internal const val SCABBARD_NAME = "scabbard-processor"
internal const val SCABBARD_PROCESSOR_DEPENDENCY = "$SCABBARD_GROUP:$SCABBARD_NAME:$VERSION"
