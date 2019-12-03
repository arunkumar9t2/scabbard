package dev.arunkumar.scabbard.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.arunkumar.scabbard.R
import dev.arunkumar.scabbard.appComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var helloWorld: HelloWorld


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        application.appComponent()
            .mainActivitySubComponentFactory()
            .create()
            .inject(this)
    }
}
