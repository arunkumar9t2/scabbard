package dev.arunkumar.scabbard.intellij.dagger.console

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project
import dev.arunkumar.scabbard.intellij.dagger.console.filters.MissingBindingFilter

/**
 * [ConsoleFilterProvider] that adds links generated images by parsing console logs
 */
class ScabbardConsoleFilterProvider : ConsoleFilterProvider {
  override fun getDefaultFilters(project: Project): Array<Filter> {
    return arrayOf(MissingBindingFilter())
  }
}
