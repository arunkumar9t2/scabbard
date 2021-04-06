package dev.arunkumar.scabbard.di

import dagger.Binds
import dagger.Module
import javax.inject.Inject

interface DelegateBinding

class DefaultDelegateBinding
@Inject
constructor(val unScopedBinding: UnScopedBinding) : DelegateBinding

@Module
abstract class DelegateBindingModule {
  @Binds
  abstract fun DefaultDelegateBinding.bindsDelegateBinding(): DelegateBinding
}
