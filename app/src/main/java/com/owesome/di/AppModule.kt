package com.owesome.di

import com.owesome.data.repository.GroupRepository
import com.owesome.data.repository.GroupRepositoryImpl
import com.owesome.ui.groups.GroupViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GroupViewModel)
    singleOf(::GroupRepositoryImpl) bind GroupRepository::class
}