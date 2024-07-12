package com.pasha.android_core.di

import dagger.MapKey
import kotlin.reflect.KClass


@MapKey
annotation class DependenciesKey(val value: KClass<out Dependencies>)