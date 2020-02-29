package dev.arunkumar.scabbard.gradle.util

import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import kotlin.properties.Delegates.observable

internal fun ScabbardPluginExtension.enabledProperty() =
  observable(false) { _, oldValue, newValue ->
    if (oldValue != newValue) {
      onEnabled.execute(newValue)
    }
  }