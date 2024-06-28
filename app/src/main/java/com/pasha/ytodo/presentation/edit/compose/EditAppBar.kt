package com.pasha.ytodo.presentation.edit.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pasha.ytodo.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditAppBar(
    navigateBackAction: () -> Unit,
    saveAction: () -> Unit,
    modifier: Modifier = Modifier,
    scrollOffsetCallback: (() -> Int)? = null,
    maxElevation: Dp = 16.dp,
    minElevation: Dp = 0.dp,
    windowInsets: WindowInsets = WindowInsets(top = 0.dp)
) {
    val scrollOffset: Int = scrollOffsetCallback?.invoke() ?: 0

    val elevationRange = maxElevation - minElevation
    val currentElevation = minElevation + (elevationRange * (scrollOffset / 300f))

    val toolbarElevation = currentElevation.coerceIn(minElevation, maxElevation)
    Surface(
        modifier = modifier,
        shadowElevation = toolbarElevation
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = navigateBackAction) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_24),
                        contentDescription = stringResource(
                            id = R.string.edit_task_toolbar_navigation_back_icon_content_desc
                        )
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = saveAction,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.edit_task_toolbar_menu_save_title)
                    )
                }
            },
            windowInsets = windowInsets,
            modifier = Modifier,
            title = {}
        )
    }
}