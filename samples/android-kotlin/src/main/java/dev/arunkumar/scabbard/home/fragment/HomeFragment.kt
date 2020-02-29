package dev.arunkumar.scabbard.home.fragment

import dagger.android.support.DaggerFragment
import dev.arunkumar.scabbard.debug.SimpleSingleton
import dev.arunkumar.scabbard.debug.UnScopedBinding
import dev.arunkumar.scabbard.home.ActivityDep
import javax.inject.Inject

class HomeFragment : DaggerFragment() {
  @Inject
  lateinit var activityDep: ActivityDep
  @Inject
  lateinit var fragmentDep: FragmentDep
  @Inject
  lateinit var singleton: SimpleSingleton
  @Inject
  lateinit var unScopedBinding: UnScopedBinding
}

class FragmentDep @Inject constructor()