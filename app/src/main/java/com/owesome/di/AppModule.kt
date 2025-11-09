package com.owesome.di

import com.owesome.data.api.RetroFitClient
import com.owesome.data.auth.AuthManager
import com.owesome.data.repository.GroupRepository
import com.owesome.data.repository.GroupRepositoryImpl
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GroupViewModel)
    singleOf(::AuthManager)
    singleOf(::RetroFitClient)
    singleOf(::GroupRepositoryImpl) bind GroupRepository::class
    single { get<RetroFitClient>().groupApi }
    single { get<RetroFitClient>().authApi }
    viewModel { NavViewModel() }
}