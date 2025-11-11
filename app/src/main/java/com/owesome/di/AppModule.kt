package com.owesome.di

import com.owesome.data.repository.GroupRepository
import com.owesome.data.repository.GroupRepositoryImpl
import com.owesome.notifications.NotificationFacade
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GroupViewModel)
    singleOf(::GroupRepositoryImpl) bind GroupRepository::class
    viewModel { NavViewModel() }
    single { NotificationFacade(androidContext()) }
}