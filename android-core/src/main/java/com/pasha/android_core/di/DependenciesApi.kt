package com.pasha.android_core.di

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment


inline fun <reified D : Dependencies> Fragment.findDependencies(): D {
    return findDependenciesByClass(D::class.java)
}

@Suppress("UNCHECKED_CAST")
fun <D : Dependencies> Fragment.findDependenciesByClass(clazz: Class<D>): D {
    return parents.firstNotNullOfOrNull { it.depsMap[clazz] } as D?
        ?: throw IllegalStateException("No Dependencies $clazz in ${allParents.joinToString()}")
}

val Fragment.parents: Iterable<HasDependencies> get() = allParents.mapNotNull { it as? HasDependencies }

private val Fragment.allParents: Iterable<Any>
    get() = object : Iterable<Any> {
        override fun iterator() = object : Iterator<Any> {
            private var currentParentFragment: Fragment? = parentFragment
            private var parentActivity: Activity? = activity
            private var parentApplication: Application? = parentActivity?.application

            override fun hasNext(): Boolean {
                return currentParentFragment != null || parentActivity != null || parentApplication != null
            }

            override fun next(): Any {
                currentParentFragment?.let { parent ->
                    currentParentFragment = parent.parentFragment
                    return parent
                }

                parentActivity?.let { parent ->
                    parentActivity = null
                    return parent
                }

                parentApplication?.let { parent ->
                    parentApplication = null
                    return parent
                }

                throw NoSuchElementException()
            }

        }
    }