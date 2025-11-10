package com.owesome.di

import com.owesome.MainActivity
import com.owesome.data.repository.GroupRepository
import com.owesome.data.repository.GroupRepositoryImpl
import com.owesome.data.repository.UserRepository
import com.owesome.data.repository.UserRepositoryImpl
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import com.owesome.ui.viewmodels.UserViewModel
import org.koin.androidx.scope.dsl.activityScope
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GroupViewModel)
    viewModelOf(::UserViewModel)
    singleOf(::GroupRepositoryImpl) bind GroupRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    viewModel { NavViewModel() }
}