package dev.arunkumar.scabbard.sample.hilt

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@AndroidEntryPoint
class SimpleFragment : Fragment() {

  @Inject
  lateinit var fragmentDependency: FragmentDependency

  @FragmentScoped
  class FragmentDependency @Inject constructor()

  companion object {
    fun instance() = SimpleFragment()
  }
}