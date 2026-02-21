package com.saico.core.model

data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
