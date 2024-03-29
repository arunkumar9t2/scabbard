/*
 * Copyright 2022 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arunkumar.scabbard.intellij.dagger.psi

import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.GlobalSearchScope
import dev.arunkumar.scabbard.intellij.dagger.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.dagger.DAGGER_MODULE
import dev.arunkumar.scabbard.intellij.dagger.DAGGER_SUBCOMPONENT
import dev.arunkumar.scabbard.intellij.dagger.anvil.ANVIL_MERGE_COMPONENT
import dev.arunkumar.scabbard.intellij.dagger.anvil.ANVIL_MERGE_SUBCOMPONENT
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * List of fully qualified annotations that correspond to a Dagger
 * component.
 */
val DAGGER_COMPONENT_ANNOTATIONS = listOf(
  DAGGER_COMPONENT,
  DAGGER_SUBCOMPONENT,
  DAGGER_MODULE,
  ANVIL_MERGE_COMPONENT, // Anvil's merge component is a replacement for `DAGGER_COMPONENT`.
  ANVIL_MERGE_SUBCOMPONENT // Anvil's merge sub component is a replacement for `DAGGER_SUBCOMPONENT`.
)

/**
 * @return `true` only when
 * 1. The given PSI element is an `PsiIdentifier` in `Java` language.
 * 2. and is a dagger annotation entry (present in `daggerAnnotations`).
 *    For example, for `@Component` annotation on a class or
 *    an interface, this method will return `true` only for
 *    `Component` part when it is represented as a `PsiIdentifier`.
 *
 * @param daggerAnnotations the list of dagger annotation to look for.
 * @see DAGGER_COMPONENT_ANNOTATIONS
 */
fun PsiElement.isDaggerAnnotationIdentifier(daggerAnnotations: List<String> = DAGGER_COMPONENT_ANNOTATIONS): Boolean {
  val psiIdentifier = this as? PsiIdentifier
  return daggerAnnotations.any { daggerAnnotation ->
    (psiIdentifier?.parent?.parent as? PsiAnnotation)?.qualifiedName == daggerAnnotation
  }
}

fun LeafPsiElement.ktClassOrObject(): KtClassOrObject? {
  val isAClassType = (
    text == KtTokens.CLASS_KEYWORD.value ||
      text == KtTokens.OBJECT_KEYWORD.value ||
      text == KtTokens.INTERFACE_KEYWORD.value
    )
  if (elementType is KtKeywordToken && isAClassType) {
    val classOrObjectCandidate = parent
    if (classOrObjectCandidate is KtClassOrObject) {
      return classOrObjectCandidate
    }
  }
  return null
}

fun KtClassOrObject.hasAnnotation(qualifiedAnnotationName: String): Boolean {
  return findAnnotation(FqName(qualifiedAnnotationName)) != null
}

/**
 * Converts a Kotlin class object to `PsiClass` instance. Null if
 * unsuccessful.
 */
fun KtClassOrObject.toPsiClass(): PsiClass? {
  val javaPsiFacade = JavaPsiFacade.getInstance(project)
  val searchScope = GlobalSearchScope.allScope(project)
  return fqName?.asString()
    ?.let { qualifiedName -> javaPsiFacade.findClass(qualifiedName, searchScope) }
}

fun PsiClass?.isSubClassOf(qualifiedClassName: String): Boolean {
  var currSuperClass = this
  do {
    when (currSuperClass?.qualifiedName) {
      qualifiedClassName -> return true
      else -> currSuperClass = currSuperClass?.superClass
    }
  } while (currSuperClass != null)
  return false
}
