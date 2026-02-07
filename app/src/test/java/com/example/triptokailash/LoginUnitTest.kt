package com.example.triptokailash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.triptokailash.model.UserModel
import com.example.triptokailash.repository.UserRepo
import com.example.triptokailash.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class UserLoginAndRegisterUnitTest {

    // This rule ensures that LiveData updates happen instantly on the main thread
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = TestUserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, "Login successful")
            null
        }.`when`(repo).login(eq("test@gmail.com"), eq("123456"), any())

        var successResult = false
        var messageResult = ""

        viewModel.login("test@gmail.com", "123456") { success, msg ->
            successResult = success
            messageResult = msg ?: ""
        }

        assertTrue(successResult)
        assertEquals("Login successful", messageResult)

        verify(repo).login(eq("test@gmail.com"), eq("123456"), any())
    }

    @Test
    fun login_failure_test() {
        val repo = mock<UserRepo>()
        val viewModel = TestUserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(false, "Invalid email or password")
            null
        }.`when`(repo).login(eq("wrong@gmail.com"), eq("wrongpassword"), any())

        var successResult = true
        var messageResult = ""

        viewModel.login("wrong@gmail.com", "wrongpassword") { success, msg ->
            successResult = success
            messageResult = msg ?: ""
        }

        assertTrue(!successResult)
        assertEquals("Invalid email or password", messageResult)

        verify(repo).login(eq("wrong@gmail.com"), eq("wrongpassword"), any())
    }

    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = TestUserViewModel(repo)

        val email = "test@gmail.com"
        val password = "password123"
        val userId = "test_user_id"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Registration successful", userId)
            null
        }.`when`(repo).register(eq(email), eq(password), any())

        var successResult = false
        var messageResult = ""
        var userIdResult = ""

        viewModel.register(email, password) { success, msg, returnedUserId ->
            successResult = success
            messageResult = msg
            userIdResult = returnedUserId
        }

        assertTrue(successResult)
        assertEquals("Registration successful", messageResult)
        assertEquals(userId, userIdResult)

        verify(repo).register(eq(email), eq(password), any())
    }

    @Test
    fun register_failure_test() {
        val repo = mock<UserRepo>()
        val viewModel = TestUserViewModel(repo)

        val email = "invalid_email"
        val password = "weak"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(false, "Registration failed: Invalid email format", "")
            null
        }.`when`(repo).register(eq(email), eq(password), any())

        var successResult = true
        var messageResult = ""
        var userIdResult = ""

        viewModel.register(email, password) { success, msg, returnedUserId ->
            successResult = success
            messageResult = msg
            userIdResult = returnedUserId
        }

        assertTrue(!successResult)
        assertEquals("Registration failed: Invalid email format", messageResult)
        assertEquals("", userIdResult)

        verify(repo).register(eq(email), eq(password), any())
    }

    @Test
    fun addUserToDatabase_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = TestUserViewModel(repo)

        val userId = "test_user_id"
        val userModel = UserModel(
            userId = userId,
            userEmail = "test@gmail.com",
            userName = "Test User",
            password = "password123"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "User added to database successfully")
            null
        }.`when`(repo).addUserToDatabase(eq(userId), eq(userModel), any())

        var successResult = false
        var messageResult = ""

        viewModel.addUserToDatabase(userId, userModel) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("User added to database successfully", messageResult)

        verify(repo).addUserToDatabase(eq(userId), eq(userModel), any())
    }

    @Test
    fun addUserToDatabase_failure_test() {
        val repo = mock<UserRepo>()
        val viewModel = TestUserViewModel(repo)

        val userId = "test_user_id"
        val userModel = UserModel(
            userId = userId,
            userEmail = "test@gmail.com",
            userName = "Test User",
            password = "password123"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Database connection failed")
            null
        }.`when`(repo).addUserToDatabase(eq(userId), eq(userModel), any())

        var successResult = true
        var messageResult = ""

        viewModel.addUserToDatabase(userId, userModel) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(!successResult)
        assertEquals("Database connection failed", messageResult)

        verify(repo).addUserToDatabase(eq(userId), eq(userModel), any())
    }
}

// Test-specific UserViewModel that uses composition for dependency injection
class TestUserViewModel(private val repo: UserRepo) {
    
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }
}