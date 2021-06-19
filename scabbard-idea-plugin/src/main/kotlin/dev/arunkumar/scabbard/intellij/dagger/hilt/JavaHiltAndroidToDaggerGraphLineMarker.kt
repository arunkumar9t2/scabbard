package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import dev.arunkumar.scabbard.intellij.dagger.prepareDaggerComponentLineMarkerWithFileName
import dev.arunkumar.scabbard.intellij.dagger.psi.isDaggerAnnotationIdentifier
import dev.arunkumar.scabbard.intellij.dagger.psi.isSubClassOf
import org.jetbrains.kotlin.j2k.getContainingClass

class JavaHiltAndroidToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private val hiltAnnotations = listOf(
    DAGGER_HILT_ANDROID_ENTRY_POINT,
    DAGGER_HILT_ANDROID_APP
  )

  private fun PsiClass.findHiltGeneratedComponent() = findGeneratedStandardHiltComponent(
    componentClass = this,
    hasAnnotation = { qualifiedName -> hasAnnotation(qualifiedName) },
    isSubClassOf = { qualifiedClassName -> isSubClassOf(qualifiedClassName) }
  )

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
  ) {
    if (element.isDaggerAnnotationIdentifier(hiltAnnotations)) {
      val psiClass = element.getContainingClass()
      val qualifiedName = psiClass?.findHiltGeneratedComponent()?.qualifiedName
      qualifiedName?.let {
        val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
          element = element,
          componentName = psiClass.name!!,
          componentFqcn = qualifiedName
        )
        graphLineMarker?.let { result.add(graphLineMarker) }
      }
    }
  }
}
