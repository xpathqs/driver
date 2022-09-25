package org.xpathqs.driver.navigation.base

import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.UI.Nav.Order.Companion.DEFAULT
import org.xpathqs.driver.navigation.annotations.UI.Nav.PathTo.Companion.UNDEF

interface INavigable {
    fun initNavigation() {}
    fun addNavigation(
        to: INavigable,
        weight: Int = DEFAULT,
        selfState: Int = UI.Visibility.UNDEF_STATE,
        state: Int = UI.Visibility.UNDEF_STATE,
        action: (()->Unit)? = null
    )

    val navOrder: Int
        get() = DEFAULT

    fun navigate(state: Int = UNDEF) {}
}