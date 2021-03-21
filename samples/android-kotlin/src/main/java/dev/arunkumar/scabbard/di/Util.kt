package dev.arunkumar.scabbard.di

import android.app.Application
import dev.arunkumar.scabbard.App

val Application.appComponent get() = (this as App).appComponent
