package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.impl.source.PsiJavaFileImpl
import dev.arunkumar.scabbard.intellij.utill.DAGGER_CONTRIBUTES_ANDROID_INJECTOR
import dev.arunkumar.scabbard.intellij.utill.prepareContributesAndroidInjectorLineMarker

class JavaContributesAndroidInjectorLineMarker : LineMarkerProvider {

  private fun PsiMethod.findAnnotation(annotationName: String): PsiAnnotation? {
    return annotations.firstOrNull { it.hasQualifiedName(annotationName) }
  }

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (element is PsiMethod) {
      // Check if method has @ContributesAndroidInjector
      val crInjector = element.findAnnotation(DAGGER_CONTRIBUTES_ANDROID_INJECTOR)
      if (crInjector != null) {
        val packageName = (element.containingFile as? PsiJavaFileImpl)?.packageName
        val returnTypeSimpleName = (element.returnType as PsiClassReferenceType).className
        val qualifiedPath = element.containingClass?.qualifiedName
        val methodName = element.name
        if (returnTypeSimpleName != null && qualifiedPath != null && packageName != null) {
          return prepareContributesAndroidInjectorLineMarker(
            contributesAndroidInjectorElement = crInjector,
            packageName = packageName,
            qualifiedPath = qualifiedPath,
            methodName = methodName,
            returnTypeSimpleName = returnTypeSimpleName
          )
        }
      }
    }
    return null
  }
}