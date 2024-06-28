package com.pasha.ytodo.presentation.edit.compose.priority

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.pasha.ytodo.R
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.presentation.edit.compose.share.OptionPickerTextField


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PriorityDropdownMenu(
    baseOption: TaskPriority,
    onPriorityChanged: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier,
    options: List<TaskPriority> = getPriorityOptions()
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OptionPickerTextField(
            value = stringResource(id = baseOption.stringId),
            onValueChange = {},
            textStyle = TextStyle(color = getOptionColor(baseOption)),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = {
                Text(
                    text = stringResource(
                        id = R.string.edit_task_priority_dropdown_menu_label
                    )
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(vertical = 8.dp)
        )
        DropdownMenu(
            expanded = expanded,
            modifier = Modifier.exposedDropdownSize(),
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = option.stringId),
                            color = getOptionColor(option)
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
        TaskPriority.LOW -> colorResource(id = R.color.color_gray)
        TaskPriority.NORMAL -> Color.Unspecified
        TaskPriority.HIGH -> colorResource(id = R.color.color_red)
    }
}

@Composable
private fun getPriorityOptions(): List<TaskPriority> = TaskPriority.entries
