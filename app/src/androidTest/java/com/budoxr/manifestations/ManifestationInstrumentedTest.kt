package com.budoxr.manifestations

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.budoxr.manifestations.commons.CATEGORIES
import com.budoxr.manifestations.commons.emitLastestPeriodically
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.AppDatabase
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.di.Modules
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.util.Date
import kotlin.time.Duration.Companion.seconds


@RunWith(AndroidJUnit4::class)
@SmallTest
class ManifestationInstrumentedTest : KoinComponent {


    private val manifestationInfoUseCase : ManifestationInfoUseCase by inject(ManifestationInfoUseCase::class.java)
    private val manifestationInsertUseCase by inject<ManifestationInsertUseCase>(ManifestationInsertUseCase::class.java)
    private val testDispatcher = StandardTestDispatcher()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        GlobalContext.startKoin {
            androidLogger()
            androidContext(ApplicationProvider.getApplicationContext())
            modules(Modules.unitTestModule, Modules.databaseModule)
        }

        Dispatchers.setMain(testDispatcher)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appDatabase = Room
            .inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        loadKoinModules(module { single(createdAtStart = true) { appDatabase } })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        unloadKoinModules( Modules.unitTestModule )
        unloadKoinModules( Modules.databaseModule )
        stopKoin()
    }

    @Test
    fun insertAndRetrieveManifestationRegister(): Unit = runTest {
        val flowOfManifestation = getManifestationFlow().stateIn(
            scope = this,
            started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
            initialValue = emptyList())

        val model = ManifestationModel(
            id = null,
            overview = "USD 1K en ingresos mensuales",
            description = "Estoy feliz y agradecido por haber recibido un total de ingresos mensuales de USD 3K",
            creationDate = Date().toFechaTimeDb(),
            dueDate = Date().toFechaTimeDb(),
            category = CATEGORIES.SPIRITUALITY.key,
        )
        manifestationInsertUseCase.invoke(model.toEntity())

        val expected = 1
        var count = 0

        withTimeout(5000) { // Timeout after 5 seconds
            flowOfManifestation.collect { value ->
                count = value.size
                if (count == expected) {
                    return@collect
                }
            }
        }

        assertEquals("rows expected", expected, count)

    }

    private fun getManifestationFlow() : Flow<List<ManifestationModel>> {
        return manifestationInfoUseCase.invoke()
            .emitLastestPeriodically(3.seconds)

    }


}