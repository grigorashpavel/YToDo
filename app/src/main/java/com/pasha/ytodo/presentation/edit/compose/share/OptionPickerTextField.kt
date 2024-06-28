package com.pasha.ytodo.presentation.edit.compose.share

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OptionPickerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle.Default,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    }
) {
    BasicTextField(
        value = value,
        textStyle = textStyle,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = modifier,
        visualTransformation = VisualTransformation.None,
        interactionSource = interactionSource,
        readOnly = true,
        singleLine = true,
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = value,
            enabled = enabled,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            label = label,
            singleLine = true,
            shape = RectangleShape,
            interactionSource = interactionSource,
            contentPadding = contentPadding,
            colors = colors,
        )
    }
}