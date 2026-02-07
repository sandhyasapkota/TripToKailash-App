package com.example.triptokailash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triptokailash.view.ViewPackages
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PackageListInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ViewPackages>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testPackageScreen_displaysBackButton() {
        composeRule.onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun testBackButton_isClickable() {
        composeRule.onNodeWithContentDescription("Back")
            .performClick()
        
        composeRule.waitForIdle()
    }

    @Test
    fun testPackageScreen_loadsSuccessfully() {
        composeRule.waitForIdle()
        
        // Screen should be displayed without crashes
        composeRule.onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }
}