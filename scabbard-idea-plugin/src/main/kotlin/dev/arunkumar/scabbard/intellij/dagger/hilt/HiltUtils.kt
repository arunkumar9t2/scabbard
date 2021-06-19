package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.search.searches.ClassInheritorsSearch
import dev.arunkumar.scabbard.intellij.dagger.ANDROIDX_COMPONENT_ACTIVITY
import dev.arunkumar.scabbard.intellij.dagger.ANDROIDX_FRAGMENT
import dev.arunkumar.scabbard.intellij.dagger.ANDROID_SERVICE
import dev.arunkumar.scabbard.intellij.dagger.ANDROID_VIEW
import dev.arunkumar.scabbard.intellij.dagger.hilt.HiltComponent.*

internal const val DAGGER_HILT_ANDROID_ENTRY_POINT = "dagger.hilt.android.AndroidEntryPoint"
internal const val DAGGER_HILT_ANDROID_APP = "dagger.hilt.android.HiltAndroidApp"
internal const val DAGGER_HILT_ANDROID_WITH_FRAGMENT_BINDINGS =
  "dagger.hilt.android.WithFragmentBindings"
internal const val DAGGER_HILT_DEFINE_COMPONENT = "dagger.hilt.DefineComponent"
internal const val DAGGER_HILT_ENTRY_POINT = "dagger.hilt.EntryPoint"
internal const val DAGGER_HILT_GENERATED_COMPONENT_SUFFIX = "HiltComponents"

internal enum class HiltComponent {
  SingletonC,
  ApplicationC,
  ServiceC,
  ActivityC,
  ActivityRetainedC,
  FragmentC,
  ViewWithFragmentC,
  ViewC
}

internal fun PsiShortNamesCache.findHiltComponentByClassName(
  project: Project,
  shortClassName: String
): PsiClass? {
  val searchScope = GlobalSearchScope.projectScope(project)
  return getClassesByName(shortClassName, searchScope)
    .firstOrNull {
      it.containingClass?.qualifiedName?.endsWith(
        DAGGER_HILT_GENERATED_COMPONENT_SUFFIX
      ) == true
    }
}

/**
 * Utility function to find the Hilt generated component of the given `@AndroidEntryPoint` or `@HiltAndroidApp` annotated
 * class.
 *
 * Note: This only finds the standard in-built hilt components and does not find custom hilt components.s
 *
 * @param T The class for which the generated component needs to be found. It is either `PsiClass` or `KtClassOrObject`
 * @param hasAnnotation Check if `T` has the given annotation
 * @param isSubClassOf Check if `T` is a subclass of given class name.
 */
internal fun <T : PsiElement> findGeneratedStandardHiltComponent(
  componentClass: T,
  hasAnnotation: T.(String) -> Boolean,
  isSubClassOf: T.(String) -> Boolean
): PsiClass? {
  val project = componentClass.project
  val shortNamesCache = PsiShortNamesCache.getInstance(project)

  /**
   * Find the `PsiClass` of the `HiltComponent` using the `shortNamesCache`.
   */
  fun HiltComponent.findClass(): PsiClass? {
    return shortNamesCache.findHiltComponentByClassName(project, name)
  }
  return when {
    componentClass.hasAnnotation(DAGGER_HILT_ANDROID_APP) -> SingletonC.findClass()
      ?: ApplicationC.findClass()
    componentClass.hasAnnotation(DAGGER_HILT_ANDROID_ENTRY_POINT) -> {
      when {
        componentClass.isSubClassOf(ANDROIDX_COMPONENT_ACTIVITY) -> ActivityC.findClass()
        componentClass.isSubClassOf(ANDROIDX_FRAGMENT) -> FragmentC.findClass()
        componentClass.isSubClassOf(ANDROID_SERVICE) -> ServiceC.findClass()
        componentClass.isSubClassOf(ANDROID_VIEW) -> {
          if (componentClass.hasAnnotation(DAGGER_HILT_ANDROID_WITH_FRAGMENT_BINDINGS)) {
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

/**
 * Given a class annotated with `@DefineComponent` or `EntryPoint` will try to find the generated hilt component and
 * return it as `PsiClass`.
 */
internal fun findGeneratedCustomHiltComponent(customComponentDefinition: PsiClass): PsiClass? {
  return ClassInheritorsSearch.search(customComponentDefinition)
    .findAll()
    .firstOrNull {
      it.containingClass?.qualifiedName?.endsWith(
        DAGGER_HILT_GENERATED_COMPONENT_SUFFIX
      ) == true
    }
}
