package dev.arunkumar.scabbard.intellij.dagger.console.filters

import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.util.PsiNavigateUtil
import dev.arunkumar.scabbard.intellij.dagger.FULL_BINDING_GRAPH_FILES
import dev.arunkumar.scabbard.intellij.dagger.searchGeneratedDaggerFiles

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
        val componentFqcn = psiClass.qualifiedName!!
        val matchedFiles = searchGeneratedDaggerFiles(
          project = project,
          componentFqcn = componentFqcn,
          formats = FULL_BINDING_GRAPH_FILES.asSequence() // Missing binding will be only present in full binding graph
        )
        if (matchedFiles.isNotEmpty()) {
          val fullGraphFile = matchedFiles.first()
          return Filter.Result(
            daggerComponent.highlightStartOffset,
            daggerComponent.highlightEndOffset
          ) {
            PsiNavigateUtil.navigate(fullGraphFile)
          }
        }
      }
    return null
  }
}
