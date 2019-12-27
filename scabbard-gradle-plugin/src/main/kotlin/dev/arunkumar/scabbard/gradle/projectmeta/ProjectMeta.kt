package dev.arunkumar.scabbard.gradle.projectmeta

import org.gradle.api.Project

private const val KOTLIN_ANDROID = "kotlin-android"
const val ANNOTATION_PROCESSOR = "annotationProcessor"

internal val Project.hasJavaAnnotationProcessorConfig
  get() = configurations.findByName(
    ANNOTATION_PROCESSOR
  ) != null

internal val Project.hasKotlinAndroidPlugin get() = plugins.findPlugin(KOTLIN_ANDROID) != null

internal val Project.hasKotlinPlugin get() = plugins.findPlugin("kotlin") != null

internal val Project.isKotlinProject get() = hasKotlinAndroidPlugin || hasKotlinPlugin