package com.studentsecurevpn.ui.servers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studentsecurevpn.data.local.entity.ServerEntity
import com.studentsecurevpn.databinding.ItemServerBinding

/** RecyclerView adapter for free and premium server categories. */
class ServerAdapter(private val onSelect: (ServerEntity) -> Unit) : ListAdapter<ServerEntity, ServerAdapter.ServerViewHolder>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        return ServerViewHolder(ItemServerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ServerViewHolder(private val binding: ItemServerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(server: ServerEntity) {
            binding.serverNameText.text = server.name
            val category = if (server.isPremium) "Premium" else "Free"
            binding.serverMetaText.text = "$category • ${server.city}, ${server.country} • ${server.latencyMs} ms • Load ${server.loadPercent}%"
            binding.selectServerButton.setOnClickListener { onSelect(server) }
        }
    }

    object Diff : DiffUtil.ItemCallback<ServerEntity>() {
        override fun areItemsTheSame(oldItem: ServerEntity, newItem: ServerEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ServerEntity, newItem: ServerEntity): Boolean = oldItem == newItem
    }
}
