package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.ElementsIntoSet
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@RunWith(JUnit4::class)
class QualifiedNamesTest {

  class SimpleNode

  class NodeA
  @Inject
  constructor(
    private val names: Set<String>,
    @param:SimpleQualifier
    private val simpleNode: SimpleNode,
    @param:Named("Namey")
    private val namedNode: SimpleNode
  )

  class MemberInjectionTarget

  @Qualifier
  @Retention
  annotation class SimpleQualifier

  @Module
  abstract class SimpleModule {

    @Module
    companion object {
      @SimpleQualifier
      @Provides
      @JvmStatic
      fun simpleQualifiedNode() = SimpleNode()

      @Named("Namey")
      @Provides
      @JvmStatic
      fun namedSimpleNode() = SimpleNode()

      @Provides
      @ElementsIntoSet
      @JvmStatic
      fun provideMultibindings(): Set<String> {
        return hashSetOf("Scabbard", "Dagger")
      }
    }
  }

  @Singleton
  @Component(modules = [SimpleModule::class])
  interface SimpleComponent {
    fun inject(memberInjectionTarget: MemberInjectionTarget)
    fun nodeA(): NodeA
    fun subComponentFactory(): SimpleSubComponent.Factory
  }

  @Qualifier
  annotation class SubScope

  @SubScope
  class SubComponentNode
  @Inject
  constructor(val nodeA: NodeA)

  @SubScope
  @Subcomponent
  interface SimpleSubComponent {
    fun subcomponentNode(): SubComponentNode
    @Subcomponent.Factory
    interface Factory {
      fun create(): SimpleSubComponent
    }
  }

  private lateinit var simpleComponentDot: String
  private lateinit var simpleSubcomponentDot: String

  @Before
  fun setup() {
    simpleComponentDot = SimpleComponent::class.java.generatedDotFile().readText()
    simpleSubcomponentDot = SimpleSubComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `assert root graph label has qualified name`() {
    assertThat(simpleComponentDot).contains("graph [rankdir=\"LR\", labeljust=\"l\", label=\"dev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleComponent\"")
  }

  @Test
  fun `assert entry point nodes have qualified names`() {
    // Member injection target
    assertThat(simpleComponentDot).contains("dev.arunkumar.scabbard.plugin.QualifiedNamesTest.MemberInjectionTarget")

    assertThat(simpleComponentDot).contains("dev.arunkumar.scabbard.plugin.QualifiedNamesTest.NodeA")
  }

  @Test
  fun `assert subcomponent entry point and edge has qualified names`() {
    assertThat(simpleComponentDot).contains("dev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleSubComponent.Factory\\n\\nSubcomponent Creator")
    // End of edge
    assertThat(simpleComponentDot).contains("label=\"dev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleSubComponent\"")
  }

  @Test
  fun `assert multibindings cluster has qualified names`() {
    // Multibinding key
    assertThat(simpleComponentDot).contains("label=\"java.util.Set<java.lang.String>\"")
    // Multibinding contents
    assertThat(simpleComponentDot).contains("[shape=\"tab\", label=\"java.util.Set<java.lang.String>\", color=\"turquoise\"]")
  }

  @Test
  fun `assert multibindings cluster members have qualified names`() {
    assertThat(simpleComponentDot)
      .contains("label=\"dev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleModule.provideMultibindings()\"")
  }

  @Test
  fun `assert qualifiers have qualified names in dependency graph`() {
    assertThat(simpleComponentDot).contains("@dev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleQualifier\\ndev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleNode")
  }

  @Test
  fun `assert named qualifiers have qualified names`() {
    assertThat(simpleComponentDot).contains("@javax.inject.Named(\\\"Namey\\\")\\ndev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleNode")
  }

  @Test
  fun `assert subcomponent graph has component hierarchy rendered with qualified names`() {
    assertThat(simpleSubcomponentDot).contains("label=\"dev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleComponent → dev.arunkumar.scabbard.plugin.QualifiedNamesTest.SimpleSubComponent\"")
  }
}