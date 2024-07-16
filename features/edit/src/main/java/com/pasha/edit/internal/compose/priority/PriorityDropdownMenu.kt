package com.pasha.edit.internal.compose.priority

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.pasha.core_ui.R
import com.pasha.domain.entities.TaskPriority
import com.pasha.edit.internal.compose.share.OptionPickerTextField
import kotlinx.coroutines.flow.collectLatest


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PriorityDropdownMenu(
    baseOption: TaskPriority,
    onPriorityChanged: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier,
    options: List<TaskPriority> = getPriorityOptions()
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = expanded) {
        focusManager.clearFocus()
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
        modifier = modifier
    ) {
        OptionPickerTextField(
            value = getOptionText(priority = baseOption),
            onValueChange = {},
            textStyle = TextStyle(color = getOptionColor(baseOption)),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .indication(
                    interactionSource,
                    rememberRipple(color = colorResource(id = R.color.color_blue))
                ),
            label = {
                Text(
                    text = stringResource(
                        id = R.string.edit_task_priority_dropdown_menu_label
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            interactionSource = interactionSource,
            contentPadding = PaddingValues(vertical = 8.dp)
        )
        DropdownMenu(
            expanded = expanded,
            modifier = Modifier.exposedDropdownSize(),
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = getOptionText(priority = option),
                            color = getOptionColor(option),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onPriorityChanged(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun getOptionColor(priority: TaskPriority): Color {
    return when (priority) {
        TaskPriority.LOW -> MaterialTheme.colorScheme.tertiary
        TaskPriority.NORMAL -> MaterialTheme.colorScheme.primary
        TaskPriority.HIGH -> colorResource(id = com.pasha.core_ui.R.color.color_red)
    }
}

@Composable
private fun getOptionText(priority: TaskPriority): String {
    return when (priority) {
        TaskPriority.LOW -> stringResource(id = com.pasha.core_ui.R.string.task_priority_low_text)
        TaskPriority.NORMAL -> stringResource(id = com.pasha.core_ui.R.string.task_priority_normal_text)
        TaskPriority.HIGH -> stringResource(id = com.pasha.core_ui.R.string.task_priority_high_text)
    }
}

@Composable
private fun getPriorityOptions(): List<TaskPriority> = TaskPriority.entries
