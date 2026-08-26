package com.juko.app.feature.auth

import com.juko.app.core.data.InMemoryTokenManager
import com.juko.app.feature.auth.data.repository.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryTest {

    private lateinit var tokenManager: InMemoryTokenManager
    private lateinit var repository: FakeAuthRepository

    @BeforeTest
    fun setup() {
        tokenManager = InMemoryTokenManager()
        repository = FakeAuthRepository(tokenManager)
    }

    @Test
    fun `login success saves tokens`() = runTest {
        val result = repository.login("driver@juko.app", "password123")
        
        assertTrue(result.isSuccess)
        assertNotNull(tokenManager.getAccessToken())
        assertEquals("fake_access_token", tokenManager.getAccessToken())
    }

    @Test
    fun `login failure returns error`() = runTest {
        val result = repository.login("error@juko.app", "password123")
        
        assertTrue(result.isFailure)
        assertNull(tokenManager.getAccessToken())
    }

    @Test
    fun `logout clears tokens`() = runTest {
        tokenManager.saveAccessToken("some_token")
        
        val result = repository.logout()
        
        assertTrue(result.isSuccess)
        assertNull(tokenManager.getAccessToken())
    }
}
