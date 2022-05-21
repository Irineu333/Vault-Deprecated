package com.neo.vault.utils.extension

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

fun DialogFragment.checkToShow(
    manager: FragmentManager,
    tag: String = this.javaClass.simpleName,
): Boolean {
    val fragment = manager.findFragmentByTag(tag)

    if (fragment?.isVisible != true) {
        show(manager, tag)
        return true
    }

    return false
}