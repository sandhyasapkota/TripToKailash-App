package com.example.triptokailash.model

data class UserModel (
    val userId: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val address: String? = null,
    val contactNumber: String? = null,
    val gender: String? = null,
    val profilePictureUrl: String? = null,
    val password: String? = null,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "userName" to userName,
            "userEmail" to userEmail,
            "address" to address,
            "contactNumber" to contactNumber,
            "gender" to gender,
            "profilePictureUrl" to profilePictureUrl
        )
    }
}
