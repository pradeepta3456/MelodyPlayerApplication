package com.example.musicplayerapplication
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.musicplayerapplication.View.SignUpActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginRegisterNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignUpActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testRegisterButtonNavigation() {
        composeRule.onNodeWithTag("register")
            .performClick()
        Intents.intended(hasComponent(SignUpActivity::class.java.name))
    }
}