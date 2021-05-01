package dev.arunkumar.scabbard.intellij.dagger.console.filters

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

private typealias ConsoleLog = String

class DefaultMissingBindingComponentExtractorTest {
  private lateinit var missingBindingComponentExtractor: MissingBindingComponentExtractor

  @Before
  fun setup() {
    missingBindingComponentExtractor = DefaultMissingBindingComponentExtractor()
  }

  private data class LineInfo(val line: String, val accLength: Int)

  @OptIn(ExperimentalStdlibApi::class)
  private fun ConsoleLog.applyTo(
    missingBindingComponentExtractor: MissingBindingComponentExtractor
  ): Set<DaggerComponent> {
    return lineSequence()
      .map { LineInfo(it, 0) }
      .scanReduce { acc, line ->
        acc.copy(line = line.line, accLength = acc.accLength + line.line.length)
      }.flatMap { missingBindingComponentExtractor.extract(it.line, it.accLength).asSequence() }
      .toSet()
  }

  @Test
  fun `assert dagger component with supertype is extracted from missing binding stream of logs`() {
    val consoleLog = """
    |> Task :scabbard-processor:jar UP-TO-DATE
    |> Task :samples:android-kotlin:kaptGenerateStubsDebugKotlin UP-TO-DATE
    |
    |> Task :samples:android-kotlin:kaptDebugKotlin
    |C:\Users\arunk\AndroidProjects\personal\scabbard\samples\android-kotlin\build\tmp\kapt3\stubs\debug\dev\arunkumar\scabbard\di\AppComponent.java:8: error: [Dagger/MissingBinding] dev.arunkumar.scabbard.di.ProvisionBinding cannot be provided without an @Inject constructor or an @Provides-annotated method.
    |public abstract interface AppComponent extends dagger.android.AndroidInjector<dev.arunkumar.scabbard.App> {
    |                ^
    |      dev.arunkumar.scabbard.di.ProvisionBinding is injected at
    |          dev.arunkumar.scabbard.App.provisionBinding
    |      dev.arunkumar.scabbard.App is injected at
    |          dagger.android.AndroidInjector.inject(T)
    |  It is also requested at:
    |      dev.arunkumar.scabbard.di.ComplexMultiBinding(provisionBinding)
    |> Task :samples:android-kotlin:kaptDebugKotlin FAILED
""".trimMargin()
    val daggerComponents = consoleLog.applyTo(missingBindingComponentExtractor)
    Truth.assertThat(daggerComponents)
      .containsExactly(DaggerComponent(446, 458, "AppComponent"))
  }

  @Test
  fun `assert dagger component without supertype is extracted from missing binding stream of logs`() {
    val consoleLog = """
    |> Task :scabbard-processor:jar UP-TO-DATE
    |> Task :samples:android-kotlin:kaptGenerateStubsDebugKotlin UP-TO-DATE
    |
    |> Task :samples:android-kotlin:kaptDebugKotlin
    |C:\Users\arunk\AndroidProjects\personal\scabbard\samples\android-kotlin\build\tmp\kapt3\stubs\debug\dev\arunkumar\scabbard\di\AppComponent.java:8: error: [Dagger/MissingBinding] dev.arunkumar.scabbard.di.ProvisionBinding cannot be provided without an @Inject constructor or an @Provides-annotated method.
    |public abstract interface AppComponent  {
    |                ^
    |      dev.arunkumar.scabbard.di.ProvisionBinding is injected at
    |          dev.arunkumar.scabbard.App.provisionBinding
    |      dev.arunkumar.scabbard.App is injected at
    |          dagger.android.AndroidInjector.inject(T)
    |  It is also requested at:
    |      dev.arunkumar.scabbard.di.ComplexMultiBinding(provisionBinding)
    |> Task :samples:android-kotlin:kaptDebugKotlin FAILED
""".trimMargin()
    val daggerComponents = consoleLog.applyTo(missingBindingComponentExtractor)
    Truth.assertThat(daggerComponents)
      .containsExactly(DaggerComponent(446, 458, "AppComponent"))
  }

  @Test
  fun `assert dagger hilt component with interface supertype is extracted from missing binding stream of logs`() {
    val consoleLog = """
      |> Task :samples:android-kotlin-hilt:kaptDebugKotlin
      |[WARN] Issue detected with dagger.internal.codegen.ComponentProcessor. Expected 1 originating source file when generating C:\Users\arunk\AndroidProjects\personal\scabbard\samples\android-kotlin-hilt\build\generated\source\kapt\debug\scabbard\full_dev.arunkumar.scabbard.sample.hilt.HiltCustomModule.dot, but detected 0: [].
      |C:\Users\arunk\AndroidProjects\personal\scabbard\samples\android-kotlin-hilt\build\generated\source\kapt\debug\dev\arunkumar\scabbard\sample\hilt\HiltSampleApp_HiltComponents.java:131: error: [Dagger/MissingBinding] dev.arunkumar.scabbard.sample.hilt.SingletonBinding cannot be provided without an @Inject constructor or an @Provides-annotated method.
      |  public abstract static class SingletonC implements HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint,
      |                         ^
      |      dev.arunkumar.scabbard.sample.hilt.SingletonBinding is injected at
      |          dev.arunkumar.scabbard.sample.hilt.HiltSampleApp.singletonBinding
      |      dev.arunkumar.scabbard.sample.hilt.HiltSampleApp is injected at
      |          dev.arunkumar.scabbard.sample.hilt.HiltSampleApp_GeneratedInjector.injectHiltSampleApp(dev.arunkumar.scabbard.sample.hilt.HiltSampleApp)
      |> Task :samples:android-kotlin-hilt:kaptDebugKotlin FAILED
    """.trimMargin()
    val daggerComponents = consoleLog.applyTo(missingBindingComponentExtractor)
    Truth.assertThat(daggerComponents)
      .containsExactly(DaggerComponent(705, 715, "SingletonC"))
  }
}
