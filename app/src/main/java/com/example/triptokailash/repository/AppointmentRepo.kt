package com.example.triptokailash.repository

import com.example.triptokailash.model.AppointmentModel

interface AppointmentRepo {
    fun addAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit)
    fun getAppointments(userId: String, callback: (Boolean, String, List<AppointmentModel>) -> Unit)
    fun getAllAppointments(callback: (Boolean, String, List<AppointmentModel>) -> Unit)
    fun cancelAppointment(appointmentId: String, callback: (Boolean, String) -> Unit)
    fun updateAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit)
}
