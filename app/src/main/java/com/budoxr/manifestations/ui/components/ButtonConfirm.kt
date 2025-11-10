package com.budoxr.manifestations.ui.components

import com.budoxr.manifestations.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.ui.theme.bright
import com.budoxr.manifestations.ui.theme.grayLight

@Composable
fun ButtonConfirm(
    modifier: Modifier = Modifier,
    label: String,
    isEnabled: Boolean,
    isDarkTheme: Boolean,
    iconSize: Dp = dimensionResource(id = R.dimen.icon_large_size),
    showTopBorderLine: Boolean,
    @DrawableRes
    buttonIcon: Int?,
    buttonVector: ImageVector?,
    buttonImg: Painter?,
    onConfirmClick: onDismissType,
) {
    val btnHeight = dimensionResource(id = R.dimen.button_height)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val lineSpacing2x = dimensionResource(id = R.dimen.line_spacing_2)
    val separation2x = dimensionResource(id = R.dimen.side_separation_2x)

    val btnForeColor : Color
    if(isEnabled)
    {
        btnForeColor = if(isDarkTheme) MaterialTheme.colorScheme.tertiary else bright
    } else {
        btnForeColor = MaterialTheme.colorScheme.secondary
    }

    if (showTopBorderLine) {
        HorizontalDivider(
            modifier = modifier,
            thickness = 1.dp,
            color = grayLight
        )
        Spacer(modifier = Modifier.height(lineSpacing2x))
    }

    Button( modifier = modifier
        .fillMaxWidth()
        .height(btnHeight),
        onClick = {
            onConfirmClick.invoke()
        },
        enabled = isEnabled,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 1.dp, MaterialTheme.colorScheme.background),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary),

        ) {
        buttonIcon?.let {
            Icon( modifier = Modifier
                .size(iconSize),
                painter = painterResource(id = it),
                contentDescription = stringResource(id = R.string.content_description_icon),
                tint = btnForeColor
            )
        }
        buttonVector?.let {
            Icon( modifier = Modifier
                .size(iconSize),
                imageVector = buttonVector,
                contentDescription = stringResource(id = R.string.content_description_icon),
                tint = btnForeColor
            )
        }
        buttonImg?.let {
            Image( modifier = Modifier
                .size( iconSize ),
                painter = it,
                contentDescription = stringResource(id = R.string.content_description_icon),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(modifier = Modifier.width(separation2x))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = btnForeColor
        )
    }
    Spacer(modifier = Modifier.height(lineSpacing))
}