package dev.arunkumar.scabbard.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

abstract class ScabbardFunctionalTest {
  @get:Rule
  val testProjectDir = TemporaryFolder()

  protected lateinit var gradleRunner: GradleRunner

  @Before
  fun setUp() {
    gradleRunner = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
  }

  protected fun projectFile(path: String, content: String): File {
    val root = testProjectDir.root
    return File(root, path).apply {
      parentFile?.let { if (!it.exists()) it.mkdirs() }
      if (exists()) delete()
      createNewFile()
      writeText(content)
    }
  }

  protected fun addSimpleKtSourceFile() {
    projectFile(
      path = "/src/main/java/scabbard/main.kt",
      content = """
              package scabbard
              import dagger.Component
              import javax.inject.Inject
              
              class Node @Inject constructor()
              
              @Component
              interface SimpleComponent {
                fun node(): Node
              }
            """.trimIndent()
    )
  }
}