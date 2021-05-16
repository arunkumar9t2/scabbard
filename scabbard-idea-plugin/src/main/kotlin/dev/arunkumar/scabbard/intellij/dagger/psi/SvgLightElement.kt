package dev.arunkumar.scabbard.intellij.dagger.psi

import com.intellij.ide.BrowserUtil
import com.intellij.lang.Language
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.light.LightElement

/**
 *  A [LightElement] implementation for the sole purpose of opening the SVG fil in the browser instead on in editor.
 *
 *  @param svgFile The `PsiFile` instance pointing to the svg file which should be opened in browser.
 */
class SvgLightElement(val svgFile: PsiFile) : LightElement(
  PsiManager.getInstance(svgFile.project),
  Language.ANY
) {

  override fun toString(): String {
    return "LightElement: $svgFile"
  }

  override fun navigate(requestFocus: Boolean) {
    BrowserUtil.browse(svgFile.virtualFile)
  }
}
