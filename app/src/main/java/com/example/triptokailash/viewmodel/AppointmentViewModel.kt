package com.example.triptokailash.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.triptokailash.model.AppointmentModel
import com.example.triptokailash.repository.AppointmentRepo
import com.example.triptokailash.repository.AppointmentRepoImpl
import com.google.firebase.auth.FirebaseAuth

class AppointmentViewModel() : ViewModel() {
    private val repo: AppointmentRepo = AppointmentRepoImpl()

    private val _appointments = MutableLiveData<List<AppointmentModel>>()
    val appointments: LiveData<List<AppointmentModel>> get() = _appointments

    private val _allAppointments = MutableLiveData<List<AppointmentModel>>()
    val allAppointments: LiveData<List<AppointmentModel>> get() = _allAppointments

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun loadAppointments() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            _loading.postValue(true)
            repo.getAppointments(userId) { success, _, appointmentList ->
                _loading.postValue(false)
                if (success) {
                    _appointments.postValue(appointmentList)
                }
            }
        }
    }

    fun loadAllAppointments() {
        _loading.postValue(true)
        repo.getAllAppointments { success, _, appointmentList ->
            _loading.postValue(false)
            if (success) {
                _allAppointments.postValue(appointmentList)
            }
        }
    }

    fun addAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit) {
        repo.addAppointment(appointment, callback)
    }

    fun cancelAppointment(appointmentId: String, callback: (Boolean, String) -> Unit) {
        repo.cancelAppointment(appointmentId, callback)
    }

    fun updateAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit) {
        repo.updateAppointment(appointment, callback)
    }
}
