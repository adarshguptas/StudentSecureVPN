package com.studentsecurevpn.ui.about

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studentsecurevpn.databinding.ActivityAboutBinding

/** About page with policy-safe app description. */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.privacyButton.setOnClickListener { startActivity(Intent(this, PrivacyPolicyActivity::class.java)) }
    }
}
