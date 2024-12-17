package com.example.foresti

import android.app.Application
import com.chibatching.kotpref.Kotpref

class ForestiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
    }
}
