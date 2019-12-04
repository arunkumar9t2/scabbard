package dev.arunkumar.scabbard.home

import dagger.Subcomponent
import dev.arunkumar.scabbard.di.ActivityScope

@ActivityScope
@Subcomponent
interface MainActivitySubComponent {
    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivitySubComponent
    }
}