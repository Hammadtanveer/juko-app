package com.juko.app.feature.auth.di

import com.juko.app.feature.auth.data.repository.FakeAuthRepository
import com.juko.app.feature.auth.domain.repository.AuthRepository
import com.juko.app.feature.auth.domain.usecase.*
import com.juko.app.feature.auth.presentation.login.LoginViewModel
import com.juko.app.feature.auth.presentation.otp.OtpViewModel
import com.juko.app.feature.auth.presentation.signup.SignupViewModel
import org.koin.dsl.module

val authModule = module {
    // Repository - Using Fake for now as per user request
    single<AuthRepository> { FakeAuthRepository(get()) }

    // UseCases
    factory { LoginUseCase(get()) }
    factory { SignupUseCase(get()) }
    factory { SendOtpUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }
    factory { LogoutUseCase(get()) }

    // ViewModels
    factory { LoginViewModel(get()) }
    factory { SignupViewModel(get()) }
    factory { OtpViewModel(get(), get(), userId = "usr_001") } // userId placeholder
}
