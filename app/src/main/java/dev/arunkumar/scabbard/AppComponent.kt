package dev.arunkumar.scabbard

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dev.arunkumar.scabbard.home.MainActivitySubComponent
import javax.inject.Singleton

@Singleton
@Component
interface AppComponent {

    fun mainActivitySubComponentFactory(): MainActivitySubComponent.Factory

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance application: Application): AppComponent
    }
}