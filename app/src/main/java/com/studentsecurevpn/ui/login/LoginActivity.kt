package com.studentsecurevpn.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.studentsecurevpn.databinding.ActivityLoginBinding
import com.studentsecurevpn.ui.main.MainActivity

/** Login screen with normal authentication and Guest Mode. */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            viewModel.login(binding.emailInput.text.toString(), binding.passwordInput.text.toString())
        }
        binding.guestButton.setOnClickListener { viewModel.guest() }

        viewModel.loading.observe(this) { binding.loginProgress.visibility = if (it) View.VISIBLE else View.GONE }
        viewModel.error.observe(this) { it?.let { message -> Toast.makeText(this, message, Toast.LENGTH_LONG).show() } }
        viewModel.success.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
