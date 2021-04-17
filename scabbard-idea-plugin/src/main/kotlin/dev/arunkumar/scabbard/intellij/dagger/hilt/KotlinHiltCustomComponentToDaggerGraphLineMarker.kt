package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.arunkumar.scabbard.intellij.dagger.hasAnnotation
import dev.arunkumar.scabbard.intellij.dagger.ktClassOrObject
import dev.arunkumar.scabbard.intellij.dagger.prepareDaggerComponentLineMarkerWithFileName
import dev.arunkumar.scabbard.intellij.dagger.toPsiClass
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Provides gutters for `@DefineComponent` and `@EntryPoint`.
 */
class KotlinHiltCustomComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private fun KtClassOrObject.hasCustomHiltComponentAnnotations(): Boolean {
    return hasAnnotation(DAGGER_HILT_DEFINE_COMPONENT) || hasAnnotation(DAGGER_HILT_ENTRY_POINT)
  }

  private fun KtClassOrObject.findGeneratedCustomHiltComponent(): PsiClass? {
    return toPsiClass()?.let { findGeneratedCustomHiltComponent(it) }
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
  ) {
    when (element) {
      is LeafPsiElement -> {
        element.ktClassOrObject()?.let { ktClass ->
          if (ktClass.hasCustomHiltComponentAnnotations()) {
            val qualifiedName = ktClass.findGeneratedCustomHiltComponent()?.qualifiedName
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
