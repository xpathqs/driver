package org.xpathqs.driver.navigation.base

import org.xpathqs.driver.navigation.annotations.UI.Nav.Order.Companion.DEFAULT

interface INavigable {
    fun initNavigation() {}
    fun addNavigation(to: INavigable, weight: Int = DEFAULT, action: (()->Unit)? = null)

    val navOrder: Int
        get() = DEFAULT

    fun navigate() {}
}