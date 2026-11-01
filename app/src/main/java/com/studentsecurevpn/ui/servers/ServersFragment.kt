package com.studentsecurevpn.ui.servers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.studentsecurevpn.databinding.FragmentServersBinding
import com.studentsecurevpn.ui.main.MainViewModel

/** Server picker with Free and Premium tabs. */
class ServersFragment : Fragment() {
    private var _binding: FragmentServersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ServerAdapter
    private var showPremium = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentServersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ServerAdapter { server ->
            viewModel.selectServer(server)
            Toast.makeText(requireContext(), "Selected ${server.name}", Toast.LENGTH_SHORT).show()
        }
        binding.serverRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.serverRecyclerView.adapter = adapter
        binding.serverTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showPremium = tab.position == 1
                render(viewModel.servers.value.orEmpty())
            }
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        viewModel.servers.observe(viewLifecycleOwner) { render(it) }
    }

    private fun render(servers: List<com.studentsecurevpn.data.local.entity.ServerEntity>) {
        adapter.submitList(servers.filter { it.isPremium == showPremium })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
