package dev.arunkumar.scabbard.intellij.dagger

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.arunkumar.scabbard.intellij.dagger.psi.DAGGER_COMPONENT_ANNOTATIONS
import dev.arunkumar.scabbard.intellij.dagger.psi.hasAnnotation
import dev.arunkumar.scabbard.intellij.dagger.psi.ktClassOrObject
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private fun KtClassOrObject.hasDaggerComponentAnnotations(): Boolean {
    return DAGGER_COMPONENT_ANNOTATIONS.any { daggerAnnotation -> hasAnnotation(daggerAnnotation) }
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
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
              componentFqcn = qualifiedName
            )
            graphLineMarker?.let { result.add(graphLineMarker) }
          }
        }
      }
    }
  }
}
