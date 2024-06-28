package com.pasha.ytodo.presentation.edit.compose.priority

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.presentation.edit.compose.theme.TodoTheme


@Preview(showSystemUi = true)
@Composable
private fun PriorityMenuPreview() {
    TodoTheme(useDynamicColors = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 64.dp)
        ) {
            var basePriority by remember {
                mutableStateOf(TaskPriority.NORMAL)
            }
            PriorityDropdownMenu(
                baseOption = basePriority,
                onPriorityChanged = { newPriority ->
                    basePriority = newPriority
                }
            )
        }
    }
}