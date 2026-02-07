package com.example.triptokailash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.triptokailash.model.UserModel
import com.example.triptokailash.repository.UserRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class RegisterUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()

        val email = "test@gmail.com"
        val password = "password123"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Registration successful", "user123")
            null
        }.`when`(repo).register(eq(email), eq(password), any())

        var success = false
        var message = ""

        repo.register(email, password) { result, msg, _ ->
            success = result
            message = msg
        }

        assertTrue(success)
        assertEquals("Registration successful", message)
    }

    @Test
    fun register_failure_test() {
        val repo = mock<UserRepo>()

        val email = "invalid"
        val password = "123"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(false, "Invalid email or password", "")
            null
        }.`when`(repo).register(eq(email), eq(password), any())

        var success = true
        var message = ""

        repo.register(email, password) { result, msg, _ ->
            success = result
            message = msg
        }

        assertTrue(!success)
        assertEquals("Invalid email or password", message)
    }

    @Test
    fun addUserToDatabase_success_test() {
        val repo = mock<UserRepo>()

        val userModel = UserModel(
            userId = "123",
            userEmail = "test@gmail.com",
            userName = "Test User"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "User saved successfully")
            null
        }.`when`(repo).addUserToDatabase(eq("123"), eq(userModel), any())

        var success = false
        var message = ""

        repo.addUserToDatabase("123", userModel) { result, msg ->
            success = result
            message = msg
        }

        assertTrue(success)
        assertEquals("User saved successfully", message)
    }
}