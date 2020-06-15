package dev.arunkumar.scabbard.sample.hilt

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@AndroidEntryPoint
class SimpleActivity : AppCompatActivity() {

  @Inject
  lateinit var activityDependency: ActivityDependency

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
}
