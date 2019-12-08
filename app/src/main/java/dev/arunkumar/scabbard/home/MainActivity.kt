package dev.arunkumar.scabbard.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.arunkumar.scabbard.R
import dev.arunkumar.scabbard.appComponent
import dev.arunkumar.scabbard.debug.SimpleSingleton
import dev.arunkumar.scabbard.debug.ComplexSingleton
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var activityScopeDependency: ActivityScopeDependency
    @Inject
    lateinit var applicationSingleton: SimpleSingleton
    @Inject
    lateinit var complexSingleton: ComplexSingleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        application.appComponent()
            .mainActivitySubComponentFactory()
            .create(this)
            .inject(this)
    }
}
