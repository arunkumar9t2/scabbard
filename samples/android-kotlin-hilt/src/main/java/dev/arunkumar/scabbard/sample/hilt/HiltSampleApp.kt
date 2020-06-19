package dev.arunkumar.scabbard.sample.hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HiltSampleApp : Application() {
  @Inject
  lateinit var hiltCustomComponentBuilder: HiltCustomComponent.Builder
}