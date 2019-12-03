package dev.arunkumar.scabbard.home

import dagger.Subcomponent

@Subcomponent
interface MainActivitySubComponent {
    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivitySubComponent
    }
}