package dev.arunkumar.scabbard.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Inject
import javax.inject.Singleton

class HelloWorld @Inject constructor(private val application: Application) {
  fun say() {
    println("Hello World")
  }
}

@Singleton
@Component
interface DependantComponent {
  fun helloWorld(): HelloWorld

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): DependantComponent
  }
}
