package com.budoxr.manifestations.di

import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.data.repositories.LocalStorageImpl
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.presenters.LessonViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val appModule = module {
        single { SessionModel() }
        viewModel { LessonViewModel(get()) }
        factory<LocalStorage> { LocalStorageImpl() }
    }

    val unitTestModule = module {
        factory { LessonViewModel(get()) }
    }

}