package com.example.musicplayerapplication

import com.example.musicplayerapplication.repository.AuthRepository

import com.google.firebase.auth.FirebaseUser

import org.junit.Test

import org.junit.Assert.*

import org.mockito.kotlin.*

/**

 * Unit tests for AuthRepository signIn function.

 * Tests different scenarios: success, failure, and invalid credentials.

 */

class SignInUnitTest {

    @Test

    fun signIn_success_test() {

        val repo = mock<AuthRepository>()

        val mockUser = mock<FirebaseUser>()

        whenever(mockUser.uid).thenReturn("test123")

        whenever(mockUser.email).thenReturn("test@gmail.com")

        doAnswer { invocation ->

            val callback = invocation.getArgument<(FirebaseUser) -> Unit>(2)

            callback(mockUser)

            null

        }.`when`(repo).signIn(eq("test@gmail.com"), eq("123456"), any(), any())

        var successResult: FirebaseUser? = null

        repo.signIn("test@gmail.com", "123456",

            onSuccess = { user ->

                successResult = user

            },

            onFailure = { }

        )

        assertNotNull(successResult)

        assertEquals("test123", successResult?.uid)

        assertEquals("test@gmail.com", successResult?.email)

        verify(repo).signIn(eq("test@gmail.com"), eq("123456"), any(), any())

    }

    @Test

    fun signIn_invalidCredentials_test() {

        val repo = mock<AuthRepository>()

        val testException = Exception("The password is invalid or the user does not have a password.")

        doAnswer { invocation ->

            val callback = invocation.getArgument<(Exception) -> Unit>(3)

            callback(testException)

            null

        }.`when`(repo).signIn(eq("test@gmail.com"), eq("wrongpass"), any(), any())

        var errorMessage = ""

        repo.signIn("test@gmail.com", "wrongpass",

            onSuccess = { },

            onFailure = { exception ->

                errorMessage = exception.message ?: ""

            }

        )

        assertEquals("The password is invalid or the user does not have a password.", errorMessage)

        verify(repo).signIn(eq("test@gmail.com"), eq("wrongpass"), any(), any())

    }

    @Test

    fun signIn_userNotFound_test() {

        val repo = mock<AuthRepository>()

        val testException = Exception("There is no user record corresponding to this identifier.")

        doAnswer { invocation ->

            val callback = invocation.getArgument<(Exception) -> Unit>(3)

            callback(testException)

            null

        }.`when`(repo).signIn(eq("notexist@gmail.com"), eq("123456"), any(), any())

        var failureCalled = false

        var errorMessage = ""

        repo.signIn("notexist@gmail.com", "123456",

            onSuccess = { },

            onFailure = { exception ->

                failureCalled = true

                errorMessage = exception.message ?: ""

            }

        )

        assertTrue(failureCalled)

        assertEquals("There is no user record corresponding to this identifier.", errorMessage)

        verify(repo).signIn(eq("notexist@gmail.com"), eq("123456"), any(), any())

    }

}
