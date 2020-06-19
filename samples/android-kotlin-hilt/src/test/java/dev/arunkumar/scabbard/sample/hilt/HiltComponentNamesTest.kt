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
    assertTrue(fragmentWithViewDot.contains("ApplicationC → ActivityRetainedC → ActivityC → FragmentC → ViewWithFragmentC"))
  }

  fun `test binding graph nodes do not contain HiltComponents suffix`() {
    val applicationCDot = generatedDot<HiltSampleApp_HiltComponents.ApplicationC>(sourceSet = SOURCE_SET)
    assertTrue(applicationCDot.contains("label=\"HiltCustomComponent.Builder\""))
    assertTrue(applicationCDot.contains("label=\"ActivityRetainedC.Builder"))
    assertTrue(applicationCDot.contains("label=\"ServiceC.Builder"))
    assertTrue(applicationCDot.contains("label=\"@ActivityRetainedScoped\\nActivityRetainedC"))
  }


  companion object {
    private const val SOURCE_SET = "debug"
  }
}