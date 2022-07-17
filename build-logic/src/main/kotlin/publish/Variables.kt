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

// TODO Ensure local.properties is present with the following variables for maven publishing
internal const val OSSRH_USERNAME = "OSSRH_USERNAME"
internal const val OSSRH_PASSWORD = "OSSRH_PASSWORD"
internal const val SONATYPE_STAGING_PROFILE_ID = "SONATYPE_STAGING_PROFILE_ID"
internal const val SIGNING_KEY_ID = "SIGNING_KEY_ID"
internal const val SIGNING_KEY = "SIGNING_KEY"
internal const val SIGNING_PASSWORD = "SIGNING_PASSWORD"


internal val PublishVariables = listOf(
  OSSRH_USERNAME,
  OSSRH_PASSWORD,
  SONATYPE_STAGING_PROFILE_ID,
  SIGNING_KEY_ID,
  SIGNING_KEY,
  SIGNING_PASSWORD,
)
