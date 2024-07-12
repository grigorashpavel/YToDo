package com.pasha.android_core.navigation

import android.os.Bundle
import androidx.navigation.NavOptions

data class NavCommand(
    val action: Int,
    val args: Bundle? = null,
    val navOptions: NavOptions? = null
)
