package com.owesome.di

import com.owesome.ui.groups.GroupViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GroupViewModel)
}