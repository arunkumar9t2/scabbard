package dev.arunkumar.scabbard.home

import android.os.Bundle
import androidx.fragment.app.commit
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dev.arunkumar.scabbard.R
import dev.arunkumar.scabbard.debug.ComplexSingleton
import dev.arunkumar.scabbard.debug.DaggerDependantComponent
import dev.arunkumar.scabbard.debug.SimpleSingleton
import dev.arunkumar.scabbard.di.appComponent
import dev.arunkumar.scabbard.di.scope.ActivityScope
import dev.arunkumar.scabbard.home.fragment.HomeFragment
import dev.arunkumar.scabbard.home.fragment.ModuleHolder
import javax.inject.Inject

class HomeActivity : DaggerAppCompatActivity() {
  @Inject
  lateinit var activityDep: ActivityDep
  @Inject
  lateinit var applicationSingleton: SimpleSingleton
  @Inject
  lateinit var complexSingleton: ComplexSingleton

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    supportFragmentManager.commit {
      replace(
        R.id.fragmentContainer,
        HomeFragment()
      )
    }

    // Setup simple subcomponent
    application.appComponent.simpleSubcomponentFactory().create()

    // Setup dependant component
    DaggerDependantComponent.factory()
      .create(application)
      .helloWorld()
      .say()
  }

  @Module
  interface Builder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [ModuleHolder.HomeFragmentBuilder::class])
    fun homeActivity(): HomeActivity
  }
}
