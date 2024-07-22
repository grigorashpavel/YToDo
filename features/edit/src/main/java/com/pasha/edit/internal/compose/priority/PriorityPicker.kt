package com.pasha.edit.internal.compose.priority

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.pasha.core_ui.R
import com.pasha.domain.entities.TaskPriority
import com.pasha.edit.internal.compose.share.OptionPickerTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@Composable
fun PriorityPicker(
    baseOption: TaskPriority,
    onPriorityChanged: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier,
    options: List<TaskPriority> = getPriorityOptions()
) {
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            focusManager.clearFocus()

            if (interaction is PressInteraction.Release) {
                isBottomSheetVisible = !isBottomSheetVisible
            }
        }
    }

    var isHighlighted by rememberSaveable { mutableStateOf(false) }
    val backgroundColorWithAnimation by animateColorAsState(
        targetValue = when {
            isHighlighted && baseOption == TaskPriority.HIGH -> {
                colorResource(id = R.color.color_red).copy(.2f)
            }

            isHighlighted && baseOption == TaskPriority.LOW -> {
                colorResource(id = R.color.color_blue).copy(.2f)
            }

            else -> Color.Transparent
        },
        animationSpec = tween(400, easing = LinearOutSlowInEasing)
    )

    OptionPickerTextField(
        value = getOptionText(priority = baseOption),
        onValueChange = {},
        textStyle = TextStyle(color = getOptionColor(baseOption)),
        modifier = modifier
            .fillMaxWidth()
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
            focusedContainerColor = backgroundColorWithAnimation,
            unfocusedContainerColor = backgroundColorWithAnimation
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(vertical = 8.dp)
    )

    if (isBottomSheetVisible) {
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        PriorityBottomSheet(
            onChangeVisibility = { newVisibility ->
                isBottomSheetVisible = newVisibility
            },
            options = options,
            onPriorityChanged = { newPriority ->
                isHighlighted = newPriority != TaskPriority.NORMAL

                onPriorityChanged(newPriority)
            },
            modifier = Modifier.padding(bottom = bottomPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityBottomSheet(
    onChangeVisibility: (Boolean) -> Unit,
    options: List<TaskPriority>,
    onPriorityChanged: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                onChangeVisibility(false)
            }
        },
        modifier = modifier.fillMaxWidth(),
        sheetState = sheetState,
        containerColor = colorResource(id = R.color.back_primary)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.edit_task_priority_dropdown_menu_label),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            options.forEach { option ->
                CompositionLocalProvider(LocalRippleTheme provides RipplePriorityTheme(option)) {
                    Button(
                        onClick = {
                            onPriorityChanged(option)

                            coroutineScope.launch {
                                sheetState.hide()
                                onChangeVisibility(false)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = getOptionText(priority = option),
                            color = getOptionColor(priority = option),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

private class RipplePriorityTheme(private val taskPriority: TaskPriority) : RippleTheme {
    @Composable
    override fun defaultColor(): Color = when (taskPriority) {
        TaskPriority.LOW -> colorResource(id = R.color.color_blue)
        TaskPriority.NORMAL -> MaterialTheme.colorScheme.tertiary.copy(.5f)
        TaskPriority.HIGH -> colorResource(id = R.color.color_red)
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleTheme.defaultRippleAlpha(
            contentColor = when (taskPriority) {
                TaskPriority.LOW -> colorResource(id = R.color.color_blue)
                TaskPriority.NORMAL -> MaterialTheme.colorScheme.tertiary.copy(.5f)
                TaskPriority.HIGH -> colorResource(id = R.color.color_red)
            },
            lightTheme = true
        )

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