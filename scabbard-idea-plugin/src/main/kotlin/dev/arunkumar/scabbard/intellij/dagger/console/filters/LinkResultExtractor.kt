package dev.arunkumar.scabbard.intellij.dagger.console.filters

import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache

interface LinkResultExtractor {
  fun extract(daggerComponent: DaggerComponent): Filter.Result?
}

class DefaultLinkExtractor(private val project: Project) : LinkResultExtractor {
  override fun extract(daggerComponent: DaggerComponent): Filter.Result? {
    // Find the class
    PsiShortNamesCache
      .getInstance(project)
      .getClassesByName(daggerComponent.name, GlobalSearchScope.allScope(project))
      .firstOrNull()
      ?.let { psiClass ->
        // Create Filter Result
      }
    return null
  }
}
