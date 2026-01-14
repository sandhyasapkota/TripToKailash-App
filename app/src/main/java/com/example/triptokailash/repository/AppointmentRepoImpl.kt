package com.example.triptokailash.repository

import com.example.triptokailash.model.AppointmentModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppointmentRepoImpl : AppointmentRepo {
    val database = FirebaseDatabase.getInstance()
    val appointmentRef = database.getReference("appointments")

    override fun addAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit) {
        val appointmentId = appointmentRef.push().key
        if (appointmentId == null) {
            callback(false, "Failed to create appointment.")
            return
        }
        val newAppointment = appointment.copy(appointmentId = appointmentId)

        appointmentRef.child(appointmentId).setValue(newAppointment).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Appointment booked successfully!")
            } else {
                callback(false, task.exception?.message ?: "An unknown error occurred.")
            }
        }
    }

    override fun getAppointments(userId: String, callback: (Boolean, String, List<AppointmentModel>) -> Unit) {
        appointmentRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointmentList = mutableListOf<AppointmentModel>()
                if (snapshot.exists()) {
                    for (appointmentSnapshot in snapshot.children) {
                        val appointmentModel = appointmentSnapshot.getValue(AppointmentModel::class.java)
                        appointmentModel?.let { appointmentList.add(it) }
                    }
                }
                callback(true, "Appointments fetched", appointmentList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getAllAppointments(callback: (Boolean, String, List<AppointmentModel>) -> Unit) {
        appointmentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointmentList = mutableListOf<AppointmentModel>()
                if (snapshot.exists()) {
                    for (appointmentSnapshot in snapshot.children) {
                        val appointmentModel = appointmentSnapshot.getValue(AppointmentModel::class.java)
                        appointmentModel?.let { appointmentList.add(it) }
                    }
                }
                callback(true, "All appointments fetched", appointmentList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun cancelAppointment(appointmentId: String, callback: (Boolean, String) -> Unit) {
        appointmentRef.child(appointmentId).child("status").setValue("Cancelled").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Appointment cancelled successfully")
            } else {
                callback(false, task.exception?.message ?: "An error occurred")
            }
        }
    }

    override fun updateAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit) {
        if (appointment.appointmentId == null) {
            callback(false, "Appointment ID is missing.")
            return
        }
        appointmentRef.child(appointment.appointmentId).setValue(appointment).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Appointment updated successfully!")
            } else {
                callback(false, task.exception?.message ?: "An unknown error occurred.")
            }
        }
    }
}
