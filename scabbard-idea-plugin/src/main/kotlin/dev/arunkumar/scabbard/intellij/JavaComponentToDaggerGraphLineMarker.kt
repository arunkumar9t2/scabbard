package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import dev.arunkumar.scabbard.intellij.utill.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.utill.DAGGER_MODULE
import dev.arunkumar.scabbard.intellij.utill.DAGGER_SUBCOMPONENT
import dev.arunkumar.scabbard.intellij.utill.prepareLineMarkerOpenerForFileName
import org.jetbrains.kotlin.j2k.getContainingClass

class JavaComponentToDaggerGraphLineMarker : LineMarkerProvider {

  private val daggerAnnotations = listOf(DAGGER_COMPONENT, DAGGER_SUBCOMPONENT, DAGGER_MODULE)

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

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (element.isDaggerAnnotationIdentifier()) {
      val psiClass = element.getContainingClass()
      val qualifiedName = psiClass?.qualifiedName
      qualifiedName?.let {
        return prepareLineMarkerOpenerForFileName(
          element = element,
          componentName = psiClass.name!!,
          fileName = qualifiedName
        )
      }
    }
    return null
  }
}