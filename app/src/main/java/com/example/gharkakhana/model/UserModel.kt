package com.example.gharkakhana.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    @SerialName("id")      val id: String? = null,
    @SerialName("name")    val name: String? = null,
    @SerialName("email")   val email: String? = null,   // ← add this
    @SerialName("address") val address: String? = null,
    @SerialName("phone")   val phone: String? = null
)