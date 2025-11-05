package com.budoxr.manifestations.ui.features

import com.budoxr.manifestations.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.ui.theme.ManifestationsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopBar(
    label: String,
    icon: ImageVector? = Icons.Filled.Close,
    onBackButtonClick: onDismissType,
    iconAction: ImageVector? = null,
    onActionClick: onDismissType
)  {
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (icon != null) {
                IconButton(onClick = { onBackButtonClick.invoke() }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = R.string.content_description_icon),
                    )
                }
            }
        },
        title = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall
            )
        },
        actions = {
            if (iconAction != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = stringResource(id = R.string.content_description_icon)
                    )
                }
            }
        },
        //TODO check the behavior of the colors
//        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.background,
//            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//        )
    )
}


@Composable
@Preview(showBackground = true)
private fun MainScreenTopBarPreview()
{
    ManifestationsTheme {

        MainScreenTopBar(
            label = "T I T U L O",
            icon = Icons.Filled.Close,
            onBackButtonClick = {},
            iconAction = Icons.Filled.AccessAlarm,
            onActionClick = {},
        )

    }

}