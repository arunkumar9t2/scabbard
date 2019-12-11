package dev.arunkumar.scabbard.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dev.arunkumar.scabbard.App
import dev.arunkumar.scabbard.debug.DummyInjectionTarget
import dev.arunkumar.scabbard.debug.MultiBindingsProvisionModule
import dev.arunkumar.scabbard.debug.ProvisionModule
import dev.arunkumar.scabbard.home.MainActivitySubComponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ProvisionModule::class,
        MultiBindingsProvisionModule::class
    ]
)
interface AppComponent {

    // Bindings
    fun mainActivitySubComponentFactory(): MainActivitySubComponent.Factory

    // Injection targets
    fun inject(app: App)

    fun inject(dummyInjectionTarget: DummyInjectionTarget)

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance application: Application): AppComponent
    }
}

