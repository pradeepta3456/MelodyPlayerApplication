package com.example.musicplayerapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.musicplayerapplication.View.SignInActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInEmailInputTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignInActivity>()

    @Test
    fun testEmailFieldAcceptsInput() {
        composeRule.onNodeWithTag("email")
            .performTextInput("test@example.com")
        composeRule.waitForIdle()
    }
}