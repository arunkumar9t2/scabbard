package dev.arunkumar.scabbard.intellij.dagger

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private fun KtClassOrObject.hasDaggerComponentAnnotations(): Boolean {
    return hasAnnotation(DAGGER_COMPONENT)
      || hasAnnotation(DAGGER_SUBCOMPONENT)
      || hasAnnotation(DAGGER_MODULE)
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    when (element) {
      is LeafPsiElement -> {
        element.ktClassOrObject()?.let { ktClass ->
          if (ktClass.hasDaggerComponentAnnotations()) {
            val componentName = ktClass.name
            val qualifiedName = ktClass.getKotlinFqName().toString()
            val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
              element = element,
              componentName = componentName!!,
              fileName = qualifiedName
            )
            graphLineMarker?.let { result.add(graphLineMarker) }
          }
        }
      }
    }
  }
}
