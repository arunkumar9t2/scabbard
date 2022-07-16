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

import ANDROID_PACKAGE_NAME
import ANDROID_VERSION_CODE
import ANDROID_VERSION_NAME
import com.android.build.api.dsl.ApplicationExtension
import gradle.ConfigurablePlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

public class AndroidBinary : ConfigurablePlugin({
  apply(plugin = "com.android.application")

  androidCommon()

  configure<ApplicationExtension> {
    defaultConfig {
      applicationId = ANDROID_PACKAGE_NAME
      versionCode = ANDROID_VERSION_CODE
      versionName = ANDROID_VERSION_NAME
    }
  }
})

