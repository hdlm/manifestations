package com.budoxr.manifestations.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onFloatType
import com.budoxr.manifestations.data.mapper.emptyConfigModel
import com.budoxr.manifestations.presentation.domain.ConfigModel
import com.budoxr.manifestations.presentation.presenters.SettingViewModel
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.grayLight
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingBottomSheet(
    onDismissBottomSheet: onDismissType,
    askReadExternalStoragePermission: () -> Boolean,
    askWriteExternalStoragePermission: () -> Boolean,
    viewModel: SettingViewModel = koinViewModel()
) {
    val sheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true )
    val scope = rememberCoroutineScope()

    val isReadGranted = viewModel.isReadGranted.collectAsStateWithLifecycle()
    val isWriteGranted = viewModel.isWriteGranted.collectAsStateWithLifecycle()
    val config = viewModel.config.collectAsStateWithLifecycle()

    val onDismissRequest: onDismissType = {
        scope.launch {
            viewModel.tts.config.value.speechRate = config.value.speechRate
            sheetState.hide()
        }.invokeOnCompletion {
            onDismissBottomSheet.invoke()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        sheetState = sheetState,
    ) {
        SettingContentBottomSheet(
            isReadGranted = isReadGranted.value,
            isWriteGranted = isWriteGranted.value,
            askReadExternalStoragePermission = askReadExternalStoragePermission,
            askWriteExternalStoragePermission = askWriteExternalStoragePermission,
            onDismissRequest = onDismissRequest,
            onBackup = viewModel::backup,
            onRestore = viewModel::restore,
            onExport = viewModel::export,
            onImport = viewModel::import,
            onSpeechRateChange = viewModel::setSpeechRate,
            config = config.value
        )

    }

}

@Composable
fun SettingContentBottomSheet(
    onDismissRequest: onDismissType,
    isReadGranted: Boolean,
    isWriteGranted: Boolean,
    askReadExternalStoragePermission: () -> Boolean,
    askWriteExternalStoragePermission: () -> Boolean,
    onBackup: (Context) -> Unit,
    onRestore: (Context) -> Unit,
    onImport: (Context, Uri, () -> Boolean) -> Unit,
    onExport: (Context, Uri, () -> Boolean) -> Unit,
    onSpeechRateChange: onFloatType,
    config: ConfigModel
) {
    val horizontalMargin = dimensionResource(R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(R.dimen.line_spacing_3)
    val iconTopBarSize = dimensionResource(R.dimen.icon_topbar_size)

    val context = LocalContext.current
    var isExport by remember { mutableStateOf(true) }
    var selectedFolderUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var speechRate by remember { mutableFloatStateOf(config.speechRate * 10.0f) }
    var isFirstTime by remember { mutableStateOf(true) }

    val message = stringResource(R.string.label_no_folder_selected)
    val actionLabel = stringResource(R.string.label_close)

    val selectFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        selectedFolderUri = uri
    }
    LaunchedEffect(selectedFolderUri) {
        selectedFolderUri?.let {
            Log.d(TAG, "Selected folder URI: $it")
            if (isExport) {
                onExport.invoke(context, it, askWriteExternalStoragePermission)
            } else {
                onImport.invoke(context, it, askReadExternalStoragePermission)
            }
        } ?: run {
            Log.d(TAG, "No folder selected")
            if (isFirstTime) {
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = false,
                    duration = SnackbarDuration.Short
                )
                isFirstTime = false
            }

        }
    }

    val onBackupButtonClick: onDismissType = {
        Log.d(TAG, "onBackupButtonClick() -> invoked")
        onBackup.invoke(context)

    }
    val onRestoreButtonClick: onDismissType = {
        Log.d(TAG, "onRestoreButtonClick() -> invoked")
        onRestore.invoke(context)

    }
    val onImportButtonClick: onDismissType = {
        Log.d(TAG, "onImportButtonClick() -> invoked")
        isExport = true
        selectFolderLauncher.launch(null)

    }
    val onExportButtonClick: onDismissType = {
        Log.d(TAG, "onExportButtonClick() -> invoked")
        isExport = false
        selectFolderLauncher.launch(null)

    }


    Box {
        Column( horizontalAlignment = Alignment.CenterHorizontally) {
            Row( modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box ( modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    IconButton( modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = { onDismissRequest.invoke() }
                    ) {
                        Icon(
                            modifier = Modifier.size(iconTopBarSize),
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.content_description_icon),
                        )

                    }
                    Text( modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.title_settings),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }

            }

            Spacer(modifier = Modifier.height(lineSpacing))

            Column (modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        onBackupButtonClick.invoke()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.button_backup),
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(lineSpacing))

                Button(
                    onClick = {
                        onRestoreButtonClick.invoke()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.button_restore),
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(lineSpacing))

                Button(
                    onClick = {
                        onExportButtonClick.invoke()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.button_import),
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(lineSpacing))

                Button(
                    onClick = {
                        onImportButtonClick.invoke()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.button_export),
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(lineSpacing))

            Column( modifier = Modifier.padding(horizontal = horizontalMargin)) {
                HorizontalDivider( modifier = Modifier, thickness = 1.dp, color = grayLight )
                Spacer(modifier = Modifier.height(lineSpacing))

                Text(
                    text = stringResource(R.string.label_speech_rate),
                    modifier = Modifier.align(Alignment.Start)
                )

                Slider(
                    value = speechRate,
                    onValueChange = {
                        speechRate = it
                        onSpeechRateChange.invoke(it)
                    },
                    steps = 9,
                    valueRange = 0f..10f
                )
                Text(
                    text = floor(speechRate).toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

            }

            Spacer(modifier = Modifier.height(lineSpacing))

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SettingBottomSheetPreview() {

    val askReadExternalStoragePermission: () -> Boolean = { true }
    val askWriteExternalStoragePermission: () -> Boolean = { true }

    val viewModel =  SettingViewModel()

    ManifestationsTheme {
        SettingContentBottomSheet(
            onDismissRequest = {},
            isReadGranted = true,
            isWriteGranted = true,
            askReadExternalStoragePermission = askReadExternalStoragePermission,
            askWriteExternalStoragePermission = askWriteExternalStoragePermission,
            onBackup = viewModel::backup,
            onRestore = viewModel::restore,
            onImport = viewModel::import,
            onExport = viewModel::export,
            onSpeechRateChange = viewModel::setSpeechRate,
            config = emptyConfigModel()
        )

    }

}

private const val TAG = "che.SettingBottomSheet"