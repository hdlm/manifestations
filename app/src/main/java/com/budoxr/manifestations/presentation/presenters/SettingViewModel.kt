package com.budoxr.manifestations.presentation.presenters

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CommonValues
import com.budoxr.manifestations.commons.TextToSpeechHelper
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.mapper.emptyConfigModel
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.data.mapper.toModel
import com.budoxr.manifestations.presentation.domain.ConfigModel
import com.budoxr.manifestations.presentation.usecase.BackupUseCase
import com.budoxr.manifestations.presentation.usecase.ConfigInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ConfigInsertUseCase
import com.budoxr.manifestations.presentation.usecase.ExportDatabaseUseCase
import com.budoxr.manifestations.presentation.usecase.ImportDatabaseUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertUseCase
import com.budoxr.manifestations.presentation.usecase.RestoreUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SettingViewModel : ViewModel(), KoinComponent {
    private val util: Utily by inject()
    private val backupUseCase: BackupUseCase by inject()
    private val restoreUseCase: RestoreUseCase by inject()
    private val exportDbUseCase: ExportDatabaseUseCase by inject()
    private val importDbUseCase: ImportDatabaseUseCase by inject()
    private val manifestationInfoUseCase: ManifestationInfoUseCase by inject()
    private val manifestationInsertUseCase: ManifestationInsertUseCase by inject()
    private val configInfoUseCase: ConfigInfoUseCase by inject()
    private val configInsertUseCase: ConfigInsertUseCase by inject()
    private val _tts: TextToSpeechHelper by inject()
    val tts: TextToSpeechHelper
        get() = _tts

    private val _isReadGranted = MutableStateFlow(false)
    private val _isWriteGranted = MutableStateFlow(false)
    private val _config = MutableStateFlow(emptyConfigModel())

    val isReadGranted : MutableStateFlow<Boolean>
        get() = _isReadGranted

    val isWriteGranted : MutableStateFlow<Boolean>
        get() = _isWriteGranted

    val config: MutableStateFlow<ConfigModel>
        get() = _config

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _config.value = configInfoUseCase.invoke()

        }
    }


    /**
     * The function it is responsible to copy the [com.budoxr.manifestations.presentation.domain.ManifestationModel](manifestations)
     * registered to a json file into the private storage directory of the app.
     */
    fun backup(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            val manifestations = util.performAsyncOperation(scope = this, timeout = CommonValues.WAIT_DEFERRED, timeUnit = TimeUnit.SECONDS, dispatcher = Dispatchers.IO) {
                manifestationInfoUseCase.invoke(this)
            }.await()
            val models = util.transformList(manifestations) { it.toModel() }
            backupUseCase.invoke(context, models)

        }
    }


    /**
     * The function it is responsible to recover the [com.budoxr.manifestations.presentation.domain.ManifestationModel](manifestations)
     * registered previously saved in the private storage directory of the app.
     */
    fun restore(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            val recoveredManifestations = restoreUseCase.invoke(context)
            val entities = util.transformList(recoveredManifestations) { it.toEntity() }
            manifestationInsertUseCase.invoke(entities)
        }
    }


    fun export(context: Context, selectedFolderUri: Uri,  askWriteExternalStoragePermission: () -> Boolean ) {
        _isWriteGranted.value = askWriteExternalStoragePermission.invoke()

        viewModelScope.launch(Dispatchers.IO) {
            val manifestations = util.performAsyncOperation(scope = this, timeout = CommonValues.WAIT_DEFERRED, timeUnit = TimeUnit.SECONDS, dispatcher = Dispatchers.IO) {
                manifestationInfoUseCase.invoke(this)
            }.await()
            val models = util.transformList(manifestations) { it.toModel() }
            exportDbUseCase.invoke(context, models, selectedFolderUri)

        }

    }

    fun import(context: Context, selectedFolderUri: Uri,  askReadExternalStoragePermission: () -> Boolean) {
        _isReadGranted.value = askReadExternalStoragePermission.invoke()

        viewModelScope.launch(Dispatchers.IO) {
            val manifestations = importDbUseCase.invoke(context, selectedFolderUri)
            manifestationInsertUseCase.invoke(util.transformList(manifestations) { it.toEntity() })
        }
    }


    fun setSpeechRate(rate: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            _config.value.speechRate = rate / 10.0f
            configInsertUseCase.invoke(_config.value)
        }

    }

}