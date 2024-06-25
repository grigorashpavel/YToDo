package com.pasha.ytodo.presentation.edit.compose

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.pasha.ytodo.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditAppBar(
    navigateBackAction: () -> Unit,
    saveAction: () -> Unit,
    modifier: Modifier = Modifier,
    isScrollInInitialState: (() -> Boolean)? = null
) {
    TopAppBar(navigationIcon = {
        IconButton(onClick = navigateBackAction) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_24),
                contentDescription = stringResource(
                    id = R.string.edit_task_toolbar_navigation_back_icon_content_desc
                )
            )
        }
    }, actions = {
        TextButton(onClick = saveAction) {
            Text(
                text = stringResource(id = R.string.edit_task_toolbar_menu_save_title)
            )
        }
    }, modifier = modifier, title = {})
}