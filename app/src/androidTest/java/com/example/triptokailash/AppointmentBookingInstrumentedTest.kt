package com.example.triptokailash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triptokailash.view.AppointmentFormActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppointmentBookingInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<AppointmentFormActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testBookingScreen_displaysTitle() {
        composeRule.onNodeWithText("Book Trip")
            .assertIsDisplayed()
    }

    @Test
    fun testDateField_isClickable() {
        composeRule.waitForIdle()
        
        composeRule.onNodeWithTag("dateField")
            .performClick()
        
        composeRule.waitForIdle()
    }

    @Test
    fun testNumberOfPeopleField_isDisplayed() {
        composeRule.waitForIdle()
        
        composeRule.onNodeWithTag("numberOfPeopleField")
            .assertIsDisplayed()
    }
}