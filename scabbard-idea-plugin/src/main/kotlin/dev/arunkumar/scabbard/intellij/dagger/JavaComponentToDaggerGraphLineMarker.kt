package dev.arunkumar.scabbard.intellij.dagger

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.NotNull
import org.jetbrains.kotlin.j2k.getContainingClass

class JavaComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  override fun collectNavigationMarkers(
    element: @NotNull PsiElement,
    result: @NotNull MutableCollection<in RelatedItemLineMarkerInfo<*>>
  ) {
    if (element.isDaggerAnnotationIdentifier()) {
      val psiClass = element.getContainingClass()
      val qualifiedName = psiClass?.qualifiedName
      qualifiedName?.let {
        val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
          element = element,
          componentName = psiClass.name!!,
          fileName = qualifiedName
        )
        graphLineMarker?.let { result.add(graphLineMarker) }
      }
    }
  }
}
