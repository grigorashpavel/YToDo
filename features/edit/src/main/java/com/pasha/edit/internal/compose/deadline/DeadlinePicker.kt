package com.pasha.edit.internal.compose.deadline

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.pasha.edit.internal.compose.share.OptionPickerTextField
import java.time.LocalDateTime
import com.pasha.core_ui.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DeadlinePicker(
    deadline: LocalDateTime?,
    onDeadlineChanged: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDeadlineEnabled by rememberSaveable { mutableStateOf(deadline != null) }
    var isDialogOpened by rememberSaveable { mutableStateOf(false) }

    val optionPickerInteractionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .indication(
                    optionPickerInteractionSource,
                    rememberRipple(color = colorResource(id = R.color.color_blue))
                )
        ) {
            LaunchedEffect(key1 = Unit) {
                optionPickerInteractionSource.interactions.collect { interaction ->
                    val isClickedReleased = interaction is PressInteraction.Release
                    if (isClickedReleased && isDeadlineEnabled) {
                        isDialogOpened = !isDialogOpened

                        focusManager.clearFocus()
                    }
                }
            }

            OptionPickerTextField(
                value = deadline?.let { date ->
                    val year = date.year
                    val month = date.month.value - 1
                    val day = date.dayOfMonth

                    getFormattedDeadline(day = day, month = month, year = year)
                } ?: " ",
                label = {
                    Text(
                        text = stringResource(id = R.string.edit_task_dedline_picker_label_text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                onValueChange = {},
                colors = getOptionTextFieldColors(),
                enabled = isDeadlineEnabled,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f),
                interactionSource = optionPickerInteractionSource,
                textStyle = TextStyle(color = colorResource(id = R.color.color_blue))
            )

            Switch(
                checked = isDeadlineEnabled,
                colors = SwitchDefaults.colors(
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent,
                    checkedThumbColor = colorResource(id = R.color.color_blue),
                    uncheckedThumbColor = colorResource(id = R.color.color_blue).copy(alpha = 0.5f),
                    checkedTrackColor = colorResource(id = R.color.color_blue).copy(alpha = 0.4f),
                    uncheckedTrackColor = colorResource(id = R.color.color_blue).copy(alpha = 0.2f),
                ),
                onCheckedChange = { deadlineState ->
                    isDialogOpened = deadlineState

                    isDeadlineEnabled = deadlineState
                    if (deadlineState == false) onDeadlineChanged(null)
                }
            )
        }

        DeadlineDatePicker(
            isDialogOpened = isDialogOpened,
            onDateChanged = { date ->
                onDeadlineChanged(date)

                isDialogOpened = false
            },
            modifier = Modifier
        )
    }
}

@Composable
private fun getFormattedDeadline(day: Int, month: Int, year: Int): String {
    val months = stringArrayResource(id = R.array.deadline_formatted_months)
    val formattedMonth = months[month]

    return "$day ${formattedMonth.lowercase()} $year"
}

@Composable
private fun getOptionTextFieldColors() = TextFieldDefaults.colors(
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent
)
