package dev.arunkumar.scabbard.sample.hilt

import dev.arunkumar.scabbard.plugin.generatedDot
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], manifest = Config.NONE)
class HiltComponentNamesTest {

  @Test
  fun `test hilt component path do not contain HiltComponents suffix`() {
    // View with the fragment is the leaf component so it should have all components
    val fragmentWithViewDot = generatedDot<HiltSampleApp_HiltComponents.ViewWithFragmentC>(sourceSet = SOURCE_SET)
    assertTrue(fragmentWithViewDot.contains("SingletonC → ActivityRetainedC → ActivityC → FragmentC → ViewWithFragmentC"))
  }

  fun `test binding graph nodes do not contain HiltComponents suffix`() {
    val singletonCDot = generatedDot<HiltSampleApp_HiltComponents.SingletonC>(sourceSet = SOURCE_SET)
    assertTrue(singletonCDot.contains("label=\"HiltCustomComponent.Builder\""))
    assertTrue(singletonCDot.contains("label=\"ActivityRetainedC.Builder"))
    assertTrue(singletonCDot.contains("label=\"ServiceC.Builder"))
    assertTrue(singletonCDot.contains("label=\"@ActivityRetainedScoped\\nActivityRetainedC"))
  }

  companion object {
    private const val SOURCE_SET = "debug"
  }
}