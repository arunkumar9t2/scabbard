package dev.arunkumar.scabbard.intellij.dagger.console.filters

import com.intellij.execution.filters.Filter

/**
 * [Filter] to create links to Dagger component's full dependency graph by parsing Dagger's
 * MissingBinding error on the console.
 */
class MissingBindingFilter : Filter {
  override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
    return null
  }
}
