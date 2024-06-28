package com.pasha.ytodo.presentation.edit.compose.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pasha.ytodo.R
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.presentation.edit.compose.deadline.DeadlinePicker
import com.pasha.ytodo.presentation.edit.compose.priority.PriorityDropdownMenu
import java.time.LocalDateTime


@Composable
fun EditScreenContent(
    taskPart: ChangeableTaskPart,
    onTextChangedCallback: (String) -> Unit,
    onPriorityChangedCallback: (TaskPriority) -> Unit,
    onDeadlineChangedCallback: (LocalDateTime?) -> Unit,
    onDeleteAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        TodoTextField(
            text = taskPart.text,
            onTextChanged = { newText ->
                onTextChangedCallback(newText)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        )

        PriorityDropdownMenu(
            baseOption = taskPart.priority,
            onPriorityChanged = { newPriority ->
                onPriorityChangedCallback(newPriority)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        )

        DeadlinePicker(
            deadline = taskPart.deadline,
            onDeadlineChanged = { newDeadline ->
                onDeadlineChangedCallback(newDeadline)
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = 24.dp)
        )

        Button(
            onClick = onDeleteAction,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = colorResource(id = R.color.color_red)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete_24),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(id = R.string.delete_text))
            }
        }
    }
}
