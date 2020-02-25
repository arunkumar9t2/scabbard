import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin
import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
  `java-library`
  application
}
apply(plugin = "scabbard.gradle")
apply(from = "../gradle/scabbard-local-processor.gradle")

configure<JavaPluginExtension> {
  sourceCompatibility = VERSION_1_8
  targetCompatibility = VERSION_1_8
}

configure<ApplicationPluginConvention> {
  mainClassName = "dev.arunkumar.scabbard.javasample.ScabbardSample"
}

configure<ScabbardPluginExtension> {
  enabled = true
  failOnError = false
  fullBindingGraphValidation = true
  outputFormat = "svg"
}

dependencies {
  implementation("com.google.dagger:dagger:2.25.4")
  annotationProcessor("com.google.dagger:dagger-compiler:2.25.4")
}