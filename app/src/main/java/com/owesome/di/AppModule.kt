package com.owesome.di

import com.owesome.data.api.RetroFitClient
import com.owesome.data.auth.AuthManager
import com.owesome.data.repository.ExpenseRepository
import com.owesome.data.repository.ExpenseRepositoryImpl
import com.owesome.data.repository.GroupRepository
import com.owesome.data.repository.GroupRepositoryImpl
import com.owesome.data.repository.NotificationRepository
import com.owesome.data.repository.NotificationRepositoryImpl
import com.owesome.data.repository.UserRepository
import com.owesome.data.repository.UserRepositoryImpl
import com.owesome.ui.viewmodels.GroupEditorViewModel
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import com.owesome.ui.viewmodels.AddUserViewModel
import com.owesome.notifications.NotificationFacade
import com.owesome.ui.viewmodels.ExpenseViewModel
import com.owesome.ui.viewmodels.LoginViewModel
import com.owesome.ui.viewmodels.RegisterViewModel
import com.owesome.util.AlertManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GroupViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ExpenseViewModel)
    singleOf(::AuthManager)
    singleOf(::RetroFitClient)
    singleOf(::AlertManager)
    viewModelOf(::AddUserViewModel)
    viewModelOf(::GroupEditorViewModel)
    singleOf(::GroupRepositoryImpl) bind GroupRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::ExpenseRepositoryImpl) bind ExpenseRepository::class
    singleOf(::NotificationRepositoryImpl) bind NotificationRepository::class
    single { get<RetroFitClient>().groupApi }
    single { get<RetroFitClient>().authApi }
    single { get<RetroFitClient>().userApi }
    single { get<RetroFitClient>().expenseAPI }
    single { get<RetroFitClient>().notificationApi }
    viewModel { NavViewModel() }
    singleOf(::NotificationFacade)
}