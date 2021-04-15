package dev.arukumar.scabbard.anvil

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
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

  fun setMultiBindings(): Set<@JvmSuppressWildcards ContributedMultiBinding>

  fun mapMultiBindings(): Map<String, Listener>
}
