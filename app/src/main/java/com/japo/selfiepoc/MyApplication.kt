package com.japo.selfiepoc

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class MyApplication : Application() {
    companion object {
        lateinit var instance: com.japo.selfiepoc.MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
    }
}
