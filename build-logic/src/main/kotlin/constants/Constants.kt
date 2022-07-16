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
// TODO Update Android compile, min and target SDK versionss
public const val ANDROID_COMPILE_SDK: Int = 32
public const val ANDROID_MIN_SDK: Int = 25
public const val ANDROID_TARGET_SDK: Int = 30

// TODO Update Android package name
public const val ANDROID_PACKAGE_NAME: String = "dev.arunkumar.relic"
public const val ANDROID_RELEASE_VARIANT: String = "release"
public const val ANDROID_DEBUG_VARIANT: String = "debug"
public const val ANDROID_VERSION_CODE: Int = 1
public const val ANDROID_VERSION_NAME: String = "1.0"

public typealias ModuleVersion = Map<String, String>

@OptIn(ExperimentalStdlibApi::class)
public val ModuleVersions: ModuleVersion = buildMap {
  // TODO Configure module specific versions
  // Key - gradle module name
  // Value - publishing version name
  put("library", "0.1.0")
}
