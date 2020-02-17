package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiModifierList
import dev.arunkumar.scabbard.intellij.utill.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.utill.DAGGER_MODULE
import dev.arunkumar.scabbard.intellij.utill.DAGGER_SUBCOMPONENT
import dev.arunkumar.scabbard.intellij.utill.prepareLineMarkerOpenerForFileName


class JavaComponentToDaggerGraphLineMarker : LineMarkerProvider {

  private fun PsiElement.hasDaggerAnnotations(): Boolean {
    return findAnnotation(DAGGER_COMPONENT) != null
        || findAnnotation(DAGGER_SUBCOMPONENT) != null
        || findAnnotation(DAGGER_MODULE) != null
  }

  private fun PsiElement.findAnnotation(annotationName: String): PsiAnnotation? {
    return takeIf { this is PsiModifierList }?.run {
      (parent as? PsiClass)
        ?.modifierList
        ?.annotations
        ?.firstOrNull { it.qualifiedName == annotationName }
    }
  }

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (element.hasDaggerAnnotations()) {
      val psiClass = (element as PsiModifierList).parent as? PsiClass
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