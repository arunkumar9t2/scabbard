package dev.arunkumar.scabbard.intellij.dagger.console.filters

import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

/**
 * [Filter] to create links to Dagger component's full dependency graph by parsing Dagger's
 * MissingBinding error on the console.
 */
class MissingBindingFilter(
  private val project: Project,
  private val missingBindingComponentExtractor: MissingBindingComponentExtractor = DefaultMissingBindingComponentExtractor(),
  private val linkExtractor: LinkResultExtractor = DefaultLinkExtractor(project)
) : Filter {
  override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
    val components = missingBindingComponentExtractor.extract(line, entireLength)
    return if (components.isNotEmpty()) {
      linkExtractor.extract(components.first())
    } else null
  }
}
