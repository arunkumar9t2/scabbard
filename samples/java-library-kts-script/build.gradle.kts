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
  implementation("com.google.dagger:dagger:2.25.4")
  annotationProcessor("com.google.dagger:dagger-compiler:2.25.4")
}