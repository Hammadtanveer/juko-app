package com.juko.app.feature.auth

import app.cash.turbine.test
import com.juko.app.core.data.InMemoryTokenManager
import com.juko.app.feature.auth.data.repository.FakeAuthRepository
import com.juko.app.feature.auth.domain.usecase.LoginUseCase
import com.juko.app.feature.auth.presentation.login.LoginEvent
import com.juko.app.feature.auth.presentation.login.LoginSideEffect
import com.juko.app.feature.auth.presentation.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var tokenManager: InMemoryTokenManager
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = InMemoryTokenManager()
        fakeRepo = FakeAuthRepository(tokenManager)
        viewModel = LoginViewModel(LoginUseCase(fakeRepo))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals("", initialState.email)
            assertEquals("", initialState.password)
            assertFalse(initialState.isLoading)
        }
    }

    @Test
    fun `email changed updates state`() = runTest {
        viewModel.state.test {
            awaitItem() // Skip initial
            viewModel.onEvent(LoginEvent.EmailChanged("test@juko.app"))
            val state = awaitItem()
            assertEquals("test@juko.app", state.email)
        }
    }

    @Test
    fun `login success triggers side effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.EmailChanged("driver@juko.app"))
            viewModel.onEvent(LoginEvent.PasswordChanged("Password123"))
            viewModel.onEvent(LoginEvent.Submit)
            
            assertEquals(LoginSideEffect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `login failure updates error state`() = runTest {
        viewModel.state.test {
            awaitItem() // Initial
            
            viewModel.onEvent(LoginEvent.EmailChanged("error@juko.app"))
            awaitItem() // Email changed
            
            viewModel.onEvent(LoginEvent.PasswordChanged("Password123"))
            awaitItem() // Password changed
            
            viewModel.onEvent(LoginEvent.Submit)
            
            // Should see loading true then false with error
            assertTrue(awaitItem().isLoading)
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals("Invalid credentials", finalState.generalError)
        }
    }
}
