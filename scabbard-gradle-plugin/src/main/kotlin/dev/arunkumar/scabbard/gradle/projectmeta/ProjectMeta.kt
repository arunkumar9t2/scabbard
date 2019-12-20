package dev.arunkumar.scabbard.gradle.projectmeta

import org.gradle.api.Project

private const val KOTLIN_ANDROID = "kotlin-android"
const val ANNOTATION_PROCESSOR = "annotationProcessor"

val Project.hasJavaAnnotationProcessorConfig get() = configurations.findByName(ANNOTATION_PROCESSOR) != null

val Project.hasKotlinAndroidPlugin get() = plugins.findPlugin(KOTLIN_ANDROID) != null

val Project.hasKotlinPlugin get() = plugins.findPlugin("kotlin") != null

val Project.isKotlinProject get() = hasKotlinAndroidPlugin || hasKotlinPlugin