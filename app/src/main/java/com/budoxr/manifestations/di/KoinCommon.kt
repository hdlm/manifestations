package com.budoxr.manifestations.di

import android.content.Context
import androidx.room.Room
import com.budoxr.manifestations.commons.AppScope
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.commons.TextToSpeechHelper
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.database.AppDatabase
import com.budoxr.manifestations.data.database.daos.ConfigDao
import com.budoxr.manifestations.data.database.daos.JournalDao
import com.budoxr.manifestations.data.database.daos.LessonDao
import com.budoxr.manifestations.data.database.daos.ManifestationDao
import com.budoxr.manifestations.data.repositories.ConfigLocalRepository
import com.budoxr.manifestations.data.repositories.ConfigLocalRepositoryImpl
import com.budoxr.manifestations.data.repositories.JournalLocalRepository
import com.budoxr.manifestations.data.repositories.JournalLocalRepositoryImpl
import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import com.budoxr.manifestations.data.repositories.LessonLocalRepositoryImpl
import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.data.repositories.LocalStorageImpl
import com.budoxr.manifestations.data.repositories.ManifestationLocalRepository
import com.budoxr.manifestations.data.repositories.ManifestationLocalRepositoryImpl
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.presenters.JournalViewModel
import com.budoxr.manifestations.presentation.presenters.LessonViewModel
import com.budoxr.manifestations.presentation.presenters.ManifestationViewModel
import com.budoxr.manifestations.presentation.presenters.SettingViewModel
import com.budoxr.manifestations.presentation.usecase.BackupUseCase
import com.budoxr.manifestations.presentation.usecase.CheckPermissionUseCase
import com.budoxr.manifestations.presentation.usecase.ConfigInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ConfigInsertUseCase
import com.budoxr.manifestations.presentation.usecase.ExportDatabaseUseCase
import com.budoxr.manifestations.presentation.usecase.ImportDatabaseUseCase
import com.budoxr.manifestations.presentation.usecase.JournalAnswerExistUseCase
import com.budoxr.manifestations.presentation.usecase.JournalDeleteUseCase
import com.budoxr.manifestations.presentation.usecase.JournalInfoUseCase
import com.budoxr.manifestations.presentation.usecase.JournalInsertUseCase
import com.budoxr.manifestations.presentation.usecase.LessonDeleteUseCase
import com.budoxr.manifestations.presentation.usecase.LessonInfoUseCase
import com.budoxr.manifestations.presentation.usecase.LessonInsertUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationDeleteUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertWorkerUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationLastIdUseCase
import com.budoxr.manifestations.presentation.usecase.RestoreUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val appModule = module {
        single { AppScope() }
        single { TextToSpeechHelper(androidContext()) }
        factory { Utily() }
        single { SessionModel() }
        factory<LocalStorage> { LocalStorageImpl() }
        factory { CheckPermissionUseCase() }
        viewModel { LessonViewModel(get()) }
        viewModel { ManifestationViewModel() }
        viewModel { JournalViewModel(androidContext()) }
        viewModel { SettingViewModel() }
        single { CategoryHelper() }
        factory { BackupUseCase() }
        factory { RestoreUseCase() }
        factory { ExportDatabaseUseCase() }
        factory { ImportDatabaseUseCase() }
    }

    fun provideDataBase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "manifestation.db"
        ).
        fallbackToDestructiveMigration().build()

    fun provideManifestationDao(appDatabase: AppDatabase): ManifestationDao = appDatabase.manifestationDao()
    fun provideLessonDao(appDatabase: AppDatabase): LessonDao = appDatabase.lessonDao()
    fun provideJournalDao(appDatabase: AppDatabase): JournalDao = appDatabase.journalDao()
    fun provideConfigDao(appDatabase: AppDatabase): ConfigDao = appDatabase.configDao()

    val databaseModule = module {
        single { provideDataBase(androidContext()) }
        single { provideManifestationDao(get()) }
        single { provideLessonDao(get()) }
        single { provideJournalDao(get()) }
        single { provideConfigDao(get()) }
        factory { ManifestationInfoUseCase() }
        factory { ManifestationInsertUseCase() }
        factory { ManifestationLastIdUseCase() }
        factory { ManifestationDeleteUseCase() }
        factory { LessonInfoUseCase() }
        factory { LessonInsertUseCase() }
        factory { LessonDeleteUseCase() }
        factory { JournalInfoUseCase() }
        factory { JournalInsertUseCase() }
        factory { JournalDeleteUseCase() }
        factory { JournalAnswerExistUseCase() }
        factory { ConfigInfoUseCase() }
        factory { ConfigInsertUseCase() }
        factory<ManifestationLocalRepository> { ManifestationLocalRepositoryImpl() }
        factory<LessonLocalRepository> { LessonLocalRepositoryImpl() }
        factory<JournalLocalRepository> { JournalLocalRepositoryImpl() }
        factory<ConfigLocalRepository> { ConfigLocalRepositoryImpl() }
    }

    val workerModule = module {
        factory { ManifestationInsertWorkerUseCase(androidContext()) }
    }

    val unitTestModule = module {
    }

}