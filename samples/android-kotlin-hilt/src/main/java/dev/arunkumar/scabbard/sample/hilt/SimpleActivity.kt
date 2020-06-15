package dev.arunkumar.scabbard.sample.hilt

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@AndroidEntryPoint
class SimpleActivity : AppCompatActivity() {

  @Inject
  lateinit var activityDependency: ActivityDependency

  @Inject
  lateinit var activityRetainedDependency: ActivityRetainedDependency

  @Inject
  lateinit var provisionBinding: ProvisionBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  class MainActivityPresenter @Inject constructor()

  @ActivityScoped
  class ActivityDependency
  @Inject
  constructor(
    private val activity: Activity,
    private val mainActivityPresenter: MainActivityPresenter
  )

  @ActivityRetainedScoped
  class ActivityRetainedDependency @Inject constructor()


  class ProvisionBinding

  @Module
  @InstallIn(ActivityComponent::class)
  object ActivityProvisionModule {
    @ActivityScoped
    @Provides
    fun provisionBinding() = ProvisionBinding()
  }
}
