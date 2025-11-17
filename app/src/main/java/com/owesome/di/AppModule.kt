package com.owesome.di

import com.owesome.data.api.RetroFitClient
import com.owesome.data.auth.AuthManager
import com.owesome.data.repository.GroupRepository
import com.owesome.data.repository.GroupRepositoryImpl
import com.owesome.data.repository.UserRepository
import com.owesome.data.repository.UserRepositoryImpl
import com.owesome.ui.viewmodels.GroupEditorViewModel
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import com.owesome.ui.viewmodels.AddUserViewModel
import com.owesome.notifications.NotificationFacade
import com.owesome.ui.viewmodels.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GroupViewModel)
    viewModelOf(::LoginViewModel)
    //viewModelOf(::RegisterViewModel)
    singleOf(::AuthManager)
    singleOf(::RetroFitClient)
    viewModelOf(::AddUserViewModel)
    viewModelOf(::GroupEditorViewModel)
    singleOf(::GroupRepositoryImpl) bind GroupRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    single { get<RetroFitClient>().groupApi }
    single { get<RetroFitClient>().authApi }
    viewModel { NavViewModel() }
    single { NotificationFacade(androidContext()) }
}