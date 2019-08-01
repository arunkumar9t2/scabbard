package dev.arunkumar.sample

import android.os.Bundle
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var helloWorld: HelloWorld

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    @Module
    abstract class Builder {
        @ContributesAndroidInjector
        abstract fun mainActivity(): MainActivity
    }
}
