package com.studentsecurevpn.data.remote

import com.studentsecurevpn.data.remote.model.LoginRequest
import com.studentsecurevpn.data.remote.model.LoginResponse
import com.studentsecurevpn.data.remote.model.ServerDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/** API interface for authentication and authorized server profiles. */
interface VpnApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("servers")
    suspend fun servers(@Header("Authorization") bearerToken: String?): List<ServerDto>
}
