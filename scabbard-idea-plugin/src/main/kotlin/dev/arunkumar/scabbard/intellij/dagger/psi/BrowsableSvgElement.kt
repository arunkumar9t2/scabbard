package dev.arunkumar.scabbard.intellij.dagger.psi

import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.ide.util.EditSourceUtil
import com.intellij.pom.PomTarget
import com.intellij.pom.PomTargetPsiElement
import com.intellij.psi.PsiFile

/**
 *  Delegating implementation of [PsiFile] to open the svg file in browser on request to navigate.
 *
 *  Implementation notes:
 *  * Navigation gutter icon relies on [PomTargetPsiElement] and [PomTarget] to perform navigation before falling back
 *  to custom behavior for [PsiFile]s.
 *  * This class implements those interfaces to pass the check to avoid default navigation for `PsiFile` which is just
 *  opening it in the editor.
 *
 *  @see [EditSourceUtil]
 *  @param svgFile The `PsiFile` instance pointing to the svg file which should be opened in browser.
 */
class BrowsableSvgElement(
  val svgFile: PsiFile
) : PsiFile by svgFile, PomTargetPsiElement, PomTarget {

  override fun toString() = "Browsable Svg File: $svgFile"

  override fun getIcon(flags: Int) = AllIcons.General.Web

  override fun getTarget() = this

  override fun getNavigationElement() = this

  override fun canNavigateToSource(): Boolean = false

  override fun navigate(requestFocus: Boolean) {
    BrowserUtil.browse(svgFile.virtualFile)
  }
}
