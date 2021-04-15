package dev.arukumar.scabbard.anvil

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Binds
import dagger.Component
import dagger.Module
import javax.inject.Inject

interface AppScope

interface Binding

class DefaultBinding @Inject constructor() : Binding

@Module
@ContributesTo(AppScope::class)
interface DaggerModule {
  @Binds
  fun DefaultBinding.bind(): Binding
}

@ContributesTo(AppScope::class)
interface ComponentInterface {
  fun binding(): Binding
}

@MergeComponent(AppScope::class)
interface AppComponent {
  @Component.Factory
  interface Factory {
    fun create(): AppComponent
  }
}

fun main() {
  DaggerAppComponent
    .factory()
    .create()
    .binding()
}
