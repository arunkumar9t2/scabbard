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
