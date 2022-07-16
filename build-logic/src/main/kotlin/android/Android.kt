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

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

public fun Project.android(builder: BaseExtension.() -> Unit) {
  configure(builder)
}

internal fun Project.androidComponents(
  builder: AndroidComponentsExtension<*, *, *>.() -> Unit
) {
  configure<LibraryAndroidComponentsExtension> { builder(this) }
  configure<ApplicationAndroidComponentsExtension> { builder(this) }
  // TODO(arun) Add test and dynamic features?
}
