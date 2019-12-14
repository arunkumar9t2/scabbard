package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod

class KotlinComponentToDaggerGraphLineMarker : LineMarkerProvider {

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (element is PsiMethod) {

    }
    return null
  }
}