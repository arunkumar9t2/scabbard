package dev.arunkumar.scabbard.intellij.dagger

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import org.jetbrains.kotlin.j2k.getContainingClass

class JavaComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private val daggerAnnotations = listOf(
    DAGGER_COMPONENT,
    DAGGER_SUBCOMPONENT,
    DAGGER_MODULE
  )

  /**
   * @return true only when
   * 1. The given PSI element is an `PsiIdentifier`.
   * 2. and is a dagger annotation entry.
   * For example, for `@Component` annotation on a class or an interface, this method will return `true` only for
   * `Component` part when it is represented as a `PsiIdentifier`.
   */
  private fun PsiElement.isDaggerAnnotationIdentifier(): Boolean {
    val psiIdentifier = this as? PsiIdentifier
    return daggerAnnotations.any { daggerAnnotation ->
      (psiIdentifier?.parent?.parent as? PsiAnnotation)?.qualifiedName == daggerAnnotation
    }
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
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