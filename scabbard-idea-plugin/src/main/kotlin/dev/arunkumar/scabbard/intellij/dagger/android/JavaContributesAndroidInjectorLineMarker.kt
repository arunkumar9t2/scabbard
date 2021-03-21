package dev.arunkumar.scabbard.intellij.dagger.android

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.impl.source.PsiJavaFileImpl
import dev.arunkumar.scabbard.intellij.dagger.DAGGER_CONTRIBUTES_ANDROID_INJECTOR
import dev.arunkumar.scabbard.intellij.dagger.prepareContributesAndroidInjectorLineMarker

class JavaContributesAndroidInjectorLineMarker : RelatedItemLineMarkerProvider() {

  private fun PsiMethod.findAnnotation(annotationName: String): PsiAnnotation? {
    return annotations.firstOrNull { it.hasQualifiedName(annotationName) }
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    if (element is PsiMethod) {
      // Check if method has @ContributesAndroidInjector
      val crInjector = element.findAnnotation(DAGGER_CONTRIBUTES_ANDROID_INJECTOR)
      if (crInjector != null) {
        val packageName = (element.containingFile as? PsiJavaFileImpl)?.packageName
        val returnTypeSimpleName = (element.returnType as PsiClassReferenceType).className
        val qualifiedPath = element.containingClass?.qualifiedName
        val methodName = element.name
        if (returnTypeSimpleName != null && qualifiedPath != null && packageName != null) {
          val graphLineMarker = prepareContributesAndroidInjectorLineMarker(
            contributesAndroidInjectorElement = crInjector,
            packageName = packageName,
            qualifiedPath = qualifiedPath,
            methodName = methodName,
            returnTypeSimpleName = returnTypeSimpleName
          )
          graphLineMarker?.let { result.add(graphLineMarker) }
        }
      }
    }
  }
}
