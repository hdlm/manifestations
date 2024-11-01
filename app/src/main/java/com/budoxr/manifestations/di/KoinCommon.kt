package com.budoxr.manifestations.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.budoxr.manifestations.commons.AppScope
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.database.AppDatabase
import com.budoxr.manifestations.data.database.daos.ManifestationDao
import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.data.repositories.LocalStorageImpl
import com.budoxr.manifestations.data.repositories.ManifestationLocalRepository
import com.budoxr.manifestations.data.repositories.ManifestationLocalRepositoryImpl
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.presenters.LessonViewModel
import com.budoxr.manifestations.presentation.presenters.ManifestationViewModel
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val appModule = module {
        single { AppScope() }
        factory { Utily() }
        single { SessionModel() }
        factory<LocalStorage> { LocalStorageImpl() }
        viewModel { LessonViewModel(get()) }
        viewModel { ManifestationViewModel() }
        factory { ManifestationInfoUseCase() }
        factory<ManifestationLocalRepository> { ManifestationLocalRepositoryImpl() }

        single { CategoryHelper() }
    }

    fun provideDataBase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "manifestation.db"
        ).
        fallbackToDestructiveMigration().build()

    fun provideManifestationDao(appDatabase: AppDatabase): ManifestationDao = appDatabase.manifestationDao()

    val databaseModule = module {
        single { provideDataBase(androidContext()) }
        single { provideManifestationDao(get()) }
    }

    val unitTestModule = module {
        factory { LessonViewModel(get()) }
    }

}