package dev.arunkumar.scabbard.gradle.util

import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import kotlin.properties.Delegates.observable

/**
 * Property delegate that notifies [ScabbardPluginExtension.onEnabledStatusChange] whenever
 * the extension is set. The broadcast happens even if value itself did not change.
 */
internal fun ScabbardPluginExtension.enabledProperty() =
  observable(false) { _, _, newValue ->
    onEnabledStatusChange.execute(newValue)
  }