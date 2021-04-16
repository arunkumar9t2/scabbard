package dev.arukumar.scabbard.anvil

import com.squareup.anvil.annotations.MergeComponent
import dagger.Component

@MergeComponent(AppScope::class)
interface AnvilComponent {
  @Component.Factory
  interface Factory {
    fun create(): AnvilComponent
  }
}

fun main() {
  DaggerAnvilComponent.factory().create().apply {
    binding()
    setMultiBindings()
    mapMultiBindings()
  }
}
