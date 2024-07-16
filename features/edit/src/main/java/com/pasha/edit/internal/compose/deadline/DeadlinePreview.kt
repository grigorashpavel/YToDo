package com.pasha.edit.internal.compose.deadline

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pasha.edit.internal.compose.theme.TodoTheme
import java.time.LocalDateTime


@Preview(showSystemUi = true)
@Composable
fun DeadlinePreview() {
    TodoTheme {
        var deadline by remember {
            mutableStateOf<LocalDateTime?>(null)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 64.dp)
        ) {
            DeadlinePicker(
                deadline = deadline,
                onDeadlineChanged = { newDeadline ->
                    deadline = newDeadline
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
    }
}