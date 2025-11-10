package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.onStringType
import com.budoxr.manifestations.ui.theme.ManifestationsTheme

@Composable
fun ComboBox(
    items: Array<String>,
    label: String,
    field: MutableState<TextFieldValue>,
    enabled: Boolean = true,
    onSelectedItem: onIntType,
    modifier: Modifier
) {

    val expanded = remember { mutableStateOf( false ) }
    val selectedIndex = remember { mutableStateOf( 0 ) }

    ComboBoxText(
        modifier = modifier,
        field = field,
        items = items,
        label = label,
        selectedIndex = selectedIndex,
        expanded = expanded,
        enabled = enabled,
        onSelectedItem = onSelectedItem,
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
private fun ComboBoxText(
    modifier: Modifier,
     field: MutableState<TextFieldValue>,
     items: Array<String>,
     label: String,
     selectedIndex: MutableState<Int>,
     expanded: MutableState<Boolean>,
     enabled: Boolean = true,
     onSelectedItem: onIntType,
) {
    val iconSize = dimensionResource(id = R.dimen.icon_tiny_size)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val textFieldColors = OutlinedTextFieldDefaults.colors().copy(
        disabledTextColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
        disabledLabelColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
        disabledContainerColor = OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
//        disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedBorderColor,
    )
    var anyPoint by remember { mutableStateOf<Long?>(null) }


    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = field.value,
            onValueChange = {},
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .size(iconSize),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = stringResource(id = R.string.content_description_icon)
                )

            },
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(anyPoint) {
                    awaitEachGesture {
                        // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                        // in the Initial pass to observe events before the text field consumes them
                        // in the Main pass.
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            expanded.value = true
                        }
                    }
                }
                .padding(bottom = lineSpacing)

        )

        if ( !enabled ) {
            expanded.value = false
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
//                .fillMaxWidth()
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
                        onSelectedItem.invoke(index)
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


@Composable
@Preview(showBackground = true)
private fun ComboBoxPreview() {
    val items = arrayOf("Item 1", "Item 2", "Item 3")
    val label = "List of Items"
    val field = remember { mutableStateOf(TextFieldValue()) }

    ManifestationsTheme {
        ComboBox(
            items = items,
            label = label,
            field = field,
            enabled = true,
            onSelectedItem = { _ ->},
            modifier = Modifier
        )
    }

}