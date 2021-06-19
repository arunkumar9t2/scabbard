package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.arunkumar.scabbard.intellij.dagger.prepareDaggerComponentLineMarkerWithFileName
import dev.arunkumar.scabbard.intellij.dagger.psi.hasAnnotation
import dev.arunkumar.scabbard.intellij.dagger.psi.isSubClassOf
import dev.arunkumar.scabbard.intellij.dagger.psi.ktClassOrObject
import dev.arunkumar.scabbard.intellij.dagger.psi.toPsiClass
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinHiltAndroidToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  /***
   * @return true when the given class has Hilt Annotations.
   */
  private fun KtClassOrObject.hasHiltAnnotations(): Boolean {
    return hasAnnotation(DAGGER_HILT_ANDROID_ENTRY_POINT) || hasAnnotation(DAGGER_HILT_ANDROID_APP)
  }

  private fun KtClassOrObject.findHiltGeneratedComponent() = findGeneratedStandardHiltComponent(
    componentClass = this,
    hasAnnotation = { qualifiedName -> hasAnnotation(qualifiedName) },
    isSubClassOf = { qualifiedClassName -> isSubClassOf(this, qualifiedClassName) }
  )

  /**
   * @return `true` when the given `ktClassOrObject` is a subclass of `className`.
   */
  private fun isSubClassOf(ktClassOrObject: KtClassOrObject, qualifiedClassName: String): Boolean {
    return ktClassOrObject.toPsiClass()?.superClass.isSubClassOf(qualifiedClassName)
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
  ) {
    when (element) {
      is LeafPsiElement -> {
        element.ktClassOrObject()?.let { ktClass ->
          if (ktClass.hasHiltAnnotations()) {
            val qualifiedName = ktClass.findHiltGeneratedComponent()?.qualifiedName
            qualifiedName?.let {
              val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
                element = element,
                componentName = ktClass.name!!,
                componentFqcn = qualifiedName
              )
              graphLineMarker?.let { result.add(graphLineMarker) }
            }
          }
        }
      }
    }
  }
}
