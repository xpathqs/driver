package org.xpathqs.driver.navigation.base

import org.xpathqs.driver.navigation.NavWrapper

interface INavigator {
    fun navigate(from: NavWrapper, to: NavWrapper)
    val currentPage: INavigableDetermination
}