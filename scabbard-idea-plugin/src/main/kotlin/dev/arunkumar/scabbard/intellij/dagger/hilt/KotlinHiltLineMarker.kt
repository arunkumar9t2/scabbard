package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.GlobalSearchScope
import dev.arunkumar.scabbard.intellij.dagger.hasAnnotation
import dev.arunkumar.scabbard.intellij.dagger.isSubClassOf
import dev.arunkumar.scabbard.intellij.dagger.ktClassOrObject
import dev.arunkumar.scabbard.intellij.dagger.prepareDaggerComponentLineMarkerWithFileName
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinHiltLineMarker : RelatedItemLineMarkerProvider() {

  /***
   * @return true when the given class has Hilt Annotations.
   */
  private fun KtClassOrObject.hasHiltAnnotations(): Boolean {
    return hasAnnotation(DAGGER_ANDROID_ENTRY_POINT) || hasAnnotation(DAGGER_HILT_ANDROID_APP)
  }

  private fun KtClassOrObject.findHiltGeneratedComponent() = findGeneratedHiltComponent(
    psiElement = this,
    hasAnnotation = { qualifiedName -> hasAnnotation(qualifiedName) },
    isSubClassOf = { qualifiedClassName -> isSubClassOf(this, qualifiedClassName) }
  )

  /**
   * @return `true` when the given `ktClassOrObject` is a subclass of `className`.
   */
  private fun isSubClassOf(ktClassOrObject: KtClassOrObject, qualifiedClassName: String): Boolean {
    val project = ktClassOrObject.project
    val javaPsiFacade = JavaPsiFacade.getInstance(project)
    val searchScope = GlobalSearchScope.allScope(project)
    return ktClassOrObject.fqName?.asString()
      ?.let { qualifiedName -> javaPsiFacade.findClass(qualifiedName, searchScope)?.superClass }
      .isSubClassOf(qualifiedClassName)
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
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
                fileName = qualifiedName
              )
              graphLineMarker?.let { result.add(graphLineMarker) }
            }
          }
        }
      }
    }
  }
}