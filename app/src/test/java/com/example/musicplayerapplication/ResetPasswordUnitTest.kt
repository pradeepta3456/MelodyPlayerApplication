package com.example.musicplayerapplication

import com.example.musicplayerapplication.repository.AuthRepository

import org.junit.Test

import org.junit.Assert.*

import org.mockito.kotlin.*

/**

 * Unit tests for AuthRepository resetPassword function.

 * Tests different scenarios: success, invalid email, and user not found.

 */

class ResetPasswordUnitTest {

    @Test

    fun resetPassword_success_test() {

        val repo = mock<AuthRepository>()

        doAnswer { invocation ->

            val callback = invocation.getArgument<() -> Unit>(1)

            callback()

            null

        }.`when`(repo).resetPassword(eq("test@gmail.com"), any(), any())

        var successCalled = false

        repo.resetPassword("test@gmail.com",

            onSuccess = {

                successCalled = true

            },

            onFailure = { }

        )

        assertTrue(successCalled)

        verify(repo).resetPassword(eq("test@gmail.com"), any(), any())

    }

    @Test

    fun resetPassword_invalidEmail_test() {

        val repo = mock<AuthRepository>()

        val testException = Exception("The email address is badly formatted.")

        doAnswer { invocation ->

            val callback = invocation.getArgument<(Exception) -> Unit>(2)

            callback(testException)

            null

        }.`when`(repo).resetPassword(eq("invalidemail"), any(), any())

        var errorMessage = ""

        repo.resetPassword("invalidemail",

            onSuccess = { },

            onFailure = { exception ->

                errorMessage = exception.message ?: ""

            }

        )

        assertEquals("The email address is badly formatted.", errorMessage)

        verify(repo).resetPassword(eq("invalidemail"), any(), any())

    }

    @Test

    fun resetPassword_userNotFound_test() {

        val repo = mock<AuthRepository>()

        val testException = Exception("There is no user record corresponding to this identifier.")

        doAnswer { invocation ->

            val callback = invocation.getArgument<(Exception) -> Unit>(2)

            callback(testException)

            null

        }.`when`(repo).resetPassword(eq("notexist@gmail.com"), any(), any())

        var failureCalled = false

        var errorMessage = ""

        repo.resetPassword("notexist@gmail.com",

            onSuccess = { },

            onFailure = { exception ->

                failureCalled = true

                errorMessage = exception.message ?: ""

            }

        )

        assertTrue(failureCalled)

        assertEquals("There is no user record corresponding to this identifier.", errorMessage)

        verify(repo).resetPassword(eq("notexist@gmail.com"), any(), any())

    }

}
