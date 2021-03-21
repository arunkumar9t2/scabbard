package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import dev.arunkumar.scabbard.intellij.dagger.isDaggerAnnotationIdentifier
import dev.arunkumar.scabbard.intellij.dagger.prepareDaggerComponentLineMarkerWithFileName
import org.jetbrains.kotlin.j2k.getContainingClass

class JavaHiltCustomComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private val customComponentHiltAnnotations = listOf(
    DAGGER_HILT_DEFINE_COMPONENT,
    DAGGER_HILT_ENTRY_POINT
  )

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    if (element.isDaggerAnnotationIdentifier(customComponentHiltAnnotations)) {
      element.getContainingClass()?.let { componentClass ->
        val qualifiedName = findGeneratedCustomHiltComponent(componentClass)?.qualifiedName
        qualifiedName?.let {
          val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
            element = element,
            componentName = componentClass.name!!,
            fileName = qualifiedName
          )
          graphLineMarker?.let { result.add(graphLineMarker) }
        }
      }
    }
  }
}
