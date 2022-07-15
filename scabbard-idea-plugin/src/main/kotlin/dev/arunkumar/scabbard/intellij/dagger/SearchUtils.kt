/*
 * Copyright 2021 Arunkumar
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

package dev.arunkumar.scabbard.intellij.dagger

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

// TODO(arun) create a common package to share these between intellij and processor
internal const val FULL_GRAPH_PREFIX = "full_"
internal const val FULL_GRAPH_FILE_NAME = "$FULL_GRAPH_PREFIX%s"
internal const val TREE_GRAPH_PREFIX = "tree_"
internal const val TREE_GRAPH_FILE_NAME = "$TREE_GRAPH_PREFIX%s"

internal val FULL_BINDING_GRAPH_FILES = listOf(
  "$FULL_GRAPH_FILE_NAME.png",
  "$FULL_GRAPH_FILE_NAME.svg"
)

internal val COMPONENT_HIERARCHY_GRAPH_FILES = listOf(
  "$TREE_GRAPH_FILE_NAME.png",
  "$TREE_GRAPH_FILE_NAME.svg",
)

internal val DEFAULT_DAGGER_GRAPH_FILES = listOf(
  "%s.png",
  "%s.svg",
)

/** All known scabbard processor generated files. */
internal val GENERATED_DAGGER_FILES = (
  DEFAULT_DAGGER_GRAPH_FILES +
    COMPONENT_HIERARCHY_GRAPH_FILES +
    FULL_BINDING_GRAPH_FILES
  ).asSequence()

/**
 * Format and sanitize raw file name received from PSI processors.
 *
 * Currently replaces inner class markers `$` with `.`
 */
private fun String.sanitize(format: String): String {
  val sanitized = replace("$", ".")
  return String.format(format, sanitized)
}

/**
 * Searches the file system using `FilenameIndex` and returns all
 * matching formats in `GENERATED_DAGGER_FILES`.
 *
 * @param project IntelliJ Project
 * @param componentFqcn The fully qualified class name of Dagger
 *     component for which the files should be searched.
 * @param formats The list of the fill extension format that should be
 *     searched. Default `GENERATED_DAGGER_FILES`.
 */
internal fun searchGeneratedDaggerFiles(
  project: Project,
  componentFqcn: String,
  formats: Sequence<String> = GENERATED_DAGGER_FILES
): List<PsiFile> {
  val scope = GlobalSearchScope.allScope(project)
  val projectFileIndex = ProjectFileIndex.SERVICE.getInstance(project)
  return formats
    .flatMap { fileNameFormat ->
      // Search by file name
      FilenameIndex.getFilesByName(
        project,
        componentFqcn.sanitize(fileNameFormat),
        scope
      ).asSequence()
    }.filter { psiFile ->
      // Ensure files present only in sources
      projectFileIndex.isInSource(psiFile.virtualFile)
    }.toList()
}
