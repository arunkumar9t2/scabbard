package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import dev.arunkumar.scabbard.intellij.dagger.ANDROIDX_COMPONENT_ACTIVITY
import dev.arunkumar.scabbard.intellij.dagger.ANDROIDX_FRAGMENT
import dev.arunkumar.scabbard.intellij.dagger.ANDROID_SERVICE
import dev.arunkumar.scabbard.intellij.dagger.ANDROID_VIEW
import dev.arunkumar.scabbard.intellij.dagger.hilt.HiltComponent.*

const val DAGGER_ANDROID_ENTRY_POINT = "dagger.hilt.android.AndroidEntryPoint"
const val DAGGER_HILT_ANDROID_APP = "dagger.hilt.android.HiltAndroidApp"
const val WITH_FRAGMENT_BINDINGS = "dagger.hilt.android.WithFragmentBindings"

enum class HiltComponent {
  ApplicationC,
  ServiceC,
  ActivityC,
  ActivityRetainedC,
  FragmentC,
  ViewWithFragmentC,
  ViewC
}

fun PsiShortNamesCache.findHiltComponentByClassName(project: Project, shortClassName: String): PsiClass? {
  val hiltComponents = "HiltComponents"
  val searchScope = GlobalSearchScope.projectScope(project)
  return getClassesByName(shortClassName, searchScope)
    .firstOrNull { it.containingClass?.qualifiedName?.endsWith(hiltComponents) == true }
}

/**
 * Utility function to find the Hilt generated component of the given `@AndroidEntryPoint` or `@HiltAndroidApp` annotated
 * class.
 *
 * @param T The class for which the generated component needs to be found. It is either `PsiClass` or `KtClassOrObject`
 * @param hasAnnotation Check if `T` has the given annotation
 * @param isSubClassOf Check if `T` is a subclass of given class name.
 */
fun <T : PsiElement> findGeneratedHiltComponent(
  psiElement: T,
  hasAnnotation: T.(String) -> Boolean,
  isSubClassOf: T.(String) -> Boolean
): PsiClass? {
  val project = psiElement.project
  val shortNamesCache = PsiShortNamesCache.getInstance(project)

  /**
   * Find the `PsiClass` of the `HiltComponent` using the `shortNamesCache`.
   */
  fun HiltComponent.findClass(): PsiClass? {
    return shortNamesCache.findHiltComponentByClassName(project, name)
  }
  return when {
    psiElement.hasAnnotation(DAGGER_HILT_ANDROID_APP) -> ApplicationC.findClass()
    psiElement.hasAnnotation(DAGGER_ANDROID_ENTRY_POINT) -> {
      when {
        psiElement.isSubClassOf(ANDROIDX_COMPONENT_ACTIVITY) -> ActivityC.findClass()
        psiElement.isSubClassOf(ANDROIDX_FRAGMENT) -> FragmentC.findClass()
        psiElement.isSubClassOf(ANDROID_SERVICE) -> ServiceC.findClass()
        psiElement.isSubClassOf(ANDROID_VIEW) -> {
          if (psiElement.hasAnnotation(WITH_FRAGMENT_BINDINGS)) {
            ViewWithFragmentC.findClass()
          } else {
            ViewC.findClass()
          }
        }
        else -> null
      }
    }
    else -> null
  }
}