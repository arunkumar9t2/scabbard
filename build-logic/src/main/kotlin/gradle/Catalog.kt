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

package gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType


internal val Project.catalogs get() = extensions.getByType<VersionCatalogsExtension>()

internal val Project.deps: VersionCatalog get() = catalogs.named("deps")

internal fun VersionCatalog.version(reference: String): String? = findVersion(reference)
  .orElse(null)
  ?.toString()

internal fun VersionCatalog.dependency(reference: String): String? = findDependency(reference)
  .orElse(null)?.toString()
