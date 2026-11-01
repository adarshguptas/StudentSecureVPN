package com.studentsecurevpn.domain.model

/** Supported DNS presets inserted into the WireGuard interface config. */
enum class DnsProvider(val displayName: String, val addresses: List<String>) {
    CLOUDFLARE("Cloudflare", listOf("1.1.1.1", "1.0.0.1")),
    GOOGLE("Google", listOf("8.8.8.8", "8.8.4.4")),
    QUAD9("Quad9", listOf("9.9.9.9", "149.112.112.112"));

    companion object {
        fun fromName(value: String?): DnsProvider = entries.firstOrNull { it.name == value } ?: CLOUDFLARE
    }
}
