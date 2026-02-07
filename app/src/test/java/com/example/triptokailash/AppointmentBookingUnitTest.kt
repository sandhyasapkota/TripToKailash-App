package com.example.triptokailash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.triptokailash.model.AppointmentModel
import com.example.triptokailash.repository.AppointmentRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class AppointmentBookingUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun addAppointment_success_test() {
        val repo = mock<AppointmentRepo>()
        val viewModel = TestAppointmentViewModel(repo)

        val appointment = AppointmentModel(
            appointmentId = "apt123",
            userId = "user123",
            packageId = "pkg123",
            packageName = "Kailash Mansarovar Tour",
            appointmentDate = "2026-06-15",
            appointmentTime = "10:00 AM",
            numberOfPeople = 2,
            price = 300000.0,
            status = "Confirmed"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Appointment booked successfully")
            null
        }.`when`(repo).addAppointment(eq(appointment), any())

        var successResult = false
        var messageResult = ""

        viewModel.addAppointment(appointment) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Appointment booked successfully", messageResult)

        verify(repo).addAppointment(eq(appointment), any())
    }

    @Test
    fun addAppointment_failure_test() {
        val repo = mock<AppointmentRepo>()
        val viewModel = TestAppointmentViewModel(repo)

        val invalidAppointment = AppointmentModel(
            appointmentId = "apt123",
            userId = "",  // Invalid empty user ID
            packageId = "pkg123",
            numberOfPeople = 0  // Invalid number of people
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Invalid appointment data: User ID required")
            null
        }.`when`(repo).addAppointment(eq(invalidAppointment), any())

        var successResult = true
        var messageResult = ""

        viewModel.addAppointment(invalidAppointment) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(!successResult)
        assertEquals("Invalid appointment data: User ID required", messageResult)

        verify(repo).addAppointment(eq(invalidAppointment), any())
    }

    @Test
    fun getAppointments_success_test() {
        val repo = mock<AppointmentRepo>()
        val viewModel = TestAppointmentViewModel(repo)

        val userId = "user123"
        val appointments = listOf(
            AppointmentModel(
                appointmentId = "apt1",
                userId = userId,
                packageName = "Tour 1",
                status = "Confirmed"
            ),
            AppointmentModel(
                appointmentId = "apt2",
                userId = userId,
                packageName = "Tour 2",
                status = "Pending"
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<AppointmentModel>) -> Unit>(1)
            callback(true, "Appointments retrieved successfully", appointments)
            null
        }.`when`(repo).getAppointments(eq(userId), any())

        var successResult = false
        var messageResult = ""
        var retrievedAppointments: List<AppointmentModel>? = null

        viewModel.getAppointments(userId) { success, msg, aptList ->
            successResult = success
            messageResult = msg
            retrievedAppointments = aptList
        }

        assertTrue(successResult)
        assertEquals("Appointments retrieved successfully", messageResult)
        assertEquals(2, retrievedAppointments?.size)
        assertEquals("Tour 1", retrievedAppointments?.get(0)?.packageName)

        verify(repo).getAppointments(eq(userId), any())
    }

    @Test
    fun cancelAppointment_success_test() {
        val repo = mock<AppointmentRepo>()
        val viewModel = TestAppointmentViewModel(repo)

        val appointmentId = "apt123"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Appointment cancelled successfully")
            null
        }.`when`(repo).cancelAppointment(eq(appointmentId), any())

        var successResult = false
        var messageResult = ""

        viewModel.cancelAppointment(appointmentId) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Appointment cancelled successfully", messageResult)

        verify(repo).cancelAppointment(eq(appointmentId), any())
    }

    @Test
    fun cancelAppointment_failure_test() {
        val repo = mock<AppointmentRepo>()
        val viewModel = TestAppointmentViewModel(repo)

        val appointmentId = "nonexistent_apt"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Appointment not found")
            null
        }.`when`(repo).cancelAppointment(eq(appointmentId), any())

        var successResult = true
        var messageResult = ""

        viewModel.cancelAppointment(appointmentId) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(!successResult)
        assertEquals("Appointment not found", messageResult)

        verify(repo).cancelAppointment(eq(appointmentId), any())
    }

    @Test
    fun updateAppointment_success_test() {
        val repo = mock<AppointmentRepo>()
        val viewModel = TestAppointmentViewModel(repo)

        val updatedAppointment = AppointmentModel(
            appointmentId = "apt123",
            userId = "user123",
            packageId = "pkg123",
            appointmentDate = "2026-07-20",  // Updated date
            numberOfPeople = 3,              // Updated number of people
            status = "Confirmed"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Appointment updated successfully")
            null
        }.`when`(repo).updateAppointment(eq(updatedAppointment), any())

        var successResult = false
        var messageResult = ""

        viewModel.updateAppointment(updatedAppointment) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Appointment updated successfully", messageResult)

        verify(repo).updateAppointment(eq(updatedAppointment), any())
    }
}

// Test-specific AppointmentViewModel for dependency injection
class TestAppointmentViewModel(private val repo: AppointmentRepo) {
    
    fun addAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit) {
        repo.addAppointment(appointment, callback)
    }

    fun getAppointments(userId: String, callback: (Boolean, String, List<AppointmentModel>) -> Unit) {
        repo.getAppointments(userId, callback)
    }

    fun getAllAppointments(callback: (Boolean, String, List<AppointmentModel>) -> Unit) {
        repo.getAllAppointments(callback)
    }

    fun cancelAppointment(appointmentId: String, callback: (Boolean, String) -> Unit) {
        repo.cancelAppointment(appointmentId, callback)
    }

    fun updateAppointment(appointment: AppointmentModel, callback: (Boolean, String) -> Unit) {
        repo.updateAppointment(appointment, callback)
    }
}