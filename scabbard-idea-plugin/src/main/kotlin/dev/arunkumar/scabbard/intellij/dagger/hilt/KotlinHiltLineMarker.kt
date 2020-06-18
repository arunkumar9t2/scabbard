package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import dev.arunkumar.scabbard.intellij.dagger.*
import dev.arunkumar.scabbard.intellij.dagger.hilt.HiltComponent.*
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinHiltLineMarker : RelatedItemLineMarkerProvider() {

  /***
   * @return true when the given class has Hilt Annotations.
   */
  private fun KtClassOrObject.hasHiltAnnotations(): Boolean {
    return hasAnnotation(DAGGER_ANDROID_ENTRY_POINT) || hasAnnotation(DAGGER_HILT_ANDROID_APP)
  }

  /**
   * Given a Kotlin class annotated with `DAGGER_ANDROID_ENTRY_POINT` or `DAGGER_HILT_ANDROID_APP`, will
   * find the generated Hilt component for it and return the `PsiClass` of that component.
   */
  private fun KtClassOrObject.findGeneratedComponent(): PsiClass? {
    val shortNamesCache = PsiShortNamesCache.getInstance(project)

    /**
     * Find the `PsiClass` of the `HiltComponent` using the `shortNamesCache`.
     */
    fun HiltComponent.findClass(): PsiClass? {
      return shortNamesCache.findHiltComponentByClassName(project, name)
    }

    fun isSubClassOf(className: String) = isSubClassOf(this, className)

    return when {
      hasAnnotation(DAGGER_HILT_ANDROID_APP) -> ApplicationC.findClass()
      hasAnnotation(DAGGER_ANDROID_ENTRY_POINT) -> {
        when {
          isSubClassOf(ANDROIDX_COMPONENT_ACTIVITY) -> ActivityC.findClass()
          isSubClassOf(ANDROIDX_FRAGMENT) -> FragmentC.findClass()
          isSubClassOf(ANDROID_SERVICE) -> ServiceC.findClass()
          isSubClassOf(ANDROID_VIEW) -> {
            if (hasAnnotation(WITH_FRAGMENT_BINDINGS))
              ViewWithFragmentC.findClass()
            else ViewC.findClass()
          }
          else -> null
        }
      }
      else -> null
    }
  }

  /**
   * @return `true` when the given `ktClassOrObject` is a subclass of `className`.
   */
  private fun isSubClassOf(ktClassOrObject: KtClassOrObject, className: String): Boolean {
    val project = ktClassOrObject.project
    val javaPsiFacade = JavaPsiFacade.getInstance(project)
    val searchScope = GlobalSearchScope.allScope(project)
    var currSuperClass: PsiClass? = ktClassOrObject.fqName?.asString()
      ?.let { qualifiedName -> javaPsiFacade.findClass(qualifiedName, searchScope)?.superClass }
    do {
      when (currSuperClass?.qualifiedName) {
        className -> return true
        else -> currSuperClass = currSuperClass?.superClass
      }
    } while (currSuperClass != null)
    return false
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    when (element) {
      is LeafPsiElement -> {
        element.ktClassOrObject()?.let { ktClass ->
          if (ktClass.hasHiltAnnotations()) {
            val qualifiedName = ktClass.findGeneratedComponent()?.qualifiedName
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