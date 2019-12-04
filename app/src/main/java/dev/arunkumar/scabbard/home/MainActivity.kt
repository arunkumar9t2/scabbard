package dev.arunkumar.scabbard.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.arunkumar.scabbard.ASingleton
import dev.arunkumar.scabbard.BSingleton
import dev.arunkumar.scabbard.R
import dev.arunkumar.scabbard.appComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var activityScopeDependency: ActivityScopeDependency
    @Inject
    lateinit var aSingleton: ASingleton
    @Inject
    lateinit var bSingleton: BSingleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        application.appComponent()
            .mainActivitySubComponentFactory()
            .create(this)
            .inject(this)
    }
}
