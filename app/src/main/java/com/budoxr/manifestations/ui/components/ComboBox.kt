package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.ui.theme.emotions
import com.budoxr.manifestations.ui.theme.gray
import com.budoxr.manifestations.ui.theme.passion

@Composable
fun ComboBox(
    items: Array<String>,
    label: String,
    field: MutableState<TextFieldValue>,
    maxlength: Int = -1,
    omitLabel: Boolean = true,
    enabled: Boolean = true,
) {

    var expanded = remember { mutableStateOf( false ) }
    var selectedIndex = remember { mutableStateOf( 0 ) }

    ComboBoxText(
        modifier = Modifier,
        field = field,
        items = items,
        label = label,
        maxlength = maxlength,
        selectedIndex = selectedIndex,
        expanded = expanded,
        enabled = enabled
    )

}


@Composable
fun ComboBoxIcon(
    icon: ImageVector,
    onIconClick: onDismissType,
    enabled: Boolean = true,
    modifier: Modifier
)
{
    ComboBoxButtonIcon(
        modifier = Modifier,
        icon = icon,
        onIconClick = onIconClick,
        enabled = enabled
    )

}

@Composable
private fun ComboBoxText(modifier: Modifier,
                         field: MutableState<TextFieldValue>,
                         items: Array<String>,
                         label: String,
                         maxlength: Int,
                         selectedIndex: MutableState<Int>,
                         expanded: MutableState<Boolean>,
                         enabled: Boolean = true,
) {
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val textFieldColors = OutlinedTextFieldDefaults.colors().copy(
        disabledTextColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
        disabledLabelColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
        disabledContainerColor = OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
//        disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedBorderColor,
    )

    Surface(
        shape = MaterialTheme.shapes.medium,
    ) {
        OutlinedTextField(
            enabled = false,
            value = field.value,
            onValueChange = { newValue ->
//                field.value = newValue
                field.value = TextFieldValue(items[selectedIndex.value])
            },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
//            border = BorderStroke(1.dp, OutlinedTextFieldDefaults.colors().unfocusedLabelColor),
            colors = textFieldColors,
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded.value = true })
//                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = lineSpacing)

        )

        if ( !enabled ) {
            expanded.value = false
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.background)
        ) {
            items.forEachIndexed { index, nameCategory ->

                DropdownMenuItem(
                    text = {
                        Text(modifier = Modifier.padding(horizontal = 16.dp),
                            text = nameCategory, style = MaterialTheme.typography.bodyMedium)
                    },
                    onClick = {
                        field.value = TextFieldValue(nameCategory)
                        selectedIndex.value = index
                        expanded.value = false
                    }
                )
            }

        }
    }
}


@Composable
private fun ComboBoxButtonIcon(modifier: Modifier,
                               icon: ImageVector,
                               onIconClick: onDismissType,
                               enabled: Boolean,
) {
    val separation = dimensionResource(id = R.dimen.side_separation_2x)
    val iconSize = dimensionResource(id = R.dimen.icon_tiny_size)

    Surface(
        shape = RoundedCornerShape(12.dp),
    ) {
        IconButton(
            onClick = { onIconClick.invoke() },
            enabled = enabled
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                contentDescription = stringResource(id = R.string.content_description_icon),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }

    }
}
