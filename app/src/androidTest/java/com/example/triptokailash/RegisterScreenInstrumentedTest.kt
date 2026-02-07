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
class RegisterInstrumentedTest {
    
    @get:Rule
    val composeRule = createAndroidComposeRule<RegisterActivity2>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }


    @Test
    fun testRegisterScreen_fullNameFieldAcceptsInput() {
        composeRule.onNodeWithTag("nameField")
            .performTextInput("Test User")
        
        composeRule.waitForIdle()
    }

    @Test
    fun testRegisterScreen_emailFieldAcceptsInput() {
        composeRule.onNodeWithTag("emailField")
            .performTextInput("test@gmail.com")
        
        composeRule.waitForIdle()
    }

    @Test
    fun testRegisterScreen_passwordFieldAcceptsInput() {
        composeRule.onNodeWithTag("passwordField")
            .performTextInput("password123")
        
        composeRule.waitForIdle()
    }

    @Test
    fun testRegisterButton_isClickable() {
        composeRule.onNodeWithTag("nameField")
            .performTextInput("Test User")
        
        composeRule.onNodeWithTag("emailField")
            .performTextInput("test@gmail.com")
        
        composeRule.onNodeWithTag("passwordField")
            .performTextInput("password123")
        
        composeRule.onNodeWithTag("registerButton")
            .performClick()
        
        composeRule.waitForIdle()
    }

    @Test
    fun testSignInLink_navigatesToLoginScreen() {
        composeRule.onNodeWithText("Sign In")
            .performClick()
        
        composeRule.waitForIdle()
        
        Intents.intended(hasComponent(Login::class.java.name))
    }
}