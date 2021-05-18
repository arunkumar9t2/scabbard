package dev.arunkumar.scabbard.intellij.dagger

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.util.PsiUtilCore
import dev.arunkumar.scabbard.intellij.dagger.psi.BrowsableSvgElement

private const val DEPENDENCY_GRAPH = "dependency graph"

/**
 * Adds a gutter icon to open the dependency graph for the Subcomponent that `@ContributesAndroidInjector`
 * would be generating.
 *
 * All parameters of this function are related to the method that has the `@ContributesAndroidInjector`
 * annotation.
 *
 * @param contributesAndroidInjectorElement PsiElement representing the `@ContributesAndroidInjector`
 * @param packageName The package name of the containing file
 * @param qualifiedPath The full path to class that contains the method with the annotation.
 * @param methodName The name of the method that has the annotation
 * @param returnTypeSimpleName The simple name of the method's return type.
 */
internal fun prepareContributesAndroidInjectorLineMarker(
  contributesAndroidInjectorElement: PsiElement,
  packageName: String,
  qualifiedPath: String,
  methodName: String,
  returnTypeSimpleName: String
): RelatedItemLineMarkerInfo<PsiElement>? {
  val innerClassSep = '_'

  // Replace inner class paths with "_" and capitalize the method name to get the path
  val generatedImagePath = "$packageName." + qualifiedPath
    .substring(packageName.length + 1)
    .replace('.', innerClassSep) + "$innerClassSep${methodName.capitalize()}"

  // We could infer the generated subcomponent name by simply getting the return type and
  // suffixing "SubComponent"
  val subcomponentName = returnTypeSimpleName + "Subcomponent"
  val generatedImageName = "$generatedImagePath.$subcomponentName"
  val lineMarker = prepareDaggerComponentLineMarkerWithFileName(
    element = contributesAndroidInjectorElement,
    componentName = subcomponentName,
    componentFqcn = generatedImageName
  )
  if (lineMarker != null) {
    return lineMarker
  } else {
    // If Android Project uses packageName suffix then generated subcomponent will have suffix in
    // its package name which invalidates all the assumptions we made above. To overcome this,
    // we could get the correct package name by querying IntelliJ class index with the return type.
    val project = contributesAndroidInjectorElement.project
    PsiShortNamesCache.getInstance(project)
      .getClassesByName(
        subcomponentName,
        GlobalSearchScope.projectScope(project)
      ).firstOrNull()
      ?.let { subcomponentClass ->
        val qualifiedName = subcomponentClass.qualifiedName
        return prepareDaggerComponentLineMarkerWithFileName(
          contributesAndroidInjectorElement,
          subcomponentName,
          "$qualifiedName"
        )
      }
  }
  return null
}

/**
 * Custom cell renderer that simple updates the generated graph file name's entry to use the actual class name.
 */
private class PsiElementToDaggerComponentNameCellRenderer : DefaultPsiElementCellRenderer() {

  override fun getElementText(element: PsiElement): String {
    val project = element.project
    val searchScope = GlobalSearchScope.allScope(project)
    val javaPsiFacade = JavaPsiFacade.getInstance(project)

    val graphFileName = PsiUtilCore.getVirtualFile(element)?.nameWithoutExtension
    if (graphFileName != null) {
      val isFullGraph = graphFileName.startsWith(FULL_GRAPH_PREFIX)
      val isComponentTreeGraph = graphFileName.startsWith(TREE_GRAPH_PREFIX)

      val classNameToSearch = when {
        isFullGraph -> graphFileName.split(FULL_GRAPH_PREFIX).last()
        isComponentTreeGraph -> graphFileName.split(TREE_GRAPH_PREFIX).last()
        else -> graphFileName
      }

      val classSimpleName = javaPsiFacade.findClass(classNameToSearch, searchScope)?.name
      return when {
        isFullGraph -> "$classSimpleName's Full $DEPENDENCY_GRAPH"
        isComponentTreeGraph -> "$classSimpleName's Component Hierarchy"
        else -> "$classSimpleName's $DEPENDENCY_GRAPH"
      }
    }
    return super.getElementText(element)
  }
}

/**
 * Adds couple of additional navigation elements for certain file types.
 *
 * For example, for SVG adds a navigation element to open svg in browserS
 */
private fun addAdditionalNavigationElements(original: List<PsiFile>): List<PsiElement> {
  val result = original.toMutableList()
  val additional: List<PsiElement> = original.filter { it.virtualFile.extension == "svg" }.map(::BrowsableSvgElement)
  return (result + additional).sortedBy { (it as? PsiFile)?.name ?: it.toString() }
}

/**
 * Prepares a [RelatedItemLineMarkerInfo] instance for gutter icon by searching for [componentFqcn] in different scopes.
 * By default, [GENERATED_DAGGER_FILES] will be used as a base for searching generated component graphs.
 *
 * @return [RelatedItemLineMarkerInfo] instance with popup to navigate to all matched files
 * @param element [PsiElement] for which the Gutter icon should be targeted
 * @param componentName Simple name of the Dagger component that will be used in tooltip of the gutter icon.
 * @param componentFqcn Fully qualified name of the dagger component
 */
internal fun prepareDaggerComponentLineMarkerWithFileName(
  element: PsiElement,
  componentName: String,
  componentFqcn: String
): RelatedItemLineMarkerInfo<PsiElement>? {
  val project = element.project
  val matchedFiles: List<PsiFile> = searchGeneratedDaggerFiles(project, componentFqcn)
  val targets = addAdditionalNavigationElements(matchedFiles)
  val title = "Open $componentName's $DEPENDENCY_GRAPH"
  return if (matchedFiles.isNotEmpty()) {
    NavigationGutterIconBuilder.create(SCABBARD_ICON)
      .setTargets(targets)
      .setPopupTitle(title)
      .setTooltipText(title)
      .setCellRenderer(PsiElementToDaggerComponentNameCellRenderer())
      .createLineMarkerInfo(element)
  } else {
    // TODO(arun) Can we present a search action?
    null
  }
}
