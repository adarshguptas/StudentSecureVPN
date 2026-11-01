package com.studentsecurevpn.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studentsecurevpn.databinding.ActivityPrivacyPolicyBinding

/** Privacy Policy page. Replace sample text with reviewed production policy before publishing. */
class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
