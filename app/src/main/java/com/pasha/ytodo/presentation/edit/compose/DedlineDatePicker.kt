package com.pasha.ytodo.presentation.edit.compose


import android.icu.util.Calendar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import com.pasha.ytodo.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * **!!В реализации используется side-effect!!**
 *
 * [LaunchedEffect] от [DatePickerState.selectedDateMillis].
 *
 * [DatePicker] не имеет обратного вызова при изменении даты.
 * Также он не предоставляет состояния с подпиской.
 * Если необходим обратный вызов, то используется [DatePickerDialog],
 * который не отображается корректно в Portrait Landscape.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DeadlineDatePicker(
    isDialogOpened: Boolean,
    onDateChanged: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    datePickerState: DatePickerState = rememberTodoDatePickerState()
) {
    LaunchedEffect(key1 = datePickerState.selectedDateMillis) {
        val timePointMillis = datePickerState.selectedDateMillis
        if (timePointMillis != null) {
            val timePoint = convertMillisToLocalDateTime(timePointMillis)
            onDateChanged(timePoint)
        }
    }

    AnimatedVisibility(
        visible = isDialogOpened,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(modifier = modifier) {
            HorizontalDivider()
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                modifier = modifier.clip(
                    RoundedCornerShape(
                        dimensionResource(id = R.dimen.deadline_picker_corner_radius)
                    )
                )
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun rememberTodoDatePickerState(): DatePickerState {
    return rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = getYearFromMills(System.currentTimeMillis())

                return year >= currentYear
            }

            private fun getYearFromMills(mills: Long): Int {
                val calendar = Calendar.getInstance().also { calendar ->
                    calendar.timeInMillis = mills
                }

                return calendar.get(Calendar.YEAR)
            }
        }
    )
}

private fun convertMillisToLocalDateTime(millis: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
}
