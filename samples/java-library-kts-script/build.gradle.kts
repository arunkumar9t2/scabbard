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

import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension

plugins {
  `java-library`
  application
}
apply(plugin = "scabbard.gradle")
apply(from = "../../gradle/scabbard-local-processor.gradle")

configure<ApplicationPluginConvention> {
  mainClassName = "dev.arunkumar.scabbard.javasample.ScabbardSample"
}

configure<ScabbardPluginExtension> {
  enabled = true
  qualifiedNames = true
  failOnError = true
  fullBindingGraphValidation = true
  outputFormat = "svg"
}

dependencies {
  // TODO(arun) migrate to kts constants for versions
  implementation("com.google.dagger:dagger:2.33")
  annotationProcessor("com.google.dagger:dagger-compiler:2.33")
}
