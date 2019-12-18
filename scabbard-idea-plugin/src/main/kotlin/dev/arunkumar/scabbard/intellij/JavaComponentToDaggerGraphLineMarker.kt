package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.impl.source.PsiModifierListImpl
import dev.arunkumar.scabbard.intellij.utill.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.utill.DAGGER_SUBCOMPONENT
import dev.arunkumar.scabbard.intellij.utill.prepareLineMarkerOpenerForFileName


class JavaComponentToDaggerGraphLineMarker : LineMarkerProvider {

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (element.hasDaggerAnnotations()) {
      val psiClassImpl = (element as PsiModifierListImpl).parent as? PsiClassImpl
      val qualifiedName = psiClassImpl?.qualifiedName
      qualifiedName?.let {
        return prepareLineMarkerOpenerForFileName(
          element = element,
          componentName = psiClassImpl.name!!,
          fileName = "$qualifiedName.png"
        )
      }
    }
    return null
  }

  private fun PsiElement.hasDaggerAnnotations(): Boolean {
    return findAnnotation(DAGGER_COMPONENT) != null && findAnnotation(DAGGER_SUBCOMPONENT) != null
  }

  private fun PsiElement.findAnnotation(annotationName: String): PsiAnnotation? {
    return takeIf { this is PsiModifierListImpl }?.run {
      (parent as? PsiClassImpl)
        ?.modifierList
        ?.annotations
        ?.first { it.qualifiedName == annotationName }
    }
  }
}