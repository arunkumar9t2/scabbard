package dev.arunkumar.scabbard.debug

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

// Empty Entry Point
class DummyInjectionTarget

class UnScopedBinding @Inject constructor()

@Singleton
class SimpleSingleton @Inject constructor()

@Singleton
class ComplexSingleton
@Inject
constructor(
  private val unScopedBinding: UnScopedBinding,
  private val simpleSingleton: SimpleSingleton
)

class ProvisionBinding

@Module
object ProvisionModule {
  @Provides
  @Singleton
  @JvmStatic
  fun providesProvisionBinding() = ProvisionBinding()
}


interface MultiBindingType

class SimpleMultiBindingType
@Inject constructor() : MultiBindingType

class ComplexMultiBindingType
@Inject constructor(val provisionBinding: ProvisionBinding) : MultiBindingType

@Module
abstract class MultiBindingsProvisionModule {
  @Binds
  @IntoSet
  abstract fun simpleMultiBindingType(simpleMultiBindingType: SimpleMultiBindingType): MultiBindingType

  @Binds
  @IntoSet
  abstract fun complexMultiBindingType(complexMultiBindingType: ComplexMultiBindingType): MultiBindingType
}

interface DelegateBinding

class DefaultDelegateBinding
@Inject
constructor(val unScopedBinding: UnScopedBinding) : DelegateBinding

@Module
abstract class DelegateBindingModule {
  @Binds
  abstract fun bindsDelegateBinding(defaultDelegateBinding: DefaultDelegateBinding): DelegateBinding
}

class NamedBinding

@Module
object NamedProvisionModule {
  @Provides
  @Named("named")
  @Singleton
  @JvmStatic
  fun providesNamedBinding() = NamedBinding()
}