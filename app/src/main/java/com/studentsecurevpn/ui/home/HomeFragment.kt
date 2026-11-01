package com.studentsecurevpn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.studentsecurevpn.R
import com.studentsecurevpn.databinding.FragmentHomeBinding
import com.studentsecurevpn.domain.model.ConnectionStatus
import com.studentsecurevpn.ui.main.MainActivity
import com.studentsecurevpn.ui.main.MainViewModel

/** Home screen with connect/disconnect, status, retry, and live statistics. */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.connectButton.setOnClickListener { (requireActivity() as MainActivity).connectOrDisconnect() }
        binding.retryButton.setOnClickListener { viewModel.retry() }

        viewModel.status.observe(viewLifecycleOwner) { renderStatus(it) }
        viewModel.selectedServer.observe(viewLifecycleOwner) { server ->
            binding.selectedServerText.text = server?.let { "${it.name} • ${it.city}, ${it.country}" } ?: "No server selected"
        }
        viewModel.duration.observe(viewLifecycleOwner) { binding.durationText.text = "Duration: $it" }
        viewModel.usage.observe(viewLifecycleOwner) { binding.dataUsageText.text = "Data: $it" }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.errorText.visibility = if (error == null) View.GONE else View.VISIBLE
            binding.retryButton.visibility = if (error == null) View.GONE else View.VISIBLE
            binding.errorText.text = error
        }
    }

    private fun renderStatus(status: ConnectionStatus) {
        val (text, button, dot) = when (status) {
            ConnectionStatus.CONNECTED -> Triple("Connected", "Disconnect", R.drawable.status_dot_connected)
            ConnectionStatus.CONNECTING -> Triple("Connecting", "Disconnect", R.drawable.status_dot_connecting)
            ConnectionStatus.DISCONNECTED -> Triple("Disconnected", "Connect", R.drawable.status_dot_disconnected)
            ConnectionStatus.ERROR -> Triple("Disconnected", "Connect", R.drawable.status_dot_disconnected)
        }
        binding.statusText.text = text
        binding.connectButton.text = button
        binding.statusDot.setBackgroundResource(dot)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
