package com.example.musicplayerapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.musicplayerapplication.View.SignInActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInPasswordInputTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignInActivity>()

    @Test
    fun testPasswordFieldAcceptsInput() {
        composeRule.onNodeWithTag("password")
            .performTextInput("password123")
        composeRule.waitForIdle()
    }
}