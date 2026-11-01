package com.studentsecurevpn.ui.main

import android.Manifest
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.studentsecurevpn.R
import com.studentsecurevpn.databinding.ActivityMainBinding
import com.studentsecurevpn.domain.model.ConnectionStatus
import com.studentsecurevpn.ui.home.HomeFragment
import com.studentsecurevpn.ui.servers.ServersFragment
import com.studentsecurevpn.ui.settings.SettingsFragment

/** Main host Activity and owner of runtime permission flows. */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels()

    private val vpnPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) viewModel.connect()
        else Toast.makeText(this, "VPN permission is required to connect", Toast.LENGTH_LONG).show()
    }

    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        askNotificationPermission()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> show(HomeFragment())
                R.id.nav_servers -> show(ServersFragment())
                R.id.nav_settings -> show(SettingsFragment())
                else -> false
            }
        }
        if (savedInstanceState == null) binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    fun connectOrDisconnect() {
        if (viewModel.status.value == ConnectionStatus.CONNECTED || viewModel.status.value == ConnectionStatus.CONNECTING) {
            viewModel.disconnect()
            return
        }
        if (viewModel.selectedServer.value == null) {
            Toast.makeText(this, "Select a server first", Toast.LENGTH_LONG).show()
            binding.bottomNavigation.selectedItemId = R.id.nav_servers
            return
        }
        val intent = VpnService.prepare(this)
        if (intent != null) vpnPermission.launch(intent) else viewModel.connect()
    }

    private fun show(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
