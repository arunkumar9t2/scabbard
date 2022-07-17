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

package publish

import ModuleVersions
import gradle.ConfigurablePlugin
import io.github.gradlenexus.publishplugin.NexusPublishExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import java.io.File
import java.io.FileInputStream
import java.util.*

public class PublishingCommon : ConfigurablePlugin({
  apply(plugin = "io.github.gradle-nexus.publish-plugin")

  val localProperties: File = file("local.properties")
  if (localProperties.exists()) {
    FileInputStream(localProperties)
      .use { p -> Properties().apply { load(p) } }
      .forEach { key, value -> extra.set(key.toString(), value) }
  } else {
    PublishVariables.forEach { variable ->
      extra[variable] = providers
        .environmentVariable(variable)
        .forUseAtConfigurationTime()
        .getOrElse("")
    }
  }

  val versions = ModuleVersions

  allprojects {
    group = findProperty("groupId").toString()
    if (versions[name] != null) {
      version = if (hasProperty("snapshot")) "main-SNAPSHOT" else versions[name]!!.toString()
    }
  }

  configure<NexusPublishExtension> {
    repositories {
      sonatype {
        stagingProfileId.set(extra[SONATYPE_STAGING_PROFILE_ID].toString())
        username.set(extra[OSSRH_USERNAME].toString())
        password.set(extra[OSSRH_PASSWORD].toString())
        nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
        snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
      }
    }
  }
})
