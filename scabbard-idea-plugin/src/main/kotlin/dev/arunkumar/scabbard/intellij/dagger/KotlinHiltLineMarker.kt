package dev.arunkumar.scabbard.intellij.dagger

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinHiltLineMarker : RelatedItemLineMarkerProvider() {

  private fun KtClassOrObject.hasHiltAnnotations(): Boolean {
    return findAnnotation(FqName(DAGGER_ANDROID_ENTRY_POINT)) != null
        || findAnnotation(FqName(DAGGER_HILT_ANDROID_APP)) != null
  }

  private fun KtClassOrObject.findGeneratedComponent(): PsiClass? {
    val psiShortNamesCache = PsiShortNamesCache.getInstance(project)
    val searchScope = GlobalSearchScope.projectScope(project)
    val hiltComponents = "HiltComponents"
    return when {
      findAnnotation(FqName(DAGGER_HILT_ANDROID_APP)) != null -> {
        psiShortNamesCache
          .getClassesByName("ApplicationC", searchScope)
          .firstOrNull { it.containingClass?.qualifiedName?.endsWith(hiltComponents) == true }
      }
      else -> null
    }
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    when (element) {
      is LeafPsiElement -> {
        element.ktClassOrObject()?.let { ktClass ->
          if (ktClass.hasHiltAnnotations()) {
            val qualifiedName = ktClass.findGeneratedComponent()?.qualifiedName
            qualifiedName?.let {
              val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
                element = element,
                componentName = ktClass.name!!,
                fileName = qualifiedName
              )
              graphLineMarker?.let { result.add(graphLineMarker) }
            }
          }
        }
      }
    }
  }
}