package com.example.triptokailash.model

data class AppointmentModel(
    val appointmentId: String? = null,
    val userId: String? = null,
    val packageId: String? = null,
    val packageName: String? = null,
    val appointmentDate: String? = null,
    val appointmentTime: String? = null,
    val numberOfPeople: Int? = null,
    val price: Double? = null,
    val status: String? = null
)
