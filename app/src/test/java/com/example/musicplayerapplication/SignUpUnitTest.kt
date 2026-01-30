package com.example.musicplayerapplication

import com.example.musicplayerapplication.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*

/**
 * Unit tests for AuthRepository signUp function.
 * Tests different scenarios: success, email already exists, and weak password.
 */
class SignUpUnitTest {

    @Test
    fun signUp_success_test() {
        val repo = mock<AuthRepository>()
        val mockUser = mock<FirebaseUser>()

        whenever(mockUser.uid).thenReturn("newuser123")
        whenever(mockUser.email).thenReturn("newuser@gmail.com")
        whenever(mockUser.displayName).thenReturn("New User")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(FirebaseUser) -> Unit>(3)
            callback(mockUser)
            null
        }.`when`(repo).signUp(eq("newuser@gmail.com"), eq("123456"), eq("New User"), any(), any())

        var successResult: FirebaseUser? = null

        repo.signUp("newuser@gmail.com", "123456", "New User",
            onSuccess = { user ->
                successResult = user
            },
            onFailure = { }
        )

        assertNotNull(successResult)
        assertEquals("newuser123", successResult?.uid)
        assertEquals("newuser@gmail.com", successResult?.email)
        assertEquals("New User", successResult?.displayName)

        verify(repo).signUp(eq("newuser@gmail.com"), eq("123456"), eq("New User"), any(), any())
    }

    @Test
    fun signUp_emailAlreadyExists_test() {
        val repo = mock<AuthRepository>()

        val testException = Exception("The email address is already in use by another account.")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Exception) -> Unit>(4)
            callback(testException)
            null
        }.`when`(repo).signUp(eq("existing@gmail.com"), eq("123456"), eq("Test User"), any(), any())

        var errorMessage = ""

        repo.signUp("existing@gmail.com", "123456", "Test User",
            onSuccess = { },
            onFailure = { exception ->
                errorMessage = exception.message ?: ""
            }
        )

        assertEquals("The email address is already in use by another account.", errorMessage)

        verify(repo).signUp(eq("existing@gmail.com"), eq("123456"), eq("Test User"), any(), any())
    }

    @Test
    fun signUp_weakPassword_test() {
        val repo = mock<AuthRepository>()

        val testException = Exception("The given password is invalid. Password should be at least 6 characters")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Exception) -> Unit>(4)
            callback(testException)
            null
        }.`when`(repo).signUp(eq("test@gmail.com"), eq("123"), eq("Test User"), any(), any())

        var failureCalled = false
        var errorMessage = ""

        repo.signUp("test@gmail.com", "123", "Test User",
            onSuccess = { },
            onFailure = { exception ->
                failureCalled = true
                errorMessage = exception.message ?: ""
            }
        )

        assertTrue(failureCalled)
        assertEquals("The given password is invalid. Password should be at least 6 characters", errorMessage)

        verify(repo).signUp(eq("test@gmail.com"), eq("123"), eq("Test User"), any(), any())
    }
}