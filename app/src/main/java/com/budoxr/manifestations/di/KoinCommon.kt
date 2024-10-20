package com.budoxr.manifestations.di

import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.data.repositories.LocalStorageImpl
import com.budoxr.manifestations.presentation.presenters.LessonViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val appModule = module {
        viewModel { LessonViewModel() }
        factory<LocalStorage> { LocalStorageImpl() }
    }

    val unitTestModule = module {
        factory { LessonViewModel() }
    }

}