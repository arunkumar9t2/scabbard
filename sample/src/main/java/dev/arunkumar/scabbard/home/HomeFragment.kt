package dev.arunkumar.scabbard.home

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import dev.arunkumar.scabbard.debug.SimpleSingleton
import dev.arunkumar.scabbard.di.FragmentScope
import javax.inject.Inject

class HomeFragment : DaggerFragment() {
  @Inject
  lateinit var activityDep: ActivityDep
  @Inject
  lateinit var fragmentDep: FragmentDep
  @Inject
  lateinit var singleton: SimpleSingleton

  @Module
  interface Builder {
    @FragmentScope
    @ContributesAndroidInjector
    fun homeFragment(): HomeFragment
  }
}