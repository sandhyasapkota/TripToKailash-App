package com.example.triptokailash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triptokailash.view.Login
import com.example.triptokailash.view.RegisterActivity2
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {
    
    @get:Rule
    val composeRule = createAndroidComposeRule<Login>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testLoginScreen_emailFieldAcceptsInput() {
        composeRule.onNodeWithTag("emailField")
            .performTextInput("test@gmail.com")
        
        composeRule.waitForIdle()
    }

    @Test
    fun testLoginScreen_passwordFieldAcceptsInput() {
        composeRule.onNodeWithTag("passwordField")
            .performTextInput("password123")
        
        composeRule.waitForIdle()
    }

    @Test
    fun testLoginButton_isClickable() {
        composeRule.onNodeWithTag("emailField")
            .performTextInput("test@gmail.com")
        
        composeRule.onNodeWithTag("passwordField")
            .performTextInput("password123")
        
        composeRule.onNodeWithTag("loginButton")
            .performClick()
        
        composeRule.waitForIdle()
    }

    @Test
    fun testSignUpLink_navigatesToRegisterScreen() {
        composeRule.onNodeWithText("Sign Up")
            .performClick()
        
        composeRule.waitForIdle()
        
        Intents.intended(hasComponent(RegisterActivity2::class.java.name))
    }
}