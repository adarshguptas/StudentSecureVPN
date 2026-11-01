package com.studentsecurevpn.data.remote.model

/** Retrofit DTOs. Replace/extend these to match your legitimate backend contract. */
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val userId: String, val premium: Boolean)
data class ServerDto(
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val premium: Boolean,
    val latencyMs: Int,
    val loadPercent: Int,
    val wireGuardConfig: String?
)
