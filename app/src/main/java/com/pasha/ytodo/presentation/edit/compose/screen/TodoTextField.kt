package com.pasha.ytodo.presentation.edit.compose.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pasha.ytodo.R


@Composable
fun TodoTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(
                id = R.dimen.edit_task_todo_text_field_decor_elevation
            )
        ),
        modifier = modifier
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            minLines = 3,
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.item_task_hint_tip),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.tertiary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}