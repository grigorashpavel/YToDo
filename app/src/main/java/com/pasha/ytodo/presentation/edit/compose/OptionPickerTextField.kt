package com.pasha.ytodo.presentation.edit.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pasha.ytodo.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OptionPickerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle.Default,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        textStyle = textStyle,
        onValueChange = onValueChange,
        modifier = modifier,
        visualTransformation = VisualTransformation.None,
        interactionSource = interactionSource,
        enabled = true,
        readOnly = true,
        singleLine = true,
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = value,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            label = label,
            singleLine = true,
            enabled = true,
            shape = RectangleShape,
            interactionSource = interactionSource,
            contentPadding = contentPadding,
            colors = colors,
        )
    }
}