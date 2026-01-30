package com.example.musicplayerapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.musicplayerapplication.View.SignInActivity
import com.example.musicplayerapplication.View.SignUpActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignInActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testUIElementsAreDisplayed() {
        composeRule.onNodeWithText("Welcome Back!").assertIsDisplayed()
        composeRule.onNodeWithText("Sign in to continue your journey").assertIsDisplayed()
        composeRule.onNodeWithText("Email Address").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun testSignUpButtonNavigation() {
        composeRule.onNodeWithText("Sign Up").performClick()
        Intents.intended(hasComponent(SignUpActivity::class.java.name))
    }

    @Test
    fun testEmailFieldAcceptsInput() {
        composeRule.onAllNodesWithText("your.email@example.com")[0]
            .performTextInput("test@example.com")
        composeRule.waitForIdle()
    }

    @Test
    fun testPasswordFieldAcceptsInput() {
        composeRule.onAllNodesWithText("Enter your password")[0]
            .performTextInput("password123")
        composeRule.waitForIdle()
    }

    @Test
    fun testPasswordVisibilityToggle() {
        composeRule.onAllNodesWithText("Enter your password")[0]
            .performTextInput("mypassword")

        composeRule.onNodeWithContentDescription("Show password")
            .performClick()

        composeRule.onNodeWithContentDescription("Hide password")
            .assertExists()

        composeRule.onNodeWithContentDescription("Hide password")
            .performClick()

        composeRule.onNodeWithContentDescription("Show password")
            .assertExists()
    }

    @Test
    fun testForgotPasswordDialogOpens() {
        composeRule.onNodeWithText("Forgot Password?").performClick()
        composeRule.onNodeWithText("Reset Password").assertIsDisplayed()
    }

    @Test
    fun testForgotPasswordDialogCancel() {
        composeRule.onNodeWithText("Forgot Password?").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Reset Password").assertDoesNotExist()
    }

    @Test
    fun testCompleteSignInFlow() {
        composeRule.onAllNodesWithText("your.email@example.com")[0]
            .performTextInput("test@example.com")

        composeRule.onAllNodesWithText("Enter your password")[0]
            .performTextInput("password123")

        composeRule.onNodeWithText("Sign In").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun testInvalidEmailValidation() {
        composeRule.onAllNodesWithText("your.email@example.com")[0]
            .performTextInput("invalidemail")

        composeRule.onAllNodesWithText("Enter your password")[0]
            .performTextInput("password123")

        composeRule.onNodeWithText("Sign In").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun testShortPasswordValidation() {
        composeRule.onAllNodesWithText("your.email@example.com")[0]
            .performTextInput("test@example.com")

        composeRule.onAllNodesWithText("Enter your password")[0]
            .performTextInput("12345")

        composeRule.onNodeWithText("Sign In").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun testAllUIComponentsPresent() {
        composeRule.onNodeWithContentDescription("Music Note").assertExists()
        composeRule.onAllNodesWithContentDescription("Email").onFirst().assertExists()
        composeRule.onNodeWithContentDescription("Lock").assertExists()
        composeRule.onNodeWithText("Welcome Back!").assertExists()
        composeRule.onNodeWithText("Forgot Password?").assertExists()
    }
}