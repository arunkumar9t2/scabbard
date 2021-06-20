package dev.arunkumar.scabbard.gradle.compilerproperties

import com.google.common.truth.Truth.assertThat
import dev.arunkumar.scabbard.gradle.KAPT_PLUGIN_APPLICATION
import dev.arunkumar.scabbard.gradle.KOLTIN_PLUGIN_APPLICATION
import dev.arunkumar.scabbard.gradle.KOTLIN_VERSION
import dev.arunkumar.scabbard.gradle.ScabbardFunctionalTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import java.io.File

class CompilerPropertiesFunctionalTest : ScabbardFunctionalTest() {

  @Test
  fun `assert compiler properties are applied in kotlin project`() {
    addSimpleKtSourceFile()
    projectFile(
      path = "build.gradle",
      content = """
              | plugins {
              |   id "java-library"
              |   $KOLTIN_PLUGIN_APPLICATION
              |   $KAPT_PLUGIN_APPLICATION
              |   id "scabbard.gradle"
              | }
              | 
              | repositories {
              |   mavenCentral()
              |   jcenter()
              | }
              | 
              | scabbard {
              |    enabled true
              |    outputFormat "svg"
              | }
              | 
              | dependencies {
              |   implementation "org.jetbrains.kotlin:kotlin-stdlib:$KOTLIN_VERSION"
              |   implementation "com.google.dagger:dagger:+"
              |   kapt "com.google.dagger:dagger-compiler:+"
              | }
            """.trimMargin()
    )
    val buildResult = gradleRunner
      .withDebug(true)
      .withArguments(":kaptKotlin")
      .build().output
    assertThat(buildResult)
      .doesNotContain("The following options were not recognized by any processor")
    assertThat(
      File(
        testProjectDir.root,
        "/build/generated/source/kapt/main/scabbard/scabbard.SimpleComponent.svg"
      ).exists()
    ).isTrue()
  }

  @Test(expected = AssertionError::class) // https://github.com/arunkumar9t2/scabbard/issues/49
  fun `assert compiler properties are applied only when dagger is present in kotlin project`() {
    projectFile(
      path = "src/main/kotlin/scabbard/main.kt",
      content = """
        package scabbard
        fun main(){}
      """.trimIndent()
    )
    projectFile(
      path = "build.gradle",
      content = """
              | plugins {
              |   id "java-library"
              |   $KOLTIN_PLUGIN_APPLICATION
              |   $KAPT_PLUGIN_APPLICATION
              |   id "scabbard.gradle"
              | }
              | 
              | repositories {
              |   mavenCentral()
              |   jcenter()
              | }
              | 
              | scabbard {
              |    enabled true
              |    fullBindingGraphValidation true
              | }
              | 
              | dependencies {
              |   implementation "org.jetbrains.kotlin:kotlin-stdlib:$KOTLIN_VERSION"
              |   implementation "com.google.dagger:dagger:+"
              |   kapt "com.google.dagger:dagger-compiler:+"
              | }
            """.trimMargin()
    )
    val buildResult = gradleRunner
      .withDebug(true)
      .withArguments(":assemble")
      .build().output
    assertThat(buildResult)
      .doesNotContain("The following options were not recognized by any processor")
  }

  @Test
  fun `assert java compiler properties are applied in java project`() {
    addSimpleJavaSourceFile()
    projectFile(
      path = "build.gradle",
      content = """
              | plugins {
              |   id "java-library"
              |   id "scabbard.gradle"
              | }
              | 
              | repositories {
              |   mavenCentral()
              |   jcenter()
              | }
              | 
              | scabbard {
              |    enabled true
              |    outputFormat "svg"
              | }
              | 
              | dependencies {
              |   implementation "com.google.dagger:dagger:+"
              |   annotationProcessor "com.google.dagger:dagger-compiler:+"
              | }
            """.trimMargin()
    )
    val buildResult = gradleRunner
      .withDebug(true)
      .withArguments(":assemble")
      .build()
    assertThat(buildResult.task(":assemble")?.outcome == TaskOutcome.SUCCESS)
    assertThat(
      File(
        testProjectDir.root,
        "build/generated/sources/annotationProcessor/java/main/scabbard/scabbard.ScabbardSample.SimpleComponent.svg"
      ).exists()
    ).isTrue()
  }
}
