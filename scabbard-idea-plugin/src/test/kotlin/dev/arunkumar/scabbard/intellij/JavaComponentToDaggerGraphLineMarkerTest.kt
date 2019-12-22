package dev.arunkumar.scabbard.intellij

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class JavaComponentToDaggerGraphLineMarkerTest : LightJavaCodeInsightFixtureTestCase() {

  @Test
  fun test() {
    assertTrue(true)
  }

  //@Test
  fun `assert line markers are added for dagger annotations if file is present in project dir`() {
    myFixture.addFileToProject("dev.arunkumar.scabbard.home.simple.SimpleSubcomponent.png", "")
    myFixture.addClass(
      """package dev.arunkumar.scabbard.home.simple;

import dagger.Subcomponent;

@SimpleScope
@Subcomponent
public interface SimpleSubcomponent {

  SimpleDep simpleDep();

  @Subcomponent.Factory
  interface Factory {
    SimpleSubcomponent create();
  }
}"""
    )
    myFixture.doHighlighting()
  }
}