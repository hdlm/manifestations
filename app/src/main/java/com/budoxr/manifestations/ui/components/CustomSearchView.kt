package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.ui.theme.gray
import com.budoxr.manifestations.ui.theme.grayDark
import com.budoxr.manifestations.ui.theme.grayLight
import com.budoxr.manifestations.R
import com.budoxr.manifestations.ui.theme.ManifestationsTheme

@Composable
fun CustomSearchView(
    search: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val isVisible by remember {
        derivedStateOf {
            text.isNotBlank()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(grayLight)

    ) {
        TextField(value = search,
            onValueChange = {
                text = it
                onValueChange.invoke(text)
            },
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = grayDark,
                unfocusedTextColor = grayDark,
                focusedContainerColor = grayLight,
                unfocusedContainerColor = grayLight,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLeadingIconColor = gray,
                unfocusedLeadingIconColor = gray,
                focusedTrailingIconColor = grayLight,
                unfocusedTrailingIconColor = grayLight,
                focusedPlaceholderColor = grayLight,
                unfocusedPlaceholderColor = grayLight,
            ),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, tint = gray, contentDescription = "") },
            trailingIcon = {
                if (isVisible) {
                    IconButton(onClick = {
                        text = ""
                        onValueChange.invoke(text)
                    }) {
                        Icon(imageVector = Icons.Default.Close, tint = gray, contentDescription = "")
                    }
                }
            },
            placeholder = { Text(text = stringResource(id = R.string.label_search), color = gray) },
            modifier = Modifier.fillMaxWidth()
        )
    }

}


@Preview(showBackground = false)
@Composable
fun CustomSearchPreview() {

    ManifestationsTheme {

        CustomSearchView(search = "", onValueChange = {})

    }

}

