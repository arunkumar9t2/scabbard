package dev.arunkumar.scabbard.intellij.utill

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.util.PsiUtilCore

// TODO(arun) create a common package to share these between intellij and processor
private const val FULL_GRAPH_PREFIX = "full_"
private const val FULL_GRAPH_FILE_NAME = "$FULL_GRAPH_PREFIX%s"

private val GENERATED_FILE_FORMATS = listOf(
  "%s.png",
  "$FULL_GRAPH_FILE_NAME.png",
  "%s.svg",
  "$FULL_GRAPH_FILE_NAME.svg"
)


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
 * @param qualifiedPath The full path to class that contains the method which the annotation.
 * @param methodName The name of the method that has the annotation
 * @param returnTypeSimpleName The simple name of the method's return type.
 */
fun prepareContributesAndroidInjectorLineMarker(
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
    fileName = generatedImageName
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
 * Format and sanitize raw file name received from PSI processors.
 *
 * Currently
 * * replaces inner class markers `$` with `.`
 */
private fun String.sanitize(format: String): String {
  val sanitized = replace("$", ".")
  return String.format(format, sanitized)
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
      val classNameToSearch = if (isFullGraph) graphFileName.split(FULL_GRAPH_PREFIX).last() else graphFileName
      val classSimpleName = javaPsiFacade.findClass(classNameToSearch, searchScope)?.name
      return if (isFullGraph) {
        "$classSimpleName's Full $DEPENDENCY_GRAPH"
      } else {
        "$classSimpleName's $DEPENDENCY_GRAPH"
      }
    }
    return super.getElementText(element)
  }
}

/**
 * Prepares a LineMarker instance for gutter icon by search for [fileName] in different scopes. If given `fileName` is
 * found in any of the scope will return a `LineMarkerInfo` instance that navigates to the file on click of gutter icon.
 */
fun prepareDaggerComponentLineMarkerWithFileName(
  element: PsiElement,
  componentName: String,
  fileName: String
): RelatedItemLineMarkerInfo<PsiElement>? {
  val scope = GlobalSearchScope.allScope(element.project)
  val matchedFiles: List<PsiFile> = GENERATED_FILE_FORMATS
    .flatMap { fileNameFormat ->
      FilenameIndex.getFilesByName(
        element.project,
        fileName.sanitize(fileNameFormat),
        scope
      ).toList()
    }
  val title = "Open ${componentName}'s $DEPENDENCY_GRAPH"
  return if (matchedFiles.isNotEmpty()) {
    NavigationGutterIconBuilder.create(AllIcons.FileTypes.Diagram)
      .setTargets(matchedFiles)
      .setPopupTitle(title)
      .setTooltipText(title)
      .setCellRenderer(PsiElementToDaggerComponentNameCellRenderer())
      .createLineMarkerInfo(element)
  } else {
    // TODO(arun) Can we present a search action?
    null
  }
}