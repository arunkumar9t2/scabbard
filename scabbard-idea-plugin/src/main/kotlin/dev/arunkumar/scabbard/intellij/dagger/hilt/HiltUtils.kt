package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache

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