package dev.arukumar.scabbard.anvil

import com.squareup.anvil.annotations.MergeComponent
import dagger.Component

@MergeComponent(AppScope::class)
interface AppComponent {
  @Component.Factory
  interface Factory {
    fun create(): AppComponent
  }
}

fun main() {
  DaggerAppComponent.factory().create().apply {
    binding()
    setMultiBindings()
    mapMultiBindings()
  }
}
