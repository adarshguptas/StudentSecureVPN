package com.studentsecurevpn.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.studentsecurevpn.ui.login.LoginActivity
import com.studentsecurevpn.ui.main.MainActivity
import com.studentsecurevpn.util.ServiceLocator

/** Material Android 12+ splash screen that routes signed-in/guest users to Home. */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val destination = if (ServiceLocator.authRepository.isSignedInOrGuest()) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, destination))
        finish()
    }
}
